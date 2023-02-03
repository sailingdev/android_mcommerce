package com.example.mcommerce;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mcommerce.api.ApiClient;
import com.example.mcommerce.models.Login;
import com.example.mcommerce.models.response.ResLogin;
import com.example.mcommerce.utils.AppPrefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.edit_email)
    EditText edtEmail;
    @BindView(R.id.edit_password)
    EditText edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }


    @OnClick({R.id.btn_sign_up, R.id.btn_login, R.id.btn_change_server_url})
    void btnAction(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_up:
                Intent intent = new Intent(this, SignupActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_login:
                signin();
                break;
            case R.id.btn_change_server_url:
                showTextInputDialog();
                break;
            default:
        }
    }


    private void showTextInputDialog() {
        new LovelyTextInputDialog(this, R.style.EditTextTintTheme)
                .setTopColorRes(R.color.colorPrimary)
                .setTitle(R.string.text_input_title)
                .setIcon(R.drawable.ic_assignment_white_36dp)
                .setInputFilter(R.string.text_input_error_message, new LovelyTextInputDialog.TextFilter() {
                    @Override
                    public boolean check(String text) {
                        return !text.matches("");
                    }
                })
                .setInitialInput(mPrefs.getServerUrl())
                .setConfirmButton(android.R.string.ok, text ->
                {
                    ApiClient.apiMainService = null;
                    mPrefs.setServerUrl(text);
                    Toast.makeText(LoginActivity.this, text, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(android.R.string.no, null)
                .configureEditText(editText -> editText.setMaxLines(1))
                .show();
    }


    private void signin() {
        if (!checkEditTexts()) {
            return;
        }

        String fcm_token = mPrefs.getFcmToken();
        final String email = edtEmail.getText().toString().trim();
        final String password = edtPassword.getText().toString().trim();
        showCustomProgressDialog("signIn...");

        Call<ResLogin> call = ApiClient.getApiClient(this).signInAPI(email, password, fcm_token);

        call.enqueue(new Callback<ResLogin>() {
            @Override
            public void onResponse(Call<ResLogin> call, Response<ResLogin> response) {
                hideProgressDialog();
                ResLogin result = response.body();

                if (result != null) {
                    if (result.errorCode == 0) {
                        Login signInResModel = result.results;

                        mPrefs.setUserID(signInResModel.user.id);
                        mPrefs.setUserEmail(email);
                        mPrefs.setUserPassword(password);
                        mPrefs.setPhoneNumber(signInResModel.user.phonenumber);
                        mPrefs.setReferalPoint(signInResModel.user.referal_point);
                        mPrefs.setPurchasePoint(signInResModel.user.purchase_point);
                        mPrefs.setEarnPoint(signInResModel.user.earn_point);
                        mPrefs.setUsername(signInResModel.user.username);
                        startActivity(new Intent(LoginActivity.this, CustomerPortalActivity.class));

                        finish();
                    } else {
                        Toast.makeText(getBaseContext(), result.errMsg, Toast.LENGTH_SHORT).show();
                    }

                } else {
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


    private Boolean checkEditTexts() {
        BaseActivity.Out<String> out = new BaseActivity.Out<String>();


        if (!checkEmail(edtEmail, out)) {
            return false;
        }

        return checkEditText(edtPassword, out);
    }


    @Override
    protected void onResume() {
        super.onResume();
        String email = mPrefs.getUserEmail();
        String password = mPrefs.getUserPassword();
        if ((!email.equals("")) && (!password.equals(""))) {
            edtEmail.setText(email);
            edtPassword.setText(password);
            signin();
        }
    }


}
