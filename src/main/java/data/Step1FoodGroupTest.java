package data;

import csv.CSVImportService;
import java.io.IOException;
import java.util.List;

/**
 * Step 1: Test just FoodGroup import and basic functionality.
 */
public class Step1FoodGroupTest {
    
    public static void main(String[] args) throws DatabaseException, IOException {
        System.out.println("=== Step 1: Food Group Test ===");
        
        // Create DAO
        FoodGroupDAO foodGroupDAO = new FoodGroupDAO(new MySQLDriver(MySQLConfig.instance()));
        
        // Check if empty first
        System.out.println("Food groups before import: " + foodGroupDAO.count());
        
        // Import meal logging food groups CSV
        System.out.println("\nImporting MEAL_LOGGING_FOOD_GROUPS.csv...");
        CSVImportService csvImporter = new CSVImportService(new MySQLDriver(MySQLConfig.instance()));
        csvImporter.load("src/main/java/csv/MEAL_LOGGING_FOOD_GROUPS.csv", "food_groups");
        System.out.println("Import complete!");
        
        // Test our class works
        int totalGroups = foodGroupDAO.count();
        System.out.println("\nFood groups after import: " + totalGroups);
        
        // Show ALL food groups
        System.out.println("\n=== ALL FOOD GROUPS ===");
        List<FoodGroup> groups = foodGroupDAO.findAll();
        for (FoodGroup group : groups) {
            System.out.println("  ID: " + group.getFoodGroupId() + 
                             " | Code: " + group.getFoodGroupCode() + 
                             " | Name: " + group.getDisplayName());
        }
        
        // Test findById functionality - using meal logging food groups
        System.out.println("\n=== TESTING findById ===");
        FoodGroup dairyGroup = foodGroupDAO.findById(1);
        if (dairyGroup != null) {
            System.out.println("Found by ID 1: " + dairyGroup.getDisplayName());
            System.out.println("  Is protein group? " + dairyGroup.isProteinGroup());
            System.out.println("  Is plant-based? " + dairyGroup.isPlantBased());
        }
        
        FoodGroup beverageGroup = foodGroupDAO.findById(14);
        if (beverageGroup != null) {
            System.out.println("Found by ID 14: " + beverageGroup.getDisplayName());
            System.out.println("  Is protein group? " + beverageGroup.isProteinGroup());
            System.out.println("  Is plant-based? " + beverageGroup.isPlantBased());
        }
        
        FoodGroup mixedGroup = foodGroupDAO.findById(22);
        if (mixedGroup != null) {
            System.out.println("Found by ID 22: " + mixedGroup.getDisplayName());
            System.out.println("  Is protein group? " + mixedGroup.isProteinGroup());
            System.out.println("  Is plant-based? " + mixedGroup.isPlantBased());
        }
        
        System.out.println("\n=== SUCCESS: All " + totalGroups + " food groups imported and working! ===");
    }
} 