package com.progressSoft.kaue;


import com.progressSoft.kaue.entity.Total;
import com.progressSoft.kaue.entity.Transaction;
import com.progressSoft.kaue.entity.TransactionError;
import com.progressSoft.kaue.service.ProcessFileService;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by krb on 6/27/16.
 */
public class ProcessFileServiceTest extends TestCase {
    ProcessFileService processFileService;
    MongoTemplate mongoTemplate;

    @Before
    public void setUp(){
        ApplicationContext ctx = new GenericXmlApplicationContext("TestContext.xml");
        processFileService = (ProcessFileService) ctx.getBean("processFileService");
        mongoTemplate = (MongoTemplate) ctx.getBean("mongoTemplate");
        mongoTemplate.remove(new Query(),Total.class);
        mongoTemplate.remove(new Query(),TransactionError.class);
        mongoTemplate.remove(new Query(),Transaction.class);
    }

    @Test
    public void testProcessShouldSaveAllValidRegister() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File mockData = new File(classLoader.getResource("TEST_DATA.csv").getFile());
        processFileService.processFile(mockData);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        List<Transaction> transactions = mongoTemplate.findAll(Transaction.class);
        Assert.assertEquals(10,  transactions.size());

        Transaction transaction =  mongoTemplate.findById("1", Transaction.class);
        Assert.assertEquals("1",  transaction.getId());
        Assert.assertEquals("EUR", transaction.getFromCurrency());
        Assert.assertEquals("BRL", transaction.getToCurrency());
        Assert.assertEquals(sdf.parse("2016-05-31T21:50:29Z"), transaction.getTime());
        Assert.assertEquals(150d, transaction.getAmount());
    }

    @Test
    public void testProcessShouldSaveAllInvalidRegister() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File mockData = new File(classLoader.getResource("TEST_DATA.csv").getFile());
        processFileService.processFile(mockData);
        List<TransactionError> transactionsErrors = mongoTemplate.findAll(TransactionError.class);
        Assert.assertEquals(1,  transactionsErrors.size());

        TransactionError transactionErrors = transactionsErrors.get(0);
        Assert.assertEquals("Can't process this transaction because had empty fields", transactionErrors.getError());
        Assert.assertEquals("TEST_DATA.csv", transactionErrors.getFile());
        Assert.assertEquals(Long.valueOf(11l), transactionErrors.getLine());

    }

    @Test
    public void testProcessShouldTotalizerAllRegisterByCurrency() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File mockData = new File(classLoader.getResource("TEST_DATA.csv").getFile());
        processFileService.processFile(mockData);

        Assert.assertEquals(6,  mongoTemplate.findAll(Total.class).size());

        Total total = mongoTemplate.findById("EUR", Total.class);
        Assert.assertEquals(714.2, total.getTotal());

    }
}