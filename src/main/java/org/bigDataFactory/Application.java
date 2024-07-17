package org.bigDataFactory;

import org.bigDataFactory.sparkSystem.SparkConverter;

//@SpringBootApplication
public class Application {
    public static void main(String[] args) throws Exception {
        //SpringApplication.run(Application.class, args);
        SparkConverter spark = SparkConverter.getInstance();
        spark.fetchDataCsv();
        spark.closeSpark();
    }
}
