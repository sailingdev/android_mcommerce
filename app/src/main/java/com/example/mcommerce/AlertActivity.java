package com.example.mcommerce;

import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mcommerce.adapters.AlertRecyclerViewAdapter;
import com.example.mcommerce.adapters.PaginationScrollListener;
import com.example.mcommerce.api.ApiClient;
import com.example.mcommerce.models.Alert;
import com.example.mcommerce.models.response.ResAlert;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlertActivity extends BaseActivity implements AlertRecyclerViewAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.img_left)
    ImageView mImageLogo;
    @BindView(R.id.txt_center)
    TextView mTextTitle;
    @BindView(R.id.ic_back) ImageView mImageBackBtn;

    private AlertRecyclerViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    public static final int PAGE_START = 1;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;
    int itemCount = 0;

    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;

    private void initView(){
        mTextTitle.setText("Alerts");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        ButterKnife.bind(this);

        initView();
        swipeRefresh.setOnRefreshListener(this);
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new AlertRecyclerViewAdapter(this, new ArrayList<Alert>(), this);
        mRecyclerView.setAdapter(mAdapter);
        preparedListItem();
        /**
         * add scroll listener while user reach in bottom load more will call
         */
        mRecyclerView.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {

            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                preparedListItem();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }


    private void preparedListItem() {
        getAlerts(currentPage);
    }


    public void getAlerts(int pageNum){
        swipeRefresh.setRefreshing(true);

        Call<ResAlert> call = ApiClient.getApiClient(this).getAlertsAPI(pageNum, "");
        call.enqueue(new Callback<ResAlert>() {
            @Override
            public void onResponse(Call<ResAlert> call, Response<ResAlert> response) {
                //hideProgressDialog();
                swipeRefresh.setRefreshing(false);
                ResAlert result = response.body();

                if (result != null){
                    if (result.errorCode == 0){
                        ResAlert.Alerts results = result.results;
                        itemCount += results.alerts.size();
                        totalPage = results.totalPage;
                        if (currentPage != PAGE_START) mAdapter.removeLoading();
                        mAdapter.addAll(results.alerts);
                        swipeRefresh.setRefreshing(false);
                        if (currentPage < totalPage)
                            mAdapter.addLoading();
                        else
                            isLastPage = true;
                        isLoading = false;
                    }else {
                        Toast.makeText(getBaseContext(), result.errMsg, Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(getBaseContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResAlert> call, Throwable t) {
                //hideProgressDialog();
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getBaseContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void longClickedListener(int pos) {}

    @Override
    public void onRefresh() {
        itemCount = 0;
        currentPage = PAGE_START;
        isLastPage = false;
        mAdapter.clear();
        preparedListItem();
    }


    @OnClick({R.id.ic_back})
    void btnActions(View v){
        finish();
    }
}
