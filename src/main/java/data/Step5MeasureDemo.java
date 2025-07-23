package data;

import java.util.List;

/**
 * Simple demo to show all measures and their corresponding conversion factors for each food.
 * Follows the same pattern as other demos in the codebase.
 */
public class Step5MeasureDemo {

    public static void main(String[] args) {
        System.out.println("📏 === MEASURE & CONVERSION FACTOR DEMO ===");

        try {
            // Initialize database connection and DAOs
            IDatabaseDriver driver = new MySQLDriver(MySQLConfig.instance());
            MeasureDAO measureDAO = new MeasureDAO(driver);
            ConversionFactorDAO conversionFactorDAO = new ConversionFactorDAO(driver);
            FoodDAO foodDAO = new FoodDAO(driver, new FoodGroupDAO(driver));

            System.out.println("✅ Database connection established");

            // Import CSV data
            System.out.println("\n📥 Importing measures and conversion factors...");
            csv.CSVImportService csvImporter = new csv.CSVImportService(driver);
            csvImporter.load("src/main/java/csv/MEAL_LOGGING_MEASURES.csv", "measures");
            csvImporter.load("src/main/java/csv/MEAL_LOGGING_CONVERSION_FACTORS.csv", "conversion_factors");
            System.out.println("✅ Import complete");

            // Demo 1: Show all available measures
            System.out.println("\n" + "=".repeat(60));
            System.out.println("📏 DEMO 1: ALL AVAILABLE MEASURES");
            System.out.println("=".repeat(60));

            List<Measure> allMeasures = measureDAO.findAll();
            for (Measure measure : allMeasures) {
                System.out.printf("• ID: %-3d | %-15s | %s\n", 
                    measure.getMeasureId(), 
                    measure.getMeasureDescription(), 
                    measure.getCommonName());
            }
            System.out.println("Total measures: " + allMeasures.size());

            // Demo 2: Show conversion factors for each food
            System.out.println("\n" + "=".repeat(60));
            System.out.println("🍽️  DEMO 2: CONVERSION FACTORS BY FOOD");
            System.out.println("=".repeat(60));

            // Get our test foods (from meal logging dataset)
            Integer[] testFoodIds = {5, 61, 129, 5585}; // Chow mein, Milk, Egg, Tortilla chips

            for (Integer foodId : testFoodIds) {
                Food food = foodDAO.findById(foodId);
                if (food != null) {
                    System.out.printf("\n🍽️  Food: %s (ID: %d)\n", food.getFoodDescription(), foodId);
                    System.out.println("-".repeat(50));

                    List<ConversionFactor> conversions = conversionFactorDAO.findByFoodId(foodId);
                    if (!conversions.isEmpty()) {
                        for (ConversionFactor cf : conversions) {
                            Measure measure = measureDAO.findById(cf.getMeasureId());
                            if (measure != null) {
                                System.out.printf("   %-25s = %.3f grams\n", 
                                    measure.getCommonName(), 
                                    cf.getConversionFactorValue());
                            }
                        }
                        System.out.println("   Available serving sizes: " + conversions.size());
                    } else {
                        System.out.println("   ❌ No conversion factors available");
                    }
                }
            }

            // Demo 3: Show which foods use each measure
            System.out.println("\n" + "=".repeat(60));
            System.out.println("📊 DEMO 3: FOODS BY MEASURE TYPE");
            System.out.println("=".repeat(60));

            for (Measure measure : allMeasures) {
                List<ConversionFactor> conversionsForMeasure = conversionFactorDAO.findByMeasureId(measure.getMeasureId());
                if (!conversionsForMeasure.isEmpty()) {
                    System.out.printf("\n📏 Measure: %s\n", measure.getCommonName());
                    System.out.println("   Foods that support this measure:");

                    for (ConversionFactor cf : conversionsForMeasure) {
                        Food food = foodDAO.findById(cf.getFoodId());
                        if (food != null) {
                            System.out.printf("   • %s (%.3fg)\n", 
                                food.getFoodDescription(), 
                                cf.getConversionFactorValue());
                        }
                    }
                }
            }

            System.out.println("\n🎉 === DEMO COMPLETE ===");
            System.out.println("✅ Successfully demonstrated:");
            System.out.println("   • All available measurement units");
            System.out.println("   • Conversion factors for each food");
            System.out.println("   • Foods supporting each measure type");

        } catch (Exception e) {
            System.err.println("❌ Error in demo:");
            e.printStackTrace();
        }
    }
}
