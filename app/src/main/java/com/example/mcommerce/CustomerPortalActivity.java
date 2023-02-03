package com.example.mcommerce;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomerPortalActivity extends BaseActivity{



    @BindView(R.id.img_left)
    ImageView mImageLogo;
    @BindView(R.id.txt_center)
    TextView mTextTitle;
    @BindView(R.id.txt_right) TextView mTextRight;
    @BindView(R.id.ic_back) ImageView mImageBack;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_portal);
        ButterKnife.bind(this);
        initView();
    }


    private void initView(){
        mTextTitle.setVisibility(View.VISIBLE);
        mTextTitle.setText("Customer Portal");
        mTextRight.setVisibility(View.GONE);
        mImageBack.setVisibility(View.GONE);
    }


    @OnClick({R.id.btn_customer_info, R.id.btn_product, R.id.btn_product_motion, R.id.btn_summary, R.id.btn_alert, R.id.btn_cashier})
    void actionBtns(View view){
        switch (view.getId()){
            case R.id.btn_customer_info:
                Intent intent = new Intent(this, CustomerInfoActivity.class);
                startActivityForResult(intent, 100);
                break;
            case R.id.btn_product:
                Intent intent1 = new Intent(this, ShopsActivity.class);
                intent1.putExtra("promotion", "0");
                startActivity(intent1);
                break;
            case R.id.btn_product_motion:
                Intent intent2 = new Intent(this, ShopsActivity.class);
                intent2.putExtra("promotion", "1");
                startActivity(intent2);
                 break;
            case R.id.btn_summary:
                Intent intent3 = new Intent(this, SummaryActivity.class);
                startActivity(intent3);
                 break;
            case R.id.btn_alert:
                Intent intent4 = new Intent(this, AlertActivity.class);
                startActivity(intent4);
                break;

            case R.id.btn_cashier:
                Intent intent5 = new Intent(this, QrCodeScannerActivity.class);
                startActivity(intent5);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == 100)
                finish();
        }
    }
}
