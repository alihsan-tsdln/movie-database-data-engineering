# Overview
This project is a movie data analysis platform that collects JSON formatted movie data in CSV, structures it using Spark, and loads it into JanusGraph. By leveraging the powerful graph database features of JanusGraph, relationships between movies, actors, directors, and other entities can be visualized. The project is built as a web application using Spring Boot and React.js, allowing users to access and query this data.

# Technologies
* Java 17
* Apache Maven : 4.0.0
* Apache Spark
    - Scala Version : 2.13
    - Spark Version : v4.0.0-preview
* Docker : 24.0.7
* Docker Compose : 1.29.2
* JanusGraph : 1.1.0-20240712-100602.aaf21b1
* Apache Cassandra : 5.0.0
* Elasticsearch : 8.14.3
* Gremlin Visualizer : 1.0
* Apache Tinkerpop
  - Gremlin Driver : 3.7.2
* Spring : 3.3.1
* JSON : 20240303
* React.js : 18.3.1
* NPM : 10.8.2
* Node.js : 22.5.1

# UML of the Project

[UML of project.pdf](https://github.com/user-attachments/files/16468677/UML.class.staj.pdf)

# Installation
## Prerequisites
* Java JDK 17
* Docker
* Docker Compose
* Node.js

## Clone the project
```bash
git clone https://github.com/alihsan-tsdln/movie-database-data-engineering.git
cd movie-database-data-engineering
```

## Download Maven Dependencies and Compile the project
```bash
mvn clean install
javac *.java
```

# Leverage Docker Containers
``` bash
sudo docker-compose up -d
```
If the images don't exist, Docker will automatically pull them.


# Start the Bulk Loading
```bash
java Main
```
// WARNING : Bulk loading doesn't control consistency of your data. Be sure consistency of your data.

We may monitor our job executions from [http:/localhost:4040](http:/localhost:4040)

![image](https://github.com/user-attachments/assets/b2ffdad8-31b8-4a04-a7fd-7fac4b2581ba)


# Start the web server
```bash
java Application
```
SpringBoot will start server in [http:/localhost:8080](http:/localhost:8080)

```bash
npm start
```
NPM will start React.js in [http:/localhost:3002](http:/localhost:3002)
Normally, React tries to start in 3000 port but our Gremlin Visualizer uses 3000 port.

You can query the questions which in drop-down menu.

![Screenshot from 2024-08-01 10-04-19](https://github.com/user-attachments/assets/b54a2496-c20e-49be-a8d0-d0f537b3f69d)

# Visualize the Graph
We started Gremlin Visualizer in Docker.
Firstly, from JanusGraph instance which in Docker we will start Gremlin server.

```bash
sudo docker ps
```
We've learned the ID of JanusGraph server from output of command.

```bash
CONTAINER ID   IMAGE                                                 COMMAND                  CREATED          STATUS          PORTS                                                                                                           NAMES
595a60cce026   cassandra:3                                           "docker-entrypoint.s…"   14 minutes ago   Up 14 minutes   7000-7001/tcp, 0.0.0.0:9042->9042/tcp, :::9042->9042/tcp, 7199/tcp, 0.0.0.0:9160->9160/tcp, :::9160->9160/tcp   jce-cassandra
474275e60438   docker.elastic.co/elasticsearch/elasticsearch:6.6.0   "/usr/local/bin/dock…"   14 minutes ago   Up 14 minutes   0.0.0.0:9200->9200/tcp, :::9200->9200/tcp, 9300/tcp                                                             jce-elastic
8fe5588bcd23   janusgraph/janusgraph:latest                          "docker-entrypoint.s…"   14 minutes ago   Up 14 minutes   0.0.0.0:8182->8182/tcp, :::8182->8182/tcp                                                                       jce-janusgraph
d9540645d532   prabushitha/gremlin-visualizer:latest                 "docker-entrypoint.s…"   14 minutes ago   Up 14 minutes   0.0.0.0:3000-3001->3000-3001/tcp, :::3000-3001->3000-3001/tcp                                                   gremlin_visualize
```

```bash
sudo docker exec -it ${Id of your JanusGraph server} bash
```
We've reached the server terminal.

```bash
bin/gremlin.sh
```
We've started Gremlin Server by gremlin.sh.

```bash
janusgraph@8fe5588bcd23:/opt/janusgraph$ bin/gremlin.sh

         \,,,/
         (o o)
-----oOOo-(3)-oOOo-----
SLF4J: Class path contains multiple SLF4J bindings.
SLF4J: Found binding in [jar:file:/opt/janusgraph/lib/log4j-slf4j-impl-2.20.0.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: Found binding in [jar:file:/opt/janusgraph/lib/logback-classic-1.2.11.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.
SLF4J: Actual binding is of type [org.apache.logging.slf4j.Log4jLoggerFactory]
plugin activated: tinkerpop.server
plugin activated: tinkerpop.tinkergraph
07:32:30 INFO  org.apache.tinkerpop.gremlin.hadoop.jsr223.HadoopGremlinPlugin.getCustomizers - HADOOP_GREMLIN_LIBS is set to: /opt/janusgraph/lib
07:32:30 WARN  org.apache.hadoop.util.NativeCodeLoader.<clinit> - Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
plugin activated: tinkerpop.hadoop
plugin activated: tinkerpop.spark
plugin activated: tinkerpop.utilities
plugin activated: janusgraph.imports
gremlin>
```

Finally, we are able to use website. The link is [http:/localhost:3002](http:/localhost:3002).
Host part would be your JanusGraph's host. Port is 8182 default port of Gremlin.


![Screenshot from 2024-07-29 10-37-12](https://github.com/user-attachments/assets/87886d53-ee39-4a0e-8304-df6d5f04a2d4)
























