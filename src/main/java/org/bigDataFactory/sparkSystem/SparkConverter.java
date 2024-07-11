package org.bigDataFactory.sparkSystem;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.noggit.JSONParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.apache.spark.sql.functions.*;

public class SparkConverter {
    public SparkConverter() {
        SparkSession spark = SparkSession.builder().master("local").getOrCreate();

        Dataset<Row> df = spark.read().option("header",true).csv("src/main/resources/Movies/credits.csv");



        String jsonString;

        List<Row> listOfMovies = df.select("cast").collectAsList();
        int counter = 0;
        for(Row cast : listOfMovies) {
            jsonString = cast.getString(0);
            try {
            JSONArray jsonArray = createJsonDataAsArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                //showJsonObjectData(jsonObject);
            }
            }
            catch (JSONException e) {
                System.out.println(jsonString);
                e.printStackTrace();
                System.out.println(++counter);
            }
        }

        listOfMovies = df.select("crew").collectAsList();
        System.out.println(listOfMovies.get(0).getString(0));
        counter = 0;
        for(int i = 0; i < listOfMovies.size(); i++) {
            jsonString = listOfMovies.get(i).getString(0);
            try {
                JSONArray jsonArray = createJsonDataAsArray(jsonString);

                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(j);
                    //showJsonCrewObjectData(jsonObject);
                }
            }
            catch (JSONException e) {
                System.out.println(++counter);
            }
        }

        listOfMovies = df.select("id").collectAsList();
        df.select("id").show(false);
        /*counter = 0;
        for(Row cast : listOfCast) {
            jsonString = cast.getString(0);
            try {
                JSONArray jsonArray = createJsonDataAsArray(jsonString);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    //showJsonCrewObjectData(jsonObject);
                }
            }
            catch (JSONException e) {
                System.out.println(++counter);
            }
        }*/


        //df.select("cast").show(false);
        //df.select("crew").show(false);

        spark.stop();
        

        //df.show();
    }

    public JSONArray createJsonDataAsArray(String jsonString) {
        return new JSONArray(jsonString);
    }

    public void showJsonCastObjectData(JSONObject jsonObject) {
        String castId = jsonObject.getString("cast_id");
        String character = jsonObject.getString("character");
        String creditId = jsonObject.getString("credit_id");
        int gender = jsonObject.getInt("gender");
        int id = jsonObject.getInt("id");
        String name = jsonObject.getString("name");
        int order = jsonObject.getInt("order");
        String profilePath = jsonObject.optString("profile_path", "N/A");

        System.out.println("Cast ID: " + castId);
        System.out.println("Character: " + character);
        System.out.println("Credit ID: " + creditId);
        System.out.println("Gender: " + gender);
        System.out.println("ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Order: " + order);
        System.out.println("Profile Path: " + profilePath);
        System.out.println();
    }

    public void showJsonCrewObjectData(JSONObject jsonObject) {
        String creditId = jsonObject.getString("credit_id");
        String department = jsonObject.getString("department");
        int gender = jsonObject.getInt("gender");
        int id = jsonObject.getInt("gender");
        String job = jsonObject.getString("job");
        String name = jsonObject.getString("name");
        String profilePath = jsonObject.optString("profile_path", "N/A");

        System.out.println("Credit ID: " + creditId);
        System.out.println("Department: " + department);
        System.out.println("Gender: " + gender);
        System.out.println("ID: " + id);
        System.out.println("Job: " + job);
        System.out.println("Name: " + name);
        System.out.println("Profile Path: " + profilePath);
        System.out.println();
    }
}
