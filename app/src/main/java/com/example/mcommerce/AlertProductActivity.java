package com.example.mcommerce;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AlertProductActivity extends AppCompatActivity {

    protected String product_code;
    protected String product_message;
    protected String product_photo;
    protected String product_id;
    protected String username;
    protected String product_category;
    protected String redeem_without_cash;
    protected String redeem_with_cash;
    protected String product_shop_name;


    @BindView(R.id.img_left)
    ImageView mImageLogo;
    @BindView(R.id.txt_center)
    TextView mTextTitle;
    @BindView(R.id.txt_right) TextView mTextRight;
    @BindView(R.id.ic_back) ImageView mImageBack;

    @BindView(R.id.product_photo) ImageView mImageProductPhoto;
    @BindView(R.id.product_shop_name) TextView mTextProductShopName;
    @BindView(R.id.product_code) TextView mTextProductCode;
    @BindView(R.id.product_description) TextView mTextProductDescription;
    @BindView(R.id.product_point_redeem) TextView mTextProductPointRedeem;
    @BindView(R.id.product_point_with_cash) TextView mTextProductPointWithCash;
    @BindView(R.id.product_message) TextView mTextProductMessage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_product);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        String product_name = bundle.getString("product_name");
        product_code = bundle.getString("product_code");
        String product_description = bundle.getString("product_description");
        product_message = bundle.getString("product_message");
        product_photo = bundle.getString("product_photo");
        product_id = bundle.getString("product_id");
        username = bundle.getString("username");

        product_category = bundle.getString("product_category");
        redeem_without_cash = bundle.getString("redeem_without_cash");
        redeem_with_cash = bundle.getString("redeem_with_cash");
        product_shop_name = bundle.getString("product_shop_name");

        mTextTitle.setText(product_name);
        Picasso.with(this).load(product_photo).fit().centerCrop().placeholder(R.drawable.ic_no_image).into(mImageProductPhoto);
        mTextProductShopName.setText(product_shop_name);
        mTextProductCode.setText(product_code);
        mTextProductDescription.setText(product_description);
        mTextProductPointRedeem.setText(redeem_without_cash);
        mTextProductPointWithCash.setText(redeem_with_cash);
        mTextProductMessage.setText(product_message);

    }


    @OnClick({R.id.ic_back})
    void btnActions(View v){
        switch (v.getId()){
            case R.id.ic_back:
                finish();
                break;
                default:
        }
    }
}
