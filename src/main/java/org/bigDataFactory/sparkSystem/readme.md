# Bulk Loading with Spark
We can say, this class is backbone of the whole project. We are able to bulk load with Spark so easily. Let's dive in.

```java
SparkSession spark;

private static SparkConverter converter;

private SparkConverter() {
    spark = SparkSession.builder().master("local").getOrCreate();
}

public static synchronized SparkConverter getInstance() {
    if(converter == null) {
        converter = new SparkConverter();
    }
    return converter;
}
```

We decided singleton class for Spark Converter because we start SparkSession in constructer. Spark Driver must be one. Thus, singleton design pattern is tailor-made for us.

We've created SparkSession. We can any action and transaction job because of it.

```java
public void fetchDataCsv() throws Exception
```

You might see fetchDataCsv method in Main.java. This method managing whole process of bulk loading from inside. 

```java
Dataset<Row> df = spark.read().option("header", true).csv("src/main/resources/Movies/credits.csv");
```
We've read csv file easily with Spark.

For instance :
```
+--------------------+--------------------+-----+
|                cast|                crew|   id|
+--------------------+--------------------+-----+
|[{'cast_id': 14, ...|[{'credit_id': '5...|  862|
|[{'cast_id': 1, '...|[{'credit_id': '5...| 8844|
|[{'cast_id': 2, '...|[{'credit_id': '5...|15602|
|[{'cast_id': 1, '...|[{'credit_id': '5...|31357|
|[{'cast_id': 1, '...|[{'credit_id': '5...|11862|
+--------------------+--------------------+-----+
```
We've got data in memory. As you can see, our data is semi-structed. In every row;

id   : TMDB movie id
cast : JSON list of every cast person.
crew : JSON list of every crew person.

```java
StructType schemaCast = new StructType()
        .add("cast_id", DataTypes.IntegerType, false)
        .add("character", DataTypes.StringType, true)
        .add("gender", DataTypes.IntegerType, true)
        .add("credit_id", DataTypes.StringType, true)
        .add("name", DataTypes.StringType, true)
        .add("profile_path", DataTypes.StringType, true)
        .add("id", DataTypes.IntegerType, true)
        .add("order", DataTypes.IntegerType, true)
        .add("movie_id", DataTypes.StringType);

StructType schemaCrew = new StructType()
        .add("gender", DataTypes.IntegerType)
        .add("credit_id", DataTypes.StringType)
        .add("name", DataTypes.StringType)
        .add("profile_path", DataTypes.StringType)
        .add("id", DataTypes.IntegerType)
        .add("department", DataTypes.StringType)
        .add("job", DataTypes.StringType)
        .add("movie_id", DataTypes.StringType);
```

StructType is a class of SparkSQL. It creates schema for Datasets. We must convert the semi-structured data to structured. After creating schema and have data structuring plan:

```java
Dataset<Row> dfCast = createDataset(df, "cast", schemaCast).distinct();
Dataset<Row> dfCrew = createDataset(df, "crew", schemaCrew).distinct();
```

We will create two Dataset for Cast and Crew teams of movies. 

```java
private Dataset<Row> createDataset(@NotNull Dataset<Row> df, String columnName, StructType schema) {
    int movieIdx = 0;
    List<Row> listOfData = df.select(columnName).collectAsList();
    List<Row> movie_ids = df.select("id").collectAsList();
    ArrayList<Row> moviesDataList = new ArrayList<>();
    for(Row cast : listOfData) {
        String movie_id = movie_ids.get(movieIdx).getString(0);
        JSONArray jsonArray = new JSONArray(cast.getString(0));
        for (int i = 0; i < jsonArray.length(); i++)
            moviesDataList.add(createRowFromFactory(jsonArray.getJSONObject(i), movie_id));
        movieIdx++;
    }
    return spark.createDataFrame(moviesDataList, schema);
}
```
createDataset method is creating Datasets from JSON Lists. We've taken help from JSON library. Firstly, we choose specific column ("cast" or "crew" for this project). After that we've taken movie ids because we doesn't want to lost data which who worked on which movie. We convert them as Java List. In for loop, JSON objects of JSON list is converting Spark Row instance. With these all rows, we can create dataset easily.

Cast Dataset:
```
+-------+------------------+------+--------------------+--------------------+--------------------+------+-----+--------+
|cast_id|         character|gender|           credit_id|                name|        profile_path|    id|order|movie_id|
+-------+------------------+------+--------------------+--------------------+--------------------+------+-----+--------+
|     18|              Hitu|     2|52fe44dfc3a36847f...|Adewale Akinnuoye...|/lnFcwjgNDNRzQ8yC...| 31164|    7|    9273|
|     21|     Wachati Chief|     2|52fe44dfc3a36847f...|     Damon Standifer|                None|172354|   10|    9273|
|      4|Nicolai Tashlinkov|     0|52fe451cc3a36847f...|     Anatoli Davydov|/h612YfWBEymtZw13...| 58556|    3|    9691|
|      2|             Kirby|     2|52fe44409251416c7...|         Keith David|/nwAC9TgwRkj0Ritq...| 65827|    1|   11443|
|     18|   Amanda's Mother|     1|52fe44429251416c7...|        Claire Bloom|/4x0Cfh1g8apqOPio...| 29545|    5|   11448|
+-------+------------------+------+--------------------+--------------------+--------------------+------+-----+--------+
```

