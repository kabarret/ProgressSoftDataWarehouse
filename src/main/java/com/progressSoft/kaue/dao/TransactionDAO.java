package com.progressSoft.kaue.dao;

import com.progressSoft.kaue.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Created by krb on 6/29/16.
 */
@Component
public class TransactionDAO {

    @Autowired
    private MongoTemplate mongoOperation;

    public void saveAll(Collection<Transaction> transactions){
        if (!transactions.isEmpty());
        mongoOperation.insert(transactions, Transaction.class);
    }



}
