package com.example.mcommerce;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mcommerce.adapters.PointRedeemSummaryReportListAdapter;
import com.example.mcommerce.api.ApiClient;
import com.example.mcommerce.models.response.ResShop;
import com.example.mcommerce.models.response.ResTransactions;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SummaryActivity extends BaseActivity {


    @BindView(R.id.list_view_product)
    ListView listView;
    @BindView(R.id.img_left)
    ImageView mImageLogo;
    @BindView(R.id.txt_center)
    TextView mTextTitle;
    @BindView(R.id.txt_right) TextView mTextRight;

    PointRedeemSummaryReportListAdapter reportListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        ButterKnife.bind(this);
        getTransactionsAPI();
        reportListAdapter = new PointRedeemSummaryReportListAdapter(this);
        listView.setAdapter(reportListAdapter);
        initView();
    }


    private void initView(){
        mTextTitle.setVisibility(View.GONE);
        mTextRight.setText("$100");
    }


    @OnClick({R.id.ic_back})
    void btnActions(View v){
        switch (v.getId()){
            case R.id.ic_back:
                finish();
                break;
        }
    }



    public void getTransactionsAPI(){
        showProgressDialog("loading...");
        String user_id = String.valueOf(mPrefs.getUserID());
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id);

        Call<ResTransactions> call = ApiClient.getApiClient(this).getTransactionsAPI(map);
        call.enqueue(new Callback<ResTransactions>() {
            @Override
            public void onResponse(Call<ResTransactions> call, Response<ResTransactions> response) {
                hideProgressDialog();
                ResTransactions result = response.body();

                if (result != null){
                    if (result.errorCode == 0){
                        ResTransactions.TransactionResults results = result.results;
                        reportListAdapter.addAllTransactions(results.transactions);

                    }else {
                        Toast.makeText(getBaseContext(), result.errMsg, Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(getBaseContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResTransactions> call, Throwable t) {
                hideProgressDialog();
                Toast.makeText(getBaseContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();

            }
        });
    }


}
