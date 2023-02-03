package com.example.mcommerce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mcommerce.R;
import com.example.mcommerce.models.Category;

import java.util.ArrayList;
import java.util.List;

public class CategorySpinnerAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflter;
    private List<Category> mListCategory = new ArrayList<>();


    public CategorySpinnerAdapter(Context applicationContext) {
        this.context = applicationContext;
        inflter = (LayoutInflater.from(applicationContext));
    }


    @Override
    public int getCount() {
        return mListCategory.size();
    }

    @Override
    public Category getItem(int position) {
        return mListCategory.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflter.inflate(R.layout.category_spinner_item, null);

        Category category = mListCategory.get(position);
        TextView mTextTableItemInSpinner = convertView.findViewById(R.id.txtTableName);
        mTextTableItemInSpinner.setText(category.product_category);
        return convertView;
    }

    public void addAllCategory(List<Category> mListCategory){
        this.mListCategory = mListCategory;
        notifyDataSetChanged();
    }
}
