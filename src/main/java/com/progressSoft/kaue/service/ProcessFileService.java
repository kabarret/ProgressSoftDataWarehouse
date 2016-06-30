package com.progressSoft.kaue.service;

import com.progressSoft.kaue.dao.TotalDAO;
import com.progressSoft.kaue.dao.TransactionDAO;
import com.progressSoft.kaue.dao.TransactionErrorDAO;
import com.progressSoft.kaue.entity.Transaction;
import com.progressSoft.kaue.entity.TransactionError;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
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


    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public void processFile(final File file)  {

        try {
            final Reader reader = new InputStreamReader(new FileInputStream(file));
            final CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL.withHeader("id","from","to","time","amount").withSkipHeaderRecord());

            final Collection<Transaction> transactions = new HashSet<Transaction>();
            final Collection<TransactionError> transactionsErrors = new HashSet<TransactionError>();

            for (CSVRecord csvRecord : parser) {
                if (csvRecord.get("id").isEmpty()
                        || csvRecord.get("from").isEmpty()
                        || csvRecord.get("time").isEmpty()
                        || csvRecord.get("to").isEmpty()
                        ||  csvRecord.get("amount").isEmpty()
                ){
                    TransactionError transactionError = new TransactionError();
                    transactionError.setError("Can't process this transaction because had empty fields");
                    transactionError.setFile(file.getName());
                    transactionError.setLine(csvRecord.getRecordNumber());

                    transactionsErrors.add(transactionError);
                }else {
                    try {
                        Date time = sdf.parse(csvRecord.get("time"));

                        Transaction transaction = new Transaction();
                        transaction.setId(csvRecord.get("id"));
                        transaction.setFromCurrency(csvRecord.get("from"));
                        transaction.setToCurrency(csvRecord.get("to"));
                        transaction.setAmount(Double.valueOf(csvRecord.get("amount")));
                        transaction.setTime(time);
                        transactions.add(transaction);

                    } catch (ParseException e) {
                        TransactionError transactionError = new TransactionError();
                        transactionError.setError("Invalid date format, the format should be yyyy-MM-dd'T'HH:mm:ss");
                        transactionError.setFile(file.getName());
                        transactionError.setLine(csvRecord.getRecordNumber());
                        transactionsErrors.add(transactionError);
                    }


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
