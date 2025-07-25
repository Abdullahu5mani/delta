#!/bin/bash

echo "Loading CSV files"
mvn clean compile exec:java -Dexec.mainClass="csv.LoadNutritionDataService" -e

echo "Running tests"
mvn clean test
