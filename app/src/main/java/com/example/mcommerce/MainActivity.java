package com.example.mcommerce;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.security.MessageDigest;

import bolts.AppLink;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends BaseActivity {


    @BindView(R.id.btnShareLink)
    Button btnShareLink;
    @BindView(R.id.btnSharePhoto) Button btnSharePhoto;
    @BindView(R.id.btnShareVideo) Button btnShareVideo;
    @BindView(R.id.image)
    ImageView mImageView;

    CallbackManager callbackManager;
    ShareDialog shareDialog;


    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mImageView.setImageBitmap(bitmap);
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
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

    }

    @OnClick({R.id.btnShareLink, R.id.btnSharePhoto, R.id.btnShareVideo})
    void btnActions(View view){
        switch (view.getId()){
            case R.id.btnShareLink:
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
                        makeToast(error.toString(), Toast.LENGTH_LONG);
                    }
                });

                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setQuote("This is default Link")
                        .setContentUrl(Uri.parse("https://youtoube.com"))
                        .build();

                if (shareDialog.canShow(ShareLinkContent.class)){
                    shareDialog.show(linkContent, ShareDialog.Mode.WEB);
                }

                break;
            case R.id.btnSharePhoto:

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
                        .load("http://phrt.com.au/virox_mcommerce/public/uploads/phone3.jpg")
                        .into(target);

                break;
            case R.id.btnShareVideo:
                break;
            default:
        }
    }


    private void printKeyHash() {
        try{
            PackageInfo info = getPackageManager().getPackageInfo("com.example.mcommerce",
                    PackageManager.GET_SIGNATURES);

            for (Signature signature: info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));

            }

        } catch (Exception e){
            Log.e("KeyHash",e.getMessage());

        }
    }
}
