package com.example.mcommerce;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mcommerce.api.ApiClient;
import com.example.mcommerce.models.Login;
import com.example.mcommerce.models.User;
import com.example.mcommerce.models.response.ResLogin;
import com.example.mcommerce.utils.ImageUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends BaseActivity {

    @BindView(R.id.edit_username)
    EditText edtUsername;
    @BindView(R.id.edit_email)
    EditText edtEmail;
    @BindView(R.id.edit_birthday) EditText edtBirthday;
    @BindView(R.id.edit_password) EditText edtPassword;
    @BindView(R.id.edit_phonenumber) EditText edtPhonenumber;
    @BindView(R.id.edit_confirm_password) EditText edtConfirmPassword;

    @BindView(R.id.user_photo)
    CircleImageView mCircleImageUserPhoto;
    private String imgPath = "";
    private String fileName = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    //To do//
                    return;
                }
                // Get the Instance ID token//
                String fcm_token = task.getResult().getToken();
                @SuppressLint({"StringFormatInvalid", "LocalSuppress"})
                String msg = getString(R.string.fcm_token, fcm_token);
                mPrefs.setFcmToken(fcm_token);
                Log.e(TAG, msg);
            }
        });

    }

    @OnClick({R.id.img_back, R.id.btn_submit, R.id.user_photo})
    void btnActions(View v){
        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_submit:
                register();
                break;
            case R.id.user_photo:
                CropImage.activity()
                        .start(this);
                default:
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = result.getUri();
                imgPath = selectedImageUri.getPath(); // "/mnt/sdcard/FileName.mp3"
                Bitmap bitmap = ImageUtils.getSafeDecodeBitmap(imgPath, 200);
                mCircleImageUserPhoto.setImageBitmap(bitmap);
                //picProfileCircleImageView.setImageURI(selectedImageUri);
                Toast.makeText(this, "Cropping successful, Picture: " + result.getSampleSize(), Toast.LENGTH_LONG).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }



    public void register(){
        if (!checkEditTexts()) { return; }

        if (imgPath.equals("")){
            makeToast("Please select your photo", Toast.LENGTH_LONG);
            return;
        }


        final User user = new User();
        user.username = edtUsername.getText().toString().trim();
        user.email = edtEmail.getText().toString().trim();
        user.password = edtPassword.getText().toString().trim();
        user.birthday = edtBirthday.getText().toString().trim();
        user.phonenumber = edtPhonenumber.getText().toString().trim();

        RequestBody useranmeInstitute = RequestBody.create(MediaType.parse("multipart/form-data"), user.username);
        RequestBody emailInstitute = RequestBody.create(MediaType.parse("multipart/form-data"), user.email);
        RequestBody passwordInstitute = RequestBody.create(MediaType.parse("multipart/form-data"), user.password);
        RequestBody bithdayInstitute = RequestBody.create(MediaType.parse("multipart/form-data"), user.birthday);
        RequestBody phonenumberInstitute = RequestBody.create(MediaType.parse("multipart/form-data"), user.phonenumber);

        MultipartBody.Part body = null;

        if (!TextUtils.isEmpty(imgPath)){
            File file = new File(imgPath);
            String imageName = file.getName();
            fileName = "image\"; filename=\"" + imageName;
            // creates RequestBody instance from file
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            // MultipartBody.Part is used to send also the actual filename
            body = MultipartBody.Part.createFormData("user_photo", file.getName(), requestFile);
        }


        HashMap<String, RequestBody> requestBodyHashMap = new HashMap<>();
        requestBodyHashMap.put("username", useranmeInstitute);
        requestBodyHashMap.put("password", passwordInstitute);
        requestBodyHashMap.put("email", emailInstitute);
        requestBodyHashMap.put("birthday", bithdayInstitute);
        requestBodyHashMap.put("phonenumber", phonenumberInstitute);

        showCustomProgressDialog("register...");

        Call<ResLogin> call = ApiClient.getApiClient(this).signUpAPI(body, requestBodyHashMap);
        call.enqueue(new Callback<ResLogin>() {
            @Override
            public void onResponse(Call<ResLogin> call, Response<ResLogin> response) {
                hideProgressDialog();
                ResLogin result = response.body();

                if (result != null){
                    if (result.errorCode == 0){
                        Login signInResModel = result.results;

                        mPrefs.setUserID(signInResModel.user.id);
                        mPrefs.setUserEmail(user.email);
                        mPrefs.setUserPassword(user.password);
                        mPrefs.setPhoneNumber(signInResModel.user.phonenumber);
                        mPrefs.setReferalPoint(signInResModel.user.referal_point);
                        mPrefs.setPurchasePoint(signInResModel.user.purchase_point);
                        mPrefs.setEarnPoint(signInResModel.user.earn_point);
                        mPrefs.setUsername(signInResModel.user.username);
                        mPrefs.setUserPhoto(signInResModel.user.user_photo);
                        finish();
                    }else {
                        Toast.makeText(getBaseContext(), result.errMsg, Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(getBaseContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResLogin> call, Throwable t) {
                hideProgressDialog();
                Toast.makeText(getBaseContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();

            }
        });
    }



    private Boolean checkEditTexts(){
        Out<String> out = new Out<String>();
        if (!checkEditText(edtUsername, out)) {
            return false;
        }

        if (!checkEmail(edtEmail, out)) {
            return false;
        }

        if (!checkEditText(edtPassword, out)) {
            return false;
        }

        if (!checkEditText(edtConfirmPassword, out)) {
            return false;
        }

        if (!edtPassword.getText().toString().equals( edtConfirmPassword.getText().toString())) {
            edtConfirmPassword.setError("Password is not matched");
            edtConfirmPassword.requestFocus();
            return false;
        }
        return true;
    }


}
