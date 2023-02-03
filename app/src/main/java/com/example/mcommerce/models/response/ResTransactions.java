package com.example.mcommerce.models.response;


import com.example.mcommerce.models.Transaction;

import java.util.List;

public class ResTransactions extends ResBase{

    public TransactionResults results = null;

    public class TransactionResults{
        public List<Transaction> transactions = null;
    }
}
