package com.example.mcommerce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.mcommerce.R;
import com.example.mcommerce.common.GlobalConst;
import com.example.mcommerce.models.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductGridAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private OnProductItemClickListener onLinkageItemClickListener;

    private List<Product> productList = new ArrayList<>();

    public interface OnProductItemClickListener{
        public void onProductItemClicked(int pos);
    }

    public ProductGridAdapter(Context context, OnProductItemClickListener onLinkageItemClickListener){
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.onLinkageItemClickListener = onLinkageItemClickListener;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Product getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.product_item, null);

        Product product = getItem(position);
        TextView mTextProductName = convertView.findViewById(R.id.txt_product_name);
        TextView mTextProductCode = convertView.findViewById(R.id.txt_product_code);
        ImageView mImageProduct = convertView.findViewById(R.id.img_product);
        TextView mTextWithoutCash = convertView.findViewById(R.id.without_cash);
        TextView mTextWithCash = convertView.findViewById(R.id.with_cash);

        Picasso.with(context).load(product.product_photo_1).fit().centerCrop().placeholder(R.drawable.ic_no_image).into(mImageProduct);
        mTextProductName.setText(product.product_name);
        mTextProductCode.setText(product.product_code);
        mTextWithoutCash.setText(product.product_redeem_with_out_cash  + GlobalConst.PTS);
        mTextWithCash.setText("+$" + product.point_cash + " | " + product.product_redeem_with_cash + GlobalConst.PTS);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLinkageItemClickListener.onProductItemClicked(position);
            }
        });

        return convertView;
    }

    public void addAllProducts(List<Product> productList){
        this.productList = productList;
        notifyDataSetChanged();
    }


    public List<Product> getAllProducts(){
        return this.productList;
    }


    public void removeAll(){
        this.productList.clear();
        notifyDataSetChanged();
    }


}
