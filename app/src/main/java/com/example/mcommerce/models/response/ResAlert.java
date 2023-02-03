package com.example.mcommerce.models.response;

import com.example.mcommerce.models.Alert;

import java.util.ArrayList;
import java.util.List;

public class ResAlert extends ResBase {

    public Alerts results = null;

    public class Alerts{
        public List<Alert> alerts = new ArrayList<>();
        public int totalPage = 0;
    }
}
