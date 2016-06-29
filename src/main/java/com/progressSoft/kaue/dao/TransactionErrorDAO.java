package com.progressSoft.kaue.dao;

import com.progressSoft.kaue.entity.TransactionError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Created by krb on 6/29/16.
 */
@Component
public class TransactionErrorDAO {

    @Autowired
    private MongoTemplate mongoOperation;

    public void saveAll(Collection<TransactionError> transactions){
        if (!transactions.isEmpty());
        mongoOperation.insert(transactions, TransactionError.class);
    }
}

