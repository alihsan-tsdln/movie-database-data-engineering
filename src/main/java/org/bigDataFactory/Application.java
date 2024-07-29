package org.bigDataFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
        //SparkConverter spark = SparkConverter.getInstance();
        //spark.fetchDataCsv();
        //spark.closeSpark();
        //JanusGraphClient client = new JanusGraphClient();
        //JanusGraphManagement management = client.getGraph().openManagement();
        //management.set("query.force-index", false);
        //System.out.println(management.get("query.force-index"));
        //management.commit();
        //client.closeConnection();
    }
}
