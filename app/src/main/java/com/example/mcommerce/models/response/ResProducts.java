package com.example.mcommerce.models.response;

import com.example.mcommerce.models.Product;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ResProducts extends ResBase {

    @SerializedName("results")
    public Products results = null;


    public class Products{
        public List<Product> products = new ArrayList<>();
        public String total_point = "";
    }

}
