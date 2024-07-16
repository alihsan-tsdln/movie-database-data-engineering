package org.bigDataFactory;

import org.bigDataFactory.sparkSystem.SparkConverter;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args){
        //SpringApplication.run(Application.class, args);
        SparkConverter spark = SparkConverter.getInstance();
        spark.fetchDataCsv();
        spark.closeSpark();
    }
}
