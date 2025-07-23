package data;

import java.util.List;

/**
 * Demo showcasing FoodV2 enhanced nutritional profile capabilities.
 * Shows how foods can load and display their complete nutritional information.
 */
public class FoodV2Demo {
    
    public static void main(String[] args) {
        System.out.println("🍽️  === FOODV2 NUTRITIONAL PROFILE DEMO ===");
        
        try {
            // Initialize database connection and DAOs
            IDatabaseDriver driver = new MySQLDriver(MySQLConfig.instance());
            NutrientDAO nutrientDAO = new NutrientDAO(driver);
            NutrientAmountDAO nutrientAmountDAO = new NutrientAmountDAO(driver);
            FoodDAO foodDAO = new FoodDAO(driver, new FoodGroupDAO(driver));
            
            System.out.println("✅ Database connection established\n");
            
            // Get our test foods and create enhanced FoodV2 versions
            System.out.println("📋 Creating enhanced FoodV2 instances...");
            
            FoodV2 chowMein = createFoodV2(5, "Chinese dish, chow mein, chicken", nutrientAmountDAO, nutrientDAO);
            FoodV2 milk = createFoodV2(61, "Milk, fluid, partly skimmed, 2% M.F.", nutrientAmountDAO, nutrientDAO);
            FoodV2 friedEgg = createFoodV2(129, "Egg, chicken, whole, cooked, fried", nutrientAmountDAO, nutrientDAO);
            FoodV2 tortillaChips = createFoodV2(5585, "Snacks, tortilla chips, unsalted", nutrientAmountDAO, nutrientDAO);
            
            // Load nutritional profiles
            System.out.println("\n🔄 Loading nutritional profiles...");
            chowMein.loadNutritionalProfile();
            milk.loadNutritionalProfile();
            friedEgg.loadNutritionalProfile();
            tortillaChips.loadNutritionalProfile();
            
            // Demo 1: Display complete nutrition facts labels
            System.out.println("\n" + "=".repeat(60));
            System.out.println("📊 DEMO 1: COMPLETE NUTRITION FACTS LABELS");
            System.out.println("=".repeat(60));
            
            chowMein.displayNutritionFacts();
            friedEgg.displayNutritionFacts();
            
            // Demo 2: Compare macronutrients across foods
            System.out.println("\n" + "=".repeat(60));
            System.out.println("⚖️  DEMO 2: MACRONUTRIENT COMPARISON");
            System.out.println("=".repeat(60));
            
            FoodV2[] foods = {chowMein, milk, friedEgg, tortillaChips};
            
            System.out.printf("%-25s | %-12s | %-12s | %-12s | %-12s\n", 
                "FOOD", "CALORIES", "PROTEIN (g)", "FAT (g)", "CARBS (g)");
            System.out.println("-".repeat(80));
            
            for (FoodV2 food : foods) {
                FoodV2.NutritionalFact calories = food.getCalories();
                List<FoodV2.NutritionalFact> macros = food.getMacronutrients();
                
                String caloriesStr = calories != null ? String.format("%.0f", calories.getValue()) : "N/A";
                String proteinStr = getMacroValue(macros, "PROTEIN");
                String fatStr = getMacroValue(macros, "FAT");
                String carbStr = getMacroValue(macros, "CARBOHYDRATE");
                
                System.out.printf("%-25s | %-12s | %-12s | %-12s | %-12s\n", 
                    truncate(food.getFoodDescription(), 25), caloriesStr, proteinStr, fatStr, carbStr);
            }
            
            // Demo 3: Vitamin and mineral content
            System.out.println("\n" + "=".repeat(60));
            System.out.println("💊 DEMO 3: VITAMIN & MINERAL ANALYSIS");
            System.out.println("=".repeat(60));
            
            for (FoodV2 food : foods) {
                List<FoodV2.NutritionalFact> vitamins = food.getVitamins();
                List<FoodV2.NutritionalFact> minerals = food.getMinerals();
                
                System.out.printf("\n🍽️  %s:\n", food.getFoodDescription().toUpperCase());
                System.out.printf("   💊 Vitamins: %d   ⛏️  Minerals: %d\n", vitamins.size(), minerals.size());
                
                if (!vitamins.isEmpty()) {
                    System.out.println("   Top vitamins:");
                    for (int i = 0; i < Math.min(3, vitamins.size()); i++) {
                        FoodV2.NutritionalFact vitamin = vitamins.get(i);
                        System.out.printf("     • %s: %.2f %s\n", 
                            vitamin.getName(), vitamin.getValue(), vitamin.getUnit());
                    }
                }
                
                if (!minerals.isEmpty()) {
                    System.out.println("   Top minerals:");
                    for (int i = 0; i < Math.min(3, minerals.size()); i++) {
                        FoodV2.NutritionalFact mineral = minerals.get(i);
                        System.out.printf("     • %s: %.2f %s\n", 
                            mineral.getName(), mineral.getValue(), mineral.getUnit());
                    }
                }
            }
            
            // Demo 4: Nutritional summaries
            System.out.println("\n" + "=".repeat(60));
            System.out.println("📈 DEMO 4: NUTRITIONAL PROFILE SUMMARIES");
            System.out.println("=".repeat(60));
            
            for (FoodV2 food : foods) {
                System.out.printf("🍽️  %-30s: %s\n", 
                    food.getFoodDescription(), food.getNutritionalSummary());
            }
            
            // Demo 5: Specific nutrient lookup
            System.out.println("\n" + "=".repeat(60));
            System.out.println("🔍 DEMO 5: SPECIFIC NUTRIENT LOOKUP");
            System.out.println("=".repeat(60));
            
            System.out.println("Looking up SODIUM content across foods:");
            int sodiumId = 307; // Sodium
            
            for (FoodV2 food : foods) {
                FoodV2.NutritionalFact sodium = food.getNutritionalFact(sodiumId);
                if (sodium != null) {
                    System.out.printf("   🍽️  %-25s: %s\n", 
                        truncate(food.getFoodDescription(), 25), sodium.toString());
                } else {
                    System.out.printf("   🍽️  %-25s: No sodium data\n", 
                        truncate(food.getFoodDescription(), 25));
                }
            }
            
            System.out.println("\n🎉 === DEMO COMPLETE ===");
            System.out.println("✅ FoodV2 successfully demonstrated enhanced nutritional capabilities!");
            
        } catch (Exception e) {
            System.err.println("❌ Error in FoodV2 Demo:");
            e.printStackTrace();
        }
    }
    
    /**
     * Helper method to create FoodV2 instances.
     */
    private static FoodV2 createFoodV2(Integer foodId, String description, 
                                      NutrientAmountDAO nutrientAmountDAO, NutrientDAO nutrientDAO) {
        return new FoodV2(foodId, foodId, description, nutrientAmountDAO, nutrientDAO);
    }
    
    /**
     * Helper method to get macronutrient value by name.
     */
    private static String getMacroValue(List<FoodV2.NutritionalFact> macros, String nutrientName) {
        for (FoodV2.NutritionalFact macro : macros) {
            if (macro.getName().contains(nutrientName)) {
                return String.format("%.1f", macro.getValue());
            }
        }
        return "N/A";
    }
    
    /**
     * Helper method to truncate long strings.
     */
    private static String truncate(String str, int maxLength) {
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
}
