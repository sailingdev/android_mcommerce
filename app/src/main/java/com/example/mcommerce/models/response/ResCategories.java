package com.example.mcommerce.models.response;

import com.example.mcommerce.models.Category;

import java.util.ArrayList;
import java.util.List;

public class ResCategories extends ResBase{
    public Categories results = null;
    public class Categories{
        public List<Category> categories = new ArrayList<>();
    }
}
