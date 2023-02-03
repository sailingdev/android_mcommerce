package com.example.mcommerce.models;

import java.io.Serializable;

public class Product implements Serializable {
    public long id;
    public String product_category = "";
    public String product_code = "";
    public String product_name = "";
    public String product_description = "";
    public String product_photo_1 = "";
    public String product_photo_2 = "";
    public String product_photo_3 = "";
    public String product_redeem_with_out_cash;
    public String product_redeem_with_cash;
    public String point_cash;
    public String promothin_status;
    public String shop_name;
    public String shop_id;
}
