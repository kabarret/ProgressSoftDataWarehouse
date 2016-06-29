package com.progressSoft.kaue;

import com.progressSoft.kaue.service.ProcessFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.nio.file.StandardWatchEventKinds;

/**
 * Created by krb on 6/29/16.
 */
@Service
public class Main {

    @Autowired
    private ProcessFileService processFileService;

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final Path inputDir = Paths.get("filesToProcess");
    private static final Path outputDir = Paths.get("filesProccessed");

    public static void main(String args[]){
        ApplicationContext ctx = new GenericXmlApplicationContext("ApplicationContext.xml");
        Main mainClass = (Main) ctx.getBean("main");

        mainClass.initialFolderScan();

        LOGGER.info("==== Start Watching :" + inputDir.toAbsolutePath().toString());
        while (true) {
            try {
                List<WatchEvent<?>> events = getWatchEvents();
                for (WatchEvent event : events) {
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                        mainClass.processFile(new File(inputDir.toString(), event.context().toString()));
                    }
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.toString());
            }
        }
    }

    private static List<WatchEvent<?>> getWatchEvents() throws IOException, InterruptedException {
        WatchService watcher = inputDir.getFileSystem().newWatchService();
        inputDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);

        WatchKey watckKey = watcher.take();

        return watckKey.pollEvents();
    }


    public void processFile(File file){
        LOGGER.info("==== Process of " + file.toString() + " started");
        long startTime = System.currentTimeMillis();
        processFileService.processFile(file);
        // move to outpout
        long stopTime = System.currentTimeMillis();
        LOGGER.info("==== Process of " + file.toString() + " end with " + (stopTime - startTime)/1000 + " seconds.");
        file.renameTo(new File(outputDir.toString(), file.getName()));
        LOGGER.info("==== File " + file.toString() + " move to  " + outputDir.toAbsolutePath().toString());
    }

    public void initialFolderScan(){
        if (inputDir.toFile().list().length == 0){
            LOGGER.info("==== No file to read in " + inputDir.toAbsolutePath().toString());
            LOGGER.info("==== Please add files to process");
        }
        for (String item : inputDir.toFile().list()) {
            processFile(new File(inputDir.toAbsolutePath().toString(), item));
        }
    }

}
