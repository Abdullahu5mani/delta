#!/bin/bash

echo "Step 1: Create Database"
mysql -uroot -p -h 127.0.0.1 -P 3306 < "$(pwd)/src/main/java/data/create-database.sql"
if [ $? -eq 0 ]; then
  echo "Database created successfully!"
else
  echo "An error occurred while creating the database."
  exit 1
fi

echo "Step 2: Create Service Account"
mysql -uroot -p -h 127.0.0.1 -P 3306 < "$(pwd)/src/main/java/data/create-service-account.sql"
if [ $? -eq 0 ]; then
  echo "Database created successfully!"
else
  echo "An error occurred while creating the service account."
  exit 1
fi

echo "Step 3: Seed Database"
mysql -uroot -p -h 127.0.0.1 -P 3306 < "$(pwd)/src/main/java/data/seed-database.sql"
if [ $? -eq 0 ]; then
  echo "Database seeded successfully!"
else
  echo "An error occurred while seeding the database."
  exit 1
fi

echo "Step 4: Seed Nutrition Data"
echo "This may take a while..."
mvn clean compile exec:java -Dexec.mainClass="csv.LoadNutritionDataService" -e
if [ $? -eq 0 ]; then
  echo "Database nutrition data seeded successfully!"
else
  echo "An error occurred while seeding the database."
  echo "You can also run the main method of LoadNutritionDataService directly to load the nutrition data."
  exit 1
fi
