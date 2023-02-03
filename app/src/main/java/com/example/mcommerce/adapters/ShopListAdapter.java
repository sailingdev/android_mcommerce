package com.example.mcommerce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.mcommerce.R;
import com.example.mcommerce.models.Shop;

import java.util.ArrayList;
import java.util.List;

public class ShopListAdapter extends BaseAdapter {


    private Context context;
    private LayoutInflater layoutInflater;
    private List<Shop> mListShops = new ArrayList<>();
    private OnShopItemClickListener onShopItemClickListener;

    public ShopListAdapter(Context context, OnShopItemClickListener onShopItemClickListener){
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.onShopItemClickListener = onShopItemClickListener;
    }


    public interface OnShopItemClickListener{
        public void onShopItemClicked(int pos);
    }

    @Override
    public int getCount() {
        return mListShops.size();
    }

    @Override
    public Shop getItem(int position) {
        return mListShops.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.shop_item, null);
        Shop shop = getItem(position);

        TextView mTextShopName = convertView.findViewById(R.id.txt_shop_name);
        TextView mTextShopAddress = convertView.findViewById(R.id.txt_shop_address);
        TextView mTextShopPhonenumber = convertView.findViewById(R.id.txt_shop_phonenumber);

        mTextShopName.setText(shop.shop_name);
        mTextShopAddress.setText(shop.shop_address);
        mTextShopPhonenumber.setText(shop.shop_phone);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShopItemClickListener.onShopItemClicked(position);
            }
        });

        return convertView;
    }


    public void addAllShops(List<Shop> mListShops){
        this.mListShops = mListShops;
        notifyDataSetChanged();
    }
}
