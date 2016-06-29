package com.progressSoft.kaue;


import com.progressSoft.kaue.service.ProcessFileService;
import junit.framework.TestCase;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.io.File;

/**
 * Created by krb on 6/27/16.
 */
public class ProcessFileServiceTest extends TestCase {

    public void testProcessFile() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File mockData = new File(classLoader.getResource("MOCK_DATA.csv").getFile());

        long startTime = System.currentTimeMillis();

        ApplicationContext ctx = new GenericXmlApplicationContext("ApplicationContext.xml");
        ProcessFileService processFileService = (ProcessFileService) ctx.getBean("processFileService");
        processFileService.processFile(mockData);
        long stopTime = System.currentTimeMillis();

        System.out.println("Elapsed time was " + (stopTime - startTime)/1000 + " seconds.");

    }
}