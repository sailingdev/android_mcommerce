package com.example.mcommerce;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mcommerce.adapters.CategorySpinnerAdapter;
import com.example.mcommerce.adapters.ProductGridAdapter;
import com.example.mcommerce.api.ApiClient;
import com.example.mcommerce.common.GlobalConst;
import com.example.mcommerce.models.Category;
import com.example.mcommerce.models.Product;
import com.example.mcommerce.models.response.ResCategories;
import com.example.mcommerce.models.response.ResProducts;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsActivity extends BaseActivity implements ProductGridAdapter.OnProductItemClickListener, AdapterView.OnItemSelectedListener {

    public static String PRODUCT_ITEM = "product_item";



    @BindView(R.id.product_list)
    GridView mGridProducts;

    @BindView(R.id.img_left) ImageView mImageLogo;
    @BindView(R.id.txt_center) TextView mTextTitle;
    @BindView(R.id.txt_right) TextView mTextRight;
    @BindView(R.id.ic_back) ImageView mImageBack;
    @BindView(R.id.btn_sort_up) ImageView mImageSortUpBtn;
    @BindView(R.id.spinner_category)
    Spinner mSpinnerCategory;


    ProductGridAdapter productGridAdapter = null;
    private String shop_id;
    private String shop_name;

    private boolean isSortUp = false;
    CategorySpinnerAdapter categorySpinnerAdapter = null;
    List<Product> mListProduct = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        ButterKnife.bind(this);
        shop_id = getIntent().getExtras().getString(ShopsActivity.SHOP_ID);
        shop_name = getIntent().getExtras().getString(ShopsActivity.SHOP_NAME);
        getProductCategories();
        getProductList();
        productGridAdapter = new ProductGridAdapter(this, this);
        mGridProducts.setAdapter(productGridAdapter);
        categorySpinnerAdapter = new CategorySpinnerAdapter(this);
        mSpinnerCategory.setAdapter(categorySpinnerAdapter);
        mSpinnerCategory.setOnItemSelectedListener(this);

        initView();
    }

    private void initView(){
        mTextTitle.setVisibility(View.VISIBLE);
        mTextTitle.setText(shop_name);
    }

    @OnClick({R.id.ic_back, R.id.btn_sort_up})
    void btnActions(View v){
        switch (v.getId()){
            case R.id.ic_back:
                finish();
                break;
            case R.id.btn_sort_up:
                List<Product> mListProducts = productGridAdapter.getAllProducts();

                if (isSortUp){
                    Collections.sort(mListProducts, StringAscComparator);
                } else {
                    Collections.sort(mListProducts, StringDescComparator);
                }
                isSortUp = !isSortUp;
                productGridAdapter.addAllProducts(mListProducts);
                break;
                default:
        }
    }


    @Override
    public void onProductItemClicked(int pos) {
        Product product = productGridAdapter.getItem(pos);
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra(PRODUCT_ITEM, product);
        startActivityForResult(intent, 303);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mTextRight.setText(GlobalConst.total_point + GlobalConst.PTS);
    }



    public void getProductCategories(){
        //showProgressDialog("waiting");
        Call<ResCategories> call = ApiClient.getApiClient(this).getProductCategories();
        call.enqueue(new Callback<ResCategories>() {
            @Override
            public void onResponse(Call<ResCategories> call, Response<ResCategories> response) {
                //hideProgressDialog();

                ResCategories result = response.body();
                if (result != null){
                    if (result.errorCode == 0){
                        ResCategories.Categories results = result.results;
                        List<Category> mListCategories = results.categories;
                        categorySpinnerAdapter.addAllCategory(mListCategories);
                    }else {
                        Toast.makeText(getBaseContext(), result.errMsg, Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(getBaseContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResCategories> call, Throwable t) {
                //hideProgressDialog();
                Toast.makeText(getBaseContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
            }
        });
    }









    public void getProductList(){
        showProgressDialog("loading...");
        String user_id = String.valueOf(mPrefs.getUserID());
        String shop_id = this.shop_id;
        String promotion_status = "0";

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id);
        map.put("shop_id", shop_id);
        map.put("promotion_status", promotion_status);

        Call<ResProducts> call = ApiClient.getApiClient(this).getProductsAPI(map);
        call.enqueue(new Callback<ResProducts>() {
            @Override
            public void onResponse(Call<ResProducts> call, Response<ResProducts> response) {
                hideProgressDialog();
                ResProducts result = response.body();

                if (result != null){
                    if (result.errorCode == 0){
                        ResProducts.Products results = result.results;
                        mListProduct = results.products;
                        productGridAdapter.addAllProducts(results.products);
                        mTextRight.setVisibility(View.VISIBLE);
                        GlobalConst.total_point = results.total_point;
                        mTextRight.setText(results.total_point + GlobalConst.PTS);
                    }else {
                        Toast.makeText(getBaseContext(), result.errMsg, Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(getBaseContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResProducts> call, Throwable t) {
                hideProgressDialog();
                Toast.makeText(getBaseContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();

            }
        });
    }



    // Comparator for Ascending Order
    public static Comparator<Product> StringAscComparator = new Comparator<Product>() {

        @Override
        public int compare(Product o1, Product o2) {
            return o1.product_redeem_with_out_cash.compareTo(o2.product_redeem_with_out_cash);
        }
    };

    //Comparator for Descending Order
    public static Comparator<Product> StringDescComparator = new Comparator<Product>() {
        @Override
        public int compare(Product o1, Product o2) {
            return o2.product_redeem_with_out_cash.compareTo(o1.product_redeem_with_out_cash);
        }
    };


    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.spinner_category:
                Category category = categorySpinnerAdapter.getItem(position);
                List<Product> filterList = new ArrayList<>();
                for (int i = 0; i < mListProduct.size(); i++) {
                    if ((mListProduct.get(i).product_category.toUpperCase()).contains(category.product_category.toUpperCase())) {
                        filterList.add(mListProduct.get(i));
                    }
                }
                productGridAdapter.addAllProducts(filterList);
                break;
                default:
        }
    }



}
