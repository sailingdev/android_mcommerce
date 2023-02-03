package com.example.mcommerce.models.response;

import com.example.mcommerce.models.Shop;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ResShop extends ResBase{

    @SerializedName("results")
    public Shops results = null;

    public class Shops{
        public List<Shop> shops = new ArrayList<>();
    }

}
