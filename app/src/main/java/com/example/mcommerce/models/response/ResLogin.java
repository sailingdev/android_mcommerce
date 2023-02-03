package com.example.mcommerce.models.response;


import com.example.mcommerce.models.Login;
import com.google.gson.annotations.SerializedName;

public class ResLogin extends ResBase {

    @SerializedName("results")
    public Login results = null;
}
