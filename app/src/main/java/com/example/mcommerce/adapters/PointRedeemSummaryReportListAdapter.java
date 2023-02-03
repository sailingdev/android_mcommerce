package com.example.mcommerce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mcommerce.R;
import com.example.mcommerce.common.GlobalConst;
import com.example.mcommerce.models.Transaction;
import com.example.mcommerce.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;


public class PointRedeemSummaryReportListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

    private List<Transaction> mListTransactions = new ArrayList<>();

    public PointRedeemSummaryReportListAdapter(Context context){
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mListTransactions.size();
    }

    @Override
    public Transaction getItem(int position) {
        return mListTransactions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.summary_item, null);

        Transaction item = getItem(position);

        TextView mTextRedeemPoint = convertView.findViewById(R.id.redeem_point);
        TextView mTextRedeemReferenceNumber = convertView.findViewById(R.id.redeem_reference_number);
        TextView mTextProductCode = convertView.findViewById(R.id.product_code);
        TextView mTextDescription = convertView.findViewById(R.id.description);
        TextView mTextDateRedeem = convertView.findViewById(R.id.date_redeem);

        if (item.point_cash == null){
            mTextRedeemPoint.setText(item.point_redeem + GlobalConst.PTS);
        } else {
            mTextRedeemPoint.setText("+$" + item.point_cash + " | " + item.point_redeem + GlobalConst.PTS);
        }

        mTextRedeemReferenceNumber.setText(item.redeem_reference_number);
        mTextProductCode.setText(item.product_code);
        mTextDescription.setText(item.description);

        mTextDateRedeem.setText(DateTimeUtils.convertFormat(DateTimeUtils.FMT_FULL, DateTimeUtils.FMT_STANDARD, item.date_redeem));
        return convertView;
    }

    public void addAllTransactions(List<Transaction> transactions) {
        mListTransactions = transactions;
        notifyDataSetChanged();
    }
}
