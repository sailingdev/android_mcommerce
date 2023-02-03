package com.example.mcommerce.models;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    public int id = 0;

    @SerializedName("username")
    public String username = "";

    @SerializedName("email")
    public String email = "";

    @SerializedName("password")
    public String password = "";

    public String phonenumber = "";
    public String birthday = "";
    public String role = "";
    public String referal_point = "";
    public String purchase_point = "";
    public String earn_point = "";
    public String user_photo = "";

}
