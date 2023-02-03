package com.example.mcommerce;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mcommerce.utils.AppPrefs;
import com.example.mcommerce.utils.widgets.CustomProgressDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;


public class BaseActivity extends AppCompatActivity {


    public static final String TAG = "BaseActivity";

    public AppPrefs mPrefs;
    private CustomProgressDialog customProgressDialog;
    private ProgressDialog progressDialog;
    private Boolean doubleBackToExitPressedOnce = false;


    public static final int PRODUCTDETAIL_QRPEINT = 301;


    private void backPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
        } else {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }




    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            backPressed();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mPrefs = AppPrefs.create(this);

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

    public BaseActivity(){}

    public void makeToast(String string, int time){
        Toast.makeText(this, string, time).show();
    }


    public void showCustomProgressDialog(String message) {
        customProgressDialog = new CustomProgressDialog(this, message);
        customProgressDialog.setCancelable(false);
        customProgressDialog.show();
    }


    public void showProgressDialog(String msg) {
        showProgressDialog("", msg);
    }

    public void showProgressDialog(String title, String message) {
        progressDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.show();
    }


    public void hideProgressDialog() {
        if (customProgressDialog != null) customProgressDialog.dismiss();
        if (progressDialog != null) progressDialog.dismiss();
    }



    public  class Out<T> {
        private T s = null;

        public void set(T value) {
            this.s = value;
        }

        public T get() {
            return this.s;
        }
    }


    public Boolean checkEditText(EditText edit, Out<String> value) {
        return checkEditText(edit, value, getString(R.string.required_field));
    }

    public Boolean checkEditText(EditText edit, Out<String> value, String error) {

        edit.setError(null);
        String string = edit.getText().toString();
        if (TextUtils.isEmpty(string)) {
            edit.setError(error);
            edit.requestFocus();
            return false;
        }
        value.set(string);
        return true;
    }


    public Boolean checkEmail(EditText edit, Out<String> value) {
        if (!checkEditText(edit, value)) return false;
        String string = value.get();
        Boolean res = !TextUtils.isEmpty(string) && android.util.Patterns.EMAIL_ADDRESS.matcher(string).matches();
        if (!res) {
            edit.setError(getString(R.string.string_msg_email_invalid));
            edit.requestFocus();
            return false;
        }
        return true;
    }



}
