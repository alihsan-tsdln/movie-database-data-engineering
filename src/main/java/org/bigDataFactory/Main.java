package org.bigDataFactory;

import org.bigDataFactory.sparkSystem.SparkConverter;

public class Main {
    public static void main(String[] args) throws Exception {
        SparkConverter spark = SparkConverter.getInstance();
        spark.fetchDataCsv();
        spark.closeSpark();
    }
}
