package data;

import csv.CSVImportService;
import java.util.List;

/**
 * Step 3: Import and test Food data from FOOD NAME.csv
 * This imports 5,694 food items and links them to food groups.
 */
public class Step3FoodTest {
    
    public static void main(String[] args) {
        System.out.println("=== STEP 3: FOOD IMPORT TEST ===");
        
        try {
            // Initialize database connection and DAOs
            IDatabaseDriver driver = new MySQLDriver(MySQLConfig.instance());
            FoodGroupDAO foodGroupDAO = new FoodGroupDAO(driver);
            FoodDAO foodDAO = new FoodDAO(driver, foodGroupDAO);
            
            System.out.println("Database connection established.");
            
            // Import CSV data
            System.out.println("\nImporting MEAL_LOGGING_FOODS.csv...");
            CSVImportService csvImporter = new CSVImportService(new MySQLDriver(MySQLConfig.instance()));
            csvImporter.load("src/main/java/csv/MEAL_LOGGING_FOODS.csv", "foods");
            System.out.println("Import complete!");
            
            // Test: Count total foods
            int totalFoods = foodDAO.count();
            System.out.printf("\n✅ Total foods in database: %d\n", totalFoods);
            
            // Test: Display first 10 foods with their food groups
            System.out.println("\n📋 First 10 Foods:");
            System.out.println("ID\tCode\tDescription\t\t\t\tFood Group");
            System.out.println("---\t----\t-----------\t\t\t\t----------");
            
            List<Food> allFoods = foodDAO.findAll();
            for (int i = 0; i < Math.min(10, allFoods.size()); i++) {
                Food food = allFoods.get(i);
                String desc = food.getFoodDescription();
                if (desc.length() > 25) desc = desc.substring(0, 25) + "...";
                
                System.out.printf("%d\t%d\t%-28s\t%s\n", 
                    food.getFoodId(), 
                    food.getFoodCode(),
                    desc,
                    food.getFoodGroupName()
                );
            }
            
            // Test: Find foods by group
            System.out.println("\n🥛 Foods in Dairy and Egg Products (Group 1):");
            List<Food> dairyFoods = foodDAO.findAll().stream()
                .filter(f -> f.getFoodGroupId() != null && f.getFoodGroupId() == 1)
                .limit(5)
                .toList();
                
            for (Food food : dairyFoods) {
                System.out.printf("  • %s (ID: %d)\n", food.getFoodDescription(), food.getFoodId());
            }
            
            // Test: Find foods by ID - using meal logging food IDs
            System.out.println("\n🔍 Testing findById for specific meal foods:");
            int[] testIds = {5, 129, 2873, 5575}; // Chow mein, fried egg, coffee, ham
            
            for (int id : testIds) {
                Food food = foodDAO.findById(id);
                if (food != null) {
                    System.out.printf("  ID %d: %s [%s]\n", 
                        id, food.getFoodDescription(), food.getFoodGroupName());
                } else {
                    System.out.printf("  ID %d: Not found\n", id);
                }
            }
            
            // Test: Display foods with scientific names
            System.out.println("\n🧬 Foods with Scientific Names:");
            List<Food> scientificFoods = foodDAO.findAll().stream()
                .filter(f -> f.getScientificName() != null && !f.getScientificName().trim().isEmpty())
                .limit(5)
                .toList();
                
            for (Food food : scientificFoods) {
                System.out.printf("  • %s - %s\n", 
                    food.getFoodDescription(), food.getScientificName());
            }
            
            System.out.println("\n✅ Step 3 Complete! Food data successfully imported and tested.");
            System.out.println("📊 Ready for Step 4: Nutrient Amounts (nutrition facts per food)");
            
        } catch (Exception e) {
            System.err.println("❌ Error in Step 3:");
            e.printStackTrace();
        }
    }
} 