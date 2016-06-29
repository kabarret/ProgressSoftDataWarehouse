# ProgressSoftDataWarehouse

Prerequisite

  - Mongo DB install in localhost 2.3 or or higher
  - Java with JDK 1.7 or higher
  - Maven 3 or higher


To start run in your terminal the fallow command : 

mvn install exec:java -Dexec.mainClass="com.progressSoft.kaue.Main"  -DskipTests

You will need to add file to process in the root folder fileToProcess
The files need to be a .csv and having a header with ["id","from","to","time","amount"]
It like de sample MOCK_DATA.csv, if you don't want process the MOCK_DATA file, please remove from fileToProcess.

You can check the result of import looking into mongodb/kauedb
If you want change mongo database you can change on ApplicationContex.xml
