package com.progressSoft.kaue.service;

import com.progressSoft.kaue.dao.TotalDAO;
import com.progressSoft.kaue.dao.TransactionDAO;
import com.progressSoft.kaue.dao.TransactionErrorDAO;
import com.progressSoft.kaue.entity.Total;
import com.progressSoft.kaue.entity.Transaction;
import com.progressSoft.kaue.entity.TransactionError;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by krb on 6/27/16.
 */
@Service
public class ProcessFileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessFileService.class);

    @Autowired
    private TransactionDAO transactionDAO;

    @Autowired
    private TotalDAO totalDAO;


    @Autowired
    private TransactionErrorDAO transactionErrorDAO;

    public void processFile(File file)  {

        try {
            final Reader reader = new InputStreamReader(new FileInputStream(file));
            final CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL.withHeader("id","from","to","time","amount").withSkipHeaderRecord());

            final Collection<Transaction> transactions = new HashSet<Transaction>();
            final Collection<TransactionError> transactionsErrors = new HashSet<TransactionError>();

            for (CSVRecord csvRecord : parser) {
                if (csvRecord.get("id").isEmpty()
                        || csvRecord.get("from").isEmpty()
                        || csvRecord.get("to").isEmpty()
                        ||  csvRecord.get("amount").isEmpty()
                ){
                    TransactionError transactionError = new TransactionError();
                    transactionError.setError("Can't process this transaction because had empty fields");
                    transactionError.setFile(file.getName());
                    transactionError.setLine(csvRecord.getRecordNumber());

                    transactionsErrors.add(transactionError);
                }else {
                    Transaction transaction = new Transaction();
                    transaction.setId(csvRecord.get("id"));
                    transaction.setFromCurrency(csvRecord.get("from"));
                    transaction.setToCurrency(csvRecord.get("to"));
                    transaction.setAmount(Double.valueOf(csvRecord.get("amount")));

                    transactions.add(transaction);
                }
            }

            LOGGER.info("Saving transactions");
            transactionDAO.saveAll(transactions);
            transactionErrorDAO.saveAll(transactionsErrors);

            LOGGER.info("Totalizing transactions");
            totalDAO.totalizing();

            LOGGER.info("End process");
        } catch (IllegalArgumentException e){
            LOGGER.warn("Invalid type of file, the file need to be .csv type and have a follow header: [\"id\",\"from\",\"to\",\"time\",\"amount\"] ");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
