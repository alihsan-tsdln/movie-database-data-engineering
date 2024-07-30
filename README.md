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

# Visualize the Graph
We started Gremlin Visualizer in Docker.
Firstly, from JanusGraph instance which in Docker we will start Gremlin server.

```bash
sudo docker ps
```
We've learned the ID of JanusGraph server from output of command.

```bash
sudo docker exec -it ${Id of your JanusGraph server} bash
```
We've reached the server terminal.

```bash
bin/gremlin.sh
```
We've started Gremlin Server by gremlin.sh.

Finally, we are able to use website. The link is [http:/localhost:3000](http:/localhost:3000).
Host part would be your JanusGraph's host. Port is 8182 default port of Gremlin.






















