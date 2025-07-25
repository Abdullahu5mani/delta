package csv;

import data.DatabaseException;
import data.MySQLConfig;
import data.MySQLDriver;

import java.io.IOException;

public class LoadNutritionDataService {
    public static void main(String[] args) throws IOException, DatabaseException {
        CSVImportService csvImporter = new CSVImportService(new MySQLDriver(MySQLConfig.instance()));
        csvImporter.load("src/main/java/csv/nutrition_data/nutrients.csv", "nutrients");
        csvImporter.load("src/main/java/csv/nutrition_data/measures.csv", "measures");
        csvImporter.load("src/main/java/csv/nutrition_data/food_groups.csv", "food_groups");
        csvImporter.load("src/main/java/csv/nutrition_data/foods.csv", "foods");
        csvImporter.load("src/main/java/csv/nutrition_data/conversion_factors.csv", "conversion_factors");
        csvImporter.load("src/main/java/csv/nutrition_data/nutrient_amounts.csv", "nutrient_amounts");
    }
}
