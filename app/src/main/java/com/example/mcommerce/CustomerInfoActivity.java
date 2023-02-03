package com.example.mcommerce;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.mcommerce.common.GlobalConst;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerInfoActivity extends BaseActivity {


    @BindView(R.id.txt_username) TextView mTextUsername;
    @BindView(R.id.txt_email) TextView mTextEmail;
    @BindView(R.id.txt_phonenumber) TextView mTextPhonenumber;
    @BindView(R.id.txt_referal_point) TextView mTextReferalPoint;
    @BindView(R.id.txt_purchase_point) TextView mTextPurchasePoint;
    @BindView(R.id.txt_earn_point) TextView mTextEarnPoint;
    @BindView(R.id.user_photo)
    CircleImageView mCircleUserPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_info);
        ButterKnife.bind(this);
        mTextUsername.setText(mPrefs.getUsername());
        mTextPhonenumber.setText(mPrefs.getPhoneNumber());
        mTextEmail.setText(mPrefs.getUserEmail());
        mTextReferalPoint.setText(mPrefs.getReferalPoint() + GlobalConst.PTS);
        mTextEarnPoint.setText(mPrefs.getEarnPoint() + GlobalConst.PTS);
        mTextPurchasePoint.setText(mPrefs.getPurchasePoint() + GlobalConst.PTS);


        Picasso.with(getApplicationContext()).load(mPrefs.getServerUrl() + GlobalConst.HOST_USRE_PHOTO + mPrefs.getUserPhoto())
                .fit().centerCrop()
                .placeholder(R.drawable.back_white_round)
                .into(mCircleUserPhoto);

        Log.e("==========", mPrefs.getServerUrl() + GlobalConst.HOST_USRE_PHOTO + mPrefs.getUserPhoto());


    }


    @OnClick({R.id.btn_logout, R.id.ic_back})
    void actionBtns(View v){
        switch (v.getId()){
            case R.id.btn_logout:
                logout();
                break;
            case R.id.ic_back:
                finish();
                break;
                default:
        }
    }


    private void logout(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(CustomerInfoActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("Confirm!");
        dialog.setMessage("Are you sure you want to logout?" );
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //Action for "Delete".
                mPrefs.setUserEmail("");
                mPrefs.setUserPassword("");
                mPrefs.setUserID(0);
                Intent intent = new Intent(CustomerInfoActivity.this, LoginActivity.class);
                startActivity(intent);
                setResult(RESULT_OK);
                finish();
            }
        })
                .setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Action for "Cancel".

                    }
                });

        final AlertDialog alert = dialog.create();
        alert.show();
    }
}
