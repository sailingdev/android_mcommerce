package com.example.mcommerce;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mcommerce.adapters.ShopListAdapter;
import com.example.mcommerce.api.ApiClient;
import com.example.mcommerce.models.Shop;
import com.example.mcommerce.models.response.ResShop;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopsActivity extends BaseActivity implements ShopListAdapter.OnShopItemClickListener {

    public static String SHOP_ID = "shop_id";
    public static String SHOP_NAME = "shop_name";

    @BindView(R.id.listview_shops)
    ListView mListShops;

    @BindView(R.id.img_left)
    ImageView mImageLogo;
    @BindView(R.id.txt_center)
    TextView mTextTitle;
    @BindView(R.id.txt_right) TextView mTextRight;

    private ShopListAdapter shopListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);
        ButterKnife.bind(this);
        mTextTitle.setText(getResources().getString(R.string.shops));
        mTextTitle.setVisibility(View.VISIBLE);
        getShopList();
        shopListAdapter = new ShopListAdapter(this, this);
        mListShops.setAdapter(shopListAdapter);

    }


    public void getShopList(){
        showProgressDialog("loading...");

        String user_id = String.valueOf(mPrefs.getUserID());

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id);

        Call<ResShop> call = ApiClient.getApiClient(this).getShopsAPI(map);
        call.enqueue(new Callback<ResShop>() {
            @Override
            public void onResponse(Call<ResShop> call, Response<ResShop> response) {
                hideProgressDialog();
                ResShop result = response.body();

                if (result != null){
                    if (result.errorCode == 0){
                        ResShop.Shops results = result.results;
                        shopListAdapter.addAllShops(results.shops);

                    }else {
                        Toast.makeText(getBaseContext(), result.errMsg, Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(getBaseContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResShop> call, Throwable t) {
                hideProgressDialog();
                Toast.makeText(getBaseContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();

            }
        });
    }


    @Override
    public void onShopItemClicked(int pos) {
        String promotion = getIntent().getExtras().getString("promotion");
        Shop shop = shopListAdapter.getItem(pos);
        if (promotion.equals("0")){
            Intent intent1 = new Intent(this, ProductsActivity.class);
            intent1.putExtra(SHOP_NAME, shop.shop_name);
            intent1.putExtra(SHOP_ID, shop.shop_id);
            startActivity(intent1);
        }else {
            Intent intent1 = new Intent(this, ProductMotionActivity.class);
            intent1.putExtra(SHOP_NAME, shop.shop_name);
            intent1.putExtra(SHOP_ID, shop.shop_id);
            startActivity(intent1);
        }

    }



    @OnClick({R.id.ic_back})
    void btnActions(View v){
        switch (v.getId()){
            case R.id.ic_back:
                finish();
                break;
        }
    }
}