Crew Dataset:
```
+------+--------------------+-----------------+--------------------+-------+----------+--------------------+--------+
|gender|           credit_id|             name|        profile_path|     id|department|                 job|movie_id|
+------+--------------------+-----------------+--------------------+-------+----------+--------------------+--------+
|     1|52fe44779251416c9...|Deborah Schindler|/2vFzdHxcB8cEtvPl...|  70592|Production|            Producer|   31357|
|     2|569cf7d89251415e5...|  Mitch Gettleman|                None|1565200|     Sound|Sound Effects Editor|   11860|
|     2|52fe420dc3a36847f...|Quentin Tarantino|/9ci4NBvHXJktxjAL...|    138|   Writing|              Writer|       5|
|     0|59a1779bc3a36847c...|  Glenn Hoskinson|                None|1412227|     Sound|Sound Effects Editor|       5|
|     0|52fe428ac3a36847f...|      Arlette Mas|                None|  13831|Production|            Producer|     902|
+------+--------------------+-----------------+--------------------+-------+----------+--------------------+--------+
```

```java
JanusGraphProducer.getInstance().createSchema();
```
We created schema from JanusGraphProducer class. [Click here for detailed description of this class](). For now, we only need to know that this class generates schema for Graph database.

```java
loadMoviesToJanus(df);
```
Our only structured column was id. So we can easily load data to Graph Database. 

```java
private void loadMoviesToJanus(@NotNull Dataset<Row> df) {
    Dataset<Row> movieDataset = df.select("id").distinct();
    System.gc();
    int partitionNumber = (int) Math.ceil((double) SizeEstimator.estimate(df) / Runtime.getRuntime().freeMemory() * 5);
    System.out.println(partitionNumber);

    movieDataset.repartition(partitionNumber).foreachPartition((ForeachPartitionFunction<Row>) iterator -> JanusGraphConsumer.getInstance().loadMoviesToJanus(iterator));
}
```
We know id column is structured as well. We broke up Dataset to partitions with basic mathematical equation. Because, we would love to prevent OutOfMemmory error. We sent each parititon individually for each partition, taking into account that it can be distributed to separate nodes in the cluster. We load data with JanusGraphConsumer class. [Click here for detailed description of this class](). For now, we only need to know that this class loads data to Graph database.

```java
loadVertexesToJanus(dfCast.select("id","name","gender","profile_path").union(dfCrew.select("id","name","gender","profile_path")));
```
SQL Union operation unify data who has same column. A person can be cast and crew at the same time. (etc. Clint Eastwood, Tom Hanks. These man are actor and director.) So we unify these data and reduce redundant data.

```java
private void loadVertexesToJanus(Dataset<Row> df) {
    df = df.dropDuplicates("id");
    System.out.println("VERTEX PARTITION COUNT");
    System.out.println((int) SizeEstimator.estimate(df));
    System.out.println(Runtime.getRuntime().freeMemory());

    System.gc();
    int partitionNumber = (int) Math.ceil((double) SizeEstimator.estimate(df) / Runtime.getRuntime().freeMemory() * 5);
    System.out.println(partitionNumber);
    df.repartition(partitionNumber).foreachPartition((ForeachPartitionFunction<Row>) iterator -> JanusGraphConsumer.getInstance().loadVertexesToJanus(iterator));
}
```
Vertex load method is same with load movie method but we use loadVertexesToJanus() differantly.


After adding whole vertexes (person and movie), we must add relations. In this project, these are "acted" and "worked".

```java
loadEdgesCastToJanus(dfCast);
loadEdgesCrewToJanus(dfCrew);
```

```java
private void loadEdgesCastToJanus(@NotNull Dataset<Row> dfCast) {
    Dataset<Row> castEdges = dfCast.select( "id","movie_id","cast_id", "character", "credit_id", "order");
    System.out.println("CAST PARTITION COUNT");
    System.out.println(SizeEstimator.estimate(castEdges));
    System.out.println(Runtime.getRuntime().freeMemory());
    System.gc();
    int partitionNumber = (int) Math.ceil((double) SizeEstimator.estimate(castEdges) / Runtime.getRuntime().freeMemory() * 5);

    System.out.println(partitionNumber);

    castEdges.repartition(partitionNumber)
        .foreachPartition((ForeachPartitionFunction<Row>) iterator -> JanusGraphConsumer.getInstance().loadEdgesCastToJanus(iterator));
}

private void loadEdgesCrewToJanus(@NotNull Dataset<Row> dfCrew) {
    Dataset<Row> crewEdges = dfCrew.select("id","movie_id","credit_id", "department", "job");
    System.out.println("CREW PARTITION COUNT");
    System.out.println(SizeEstimator.estimate(crewEdges));
    System.out.println(Runtime.getRuntime().freeMemory());
    System.gc();
    int partitionNumber = (int) Math.ceil((double) SizeEstimator.estimate(crewEdges) / Runtime.getRuntime().freeMemory() * 5);
    System.out.println(partitionNumber);

    crewEdges.repartition(partitionNumber)
        .foreachPartition((ForeachPartitionFunction<Row>) iterator -> JanusGraphConsumer.getInstance().loadEdgesCrewToJanus(iterator));
}
```

As you can see, loading methods are not so differant itself.

Our bulk loading job is done. We must stop Spark server. 

```java
public void closeSpark() {
    spark.stop();
    spark.close();
}
```

The bulk loading process is done. Congrats.

















