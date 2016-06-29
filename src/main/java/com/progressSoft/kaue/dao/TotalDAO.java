package com.progressSoft.kaue.dao;

import com.progressSoft.kaue.entity.Total;
import com.progressSoft.kaue.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Created by krb on 6/29/16.
 */
@Component
public class TotalDAO {

    @Autowired
    private MongoTemplate mongoOperation;

    public void totalizing(){
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.group("fromCurrency").sum("amount").as("total"),
                Aggregation.project("total").and("fromCurrency").as("currency")
        );

        AggregationResults<Total> groupResults
                = mongoOperation.aggregate(agg, Transaction.class, Total.class);

        saveAll(groupResults.getMappedResults());
    }

    public void saveAll(Collection<Total> totals){
        if (!totals.isEmpty());
        mongoOperation.insert(totals, Total.class);
    }

}
