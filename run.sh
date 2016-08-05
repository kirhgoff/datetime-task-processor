#!/bin/bash
mvn clean install
java -jar target/datetimeprocessor-1.0-SNAPSHOT-executable.jar
