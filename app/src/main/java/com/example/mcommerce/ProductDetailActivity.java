package com.example.mcommerce;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mcommerce.api.ApiClient;
import com.example.mcommerce.common.GlobalConst;
import com.example.mcommerce.models.Product;
import com.example.mcommerce.models.response.ResTotalPoint;
import com.example.mcommerce.utils.DateTimeUtils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends BaseActivity {


    @BindView(R.id.img_left) ImageView mImageLogo;
    @BindView(R.id.txt_center) TextView mTextTitle;
    @BindView(R.id.txt_right) TextView mTextRight;

    @BindView(R.id.carouselView)
    CarouselView carouselView;
    @BindView(R.id.btn_without_cash) TextView mTextBtnWithoutCash;
    @BindView(R.id.product_number) TextView mTextProductNumber;
    @BindView(R.id.txt_product_description) TextView mTextProductDescription;
    @BindView(R.id.txt_shop_name) TextView mTextShopName;
    @BindView(R.id.txt_without_cash) TextView mTextWithoutCash;
    @BindView(R.id.txt_with_cash) TextView mTextWithCash;
    @BindView(R.id.btn_with_cash) TextView mTextBtnWithCash;
    @BindView(R.id.btn_facebook) ImageView mImagefacebookBtn;


    String[] sampleImages = new String[3];

    private static final int MAX_LENGTH = 8;

    Product product;

    String user_id = "";
    String redeem_reference_number = "";
    String product_code = "";
    String point_redeem = "";
    String point_cash = "";
    String username = "";
    String status = "";
    String shop_id = "";

    CallbackManager callbackManager;
    ShareDialog shareDialog;


    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            SharePhoto sharePhoto = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();

            if (ShareDialog.canShow(SharePhotoContent.class)){
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(sharePhoto)
                        .build();
                shareDialog.show(content,ShareDialog.Mode.AUTOMATIC);
            }
        }
        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }
        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        ButterKnife.bind(this);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        product = (Product) getIntent().getExtras().getSerializable(ProductsActivity.PRODUCT_ITEM);
        mTextProductDescription.setText(product.product_description);

        mTextProductNumber.setText(product.product_code);
        mTextShopName.setText(product.shop_name);
        mTextWithoutCash.setText(product.product_redeem_with_out_cash +  GlobalConst.PTS);
        mTextWithCash.setText("+$" + product.point_cash + " | " + product.product_redeem_with_cash + GlobalConst.PTS);

        if (product.product_photo_1 != null)
            sampleImages[0] = product.product_photo_1;
        else
            sampleImages[0] = product.product_photo_1;

        if (product.product_photo_2 != null)
            sampleImages[1] = product.product_photo_2;
        else
            sampleImages[1] = product.product_photo_2;

        if (product.product_photo_3 != null)
            sampleImages[2] = product.product_photo_3;
        else
            sampleImages[2] = product.product_photo_3;

        carouselView.setPageCount(sampleImages.length);
        carouselView.setImageListener(imageListener);

        mTextRight.setText(GlobalConst.total_point + GlobalConst.PTS);
        mTextRight.setVisibility(View.VISIBLE);
        mTextTitle.setText(product.product_name);
        mTextTitle.setVisibility(View.VISIBLE);
    }
    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            Picasso.with(getApplicationContext()).load(sampleImages[position]).fit().centerCrop().placeholder(R.drawable.ic_no_image).into(imageView);
        }
    };


    private Bitmap printQRCode(String textToQR){
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(textToQR, BarcodeFormat.QR_CODE,300,300);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }



    @OnClick({R.id.btn_without_cash, R.id.btn_with_cash, R.id.ic_back, R.id.btn_facebook})
    void btnActions(View v){

        String splite = ",";
        user_id = String.valueOf(mPrefs.getUserID());
        redeem_reference_number = "VS" + DateTimeUtils.getDateString("yyMMdd") + product.product_code;
        product_code = product.product_code;
        point_redeem = "";
        point_cash = "";
        username = mPrefs.getUsername();
        status = "1";
        shop_id = product.shop_id;

        switch (v.getId()){
            case R.id.btn_without_cash:

                int total_point = Integer.parseInt(GlobalConst.total_point);
                int point_without_cash = Integer.parseInt(product.product_redeem_with_out_cash);

                if (total_point < point_without_cash){
                    makeToast("Sorry, your points are not enough.", Toast.LENGTH_LONG);
                    return;
                }else {
                    point_redeem = product.product_redeem_with_out_cash;
                }
                String info = user_id + splite + redeem_reference_number + splite + product_code + splite + point_redeem + splite
                            + point_cash + splite + username + splite + status + splite + shop_id;
                qrCodeGenerate(info);
                break;
            case R.id.btn_with_cash:
                int total_point1 = Integer.parseInt(GlobalConst.total_point);
                int point_with_cash = Integer.parseInt(product.product_redeem_with_cash);

                if (total_point1 < point_with_cash){
                    makeToast("Sorry, your points are not enough.", Toast.LENGTH_LONG);
                    return;
                }else {
                    point_redeem = product.product_redeem_with_cash;
                    point_cash = product.point_cash;
                }

                String info_with_cash = user_id + splite + redeem_reference_number + splite + product_code + splite + point_redeem + splite
                        + point_cash + splite + username + splite + status + splite + shop_id;
                qrCodeGenerate(info_with_cash);
                break;
            case R.id.ic_back:
                finish();
                break;
            case R.id.btn_facebook:
                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        makeToast("success", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onCancel() {
                        makeToast("cancel", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onError(FacebookException error) {

                    }
                });

                Picasso.with(this)
                        .load(sampleImages[0])
                        .into(target);
                break;
                default:
        }
    }


    private void qrCodeGenerate(String info){
        Bitmap QRBit = printQRCode(info);
        if (QRBit == null) {
            Toast.makeText(this, "Unable to generate code!", Toast.LENGTH_SHORT).show();
        } else {
            Intent qRIntent = new Intent(this, ShowPrintQR.class);
            qRIntent.putExtra("bitmap", QRBit);
            startActivityForResult(qRIntent, PRODUCTDETAIL_QRPEINT);
        }
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }


    private void getTotalPoint(){
        String user_id = String.valueOf(mPrefs.getUserID());
        Call<ResTotalPoint> call = ApiClient.getApiClient(this).getTotalPointAPI(user_id);
        call.enqueue(new Callback<ResTotalPoint>() {
            @Override
            public void onResponse(Call<ResTotalPoint> call, Response<ResTotalPoint> response) {
                hideProgressDialog();
                ResTotalPoint result = response.body();

                if (result != null){
                    if (result.errorCode == 0){
                        ResTotalPoint results = result;
                        GlobalConst.total_point = results.total_point;
                        mPrefs.setReferalPoint(results.referal_point);
                        mPrefs.setEarnPoint(results.earn_point);
                        mPrefs.setPurchasePoint(results.purchase_point);

                        mTextRight.setText(results.total_point);
                    }else {
                        Toast.makeText(getBaseContext(), result.errMsg, Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(getBaseContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResTotalPoint> call, Throwable t) {
                hideProgressDialog();
                Toast.makeText(getBaseContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();

            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PRODUCTDETAIL_QRPEINT)
            getTotalPoint();
    }





}
