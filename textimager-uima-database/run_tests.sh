#!/bin/bash

# remove temp directories
rm -Rf dbtest/
rm -Rf test/

# create jar
mvn clean package

# build db docker containers
docker-compose build

# start db docker containers
docker-compose up -d

# wait for docker containers
echo "waiting for docker containers..."
sleep 30s

# run tests
java -jar target/uimadatabase-0.0.1-SNAPSHOT-jar-with-dependencies.jar writer XMI
#java -jar target/uimadatabase-0.0.1-SNAPSHOT-jar-with-dependencies.jar writer Mysql
java -jar target/uimadatabase-0.0.1-SNAPSHOT-jar-with-dependencies.jar writer Basex
java -jar target/uimadatabase-0.0.1-SNAPSHOT-jar-with-dependencies.jar writer Cassandra
java -jar target/uimadatabase-0.0.1-SNAPSHOT-jar-with-dependencies.jar writer Mongo
java -jar target/uimadatabase-0.0.1-SNAPSHOT-jar-with-dependencies.jar writer Neo4j
java -jar target/uimadatabase-0.0.1-SNAPSHOT-jar-with-dependencies.jar reader XMI
#java -jar target/uimadatabase-0.0.1-SNAPSHOT-jar-with-dependencies.jar reader Mysql
java -jar target/uimadatabase-0.0.1-SNAPSHOT-jar-with-dependencies.jar reader Basex
java -jar target/uimadatabase-0.0.1-SNAPSHOT-jar-with-dependencies.jar reader Cassandra
java -jar target/uimadatabase-0.0.1-SNAPSHOT-jar-with-dependencies.jar reader Mongo
java -jar target/uimadatabase-0.0.1-SNAPSHOT-jar-with-dependencies.jar reader Neo4j

# shutdown db docker containers
docker-compose down