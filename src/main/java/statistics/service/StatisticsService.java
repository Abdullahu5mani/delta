package statistics.service;

import meals.models.food.Food;
import meals.models.food.Measure;
import meals.models.meal.Meal;
import meals.models.meal.MealItem;
import meals.models.nutrient.Nutrient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsService {
    /**
     * Singleton instance of the service.
     */
    private static StatisticsService instance;

    /**
     * Prevent instantiation by clients to implement singleton pattern.
     */
    private StatisticsService() {
    }

    /**
     * @return The singleton instance of the service.
     */
    public static StatisticsService instance() {
        if (instance == null) {
            instance = new StatisticsService();
        }
        return instance;
    }
    
    /**
     * Converts nutrient amounts to grams for consistent comparison.
     */
    public double convertToGrams(double value, String unit) {
        if (unit == null) return value;
        
        return switch (unit.toLowerCase()) {
            case "g" -> value;                          // Already in grams
            case "mg" -> value / 1000.0;               // Milligrams to grams
            case "µg", "ug", "mcg" -> value / 1000000.0; // Micrograms to grams
            case "iu" -> value / 1000.0;               // International Units (rough conversion)
            case "re" -> value / 1000.0;               // Retinol Equivalents (rough conversion)
            case "dfe" -> value / 1000.0;              // Dietary Folate Equivalents (rough conversion)
            case "nfe" -> value / 1000.0;              // Niacin Equivalents (rough conversion)
            case "te" -> value / 1000.0;               // Tocopherol Equivalents (rough conversion)
            case "kcal" -> value / 1000.0;             // Kilocalories (treat as grams for visualization)
            case "kj" -> value / 4184.0;               // Kilojoules to grams (rough energy conversion)
            case "ne" -> value / 1000.0;               // Niacin Equivalent (mg to g)
            default -> {
                System.out.println("⚠️  Unknown unit '" + unit + "', treating as grams");
                yield value;
            }
        };
    }
    
    /**
     * Filters out water and bulk nutrients that would dominate the visualization.
     */
    public boolean isWaterOrBulk(String nutrientName) {
        String name = nutrientName.toLowerCase();
        return name.contains("moisture") || 
               name.contains("ash") || 
               name.contains("alcohol") ||
               name.contains("caffeine") ||
               name.contains("theobromine");
    }
    
    /**
     * Calculates total nutrients from all meals, converting units to grams for comparison.
     */
    public Map<String, Double> calculateNutrientTotalsFromMeals(List<Meal> meals) {
        Map<String, Double> allNutrientTotals = new HashMap<>();
        
        System.out.println("🧮 Calculating nutrient totals from " + meals.size() + " meals...");
        
        for (Meal meal : meals) {
            System.out.println("🔍 Processing meal: " + meal.getMealType() + " on " + meal.getCreatedAt());
            
            for (MealItem mealItem : meal.getMealItems()) {
                Food food = mealItem.getFood();
                Float quantity = mealItem.getQuantity();
                Measure measure = mealItem.getSelectedMeasure();
                Float conversionFactor = measure.getConversionValue();
                
                System.out.println("   FOOD: Processing food: " + food.getFoodDescription());
                System.out.println("        Quantity: " + quantity + " x " + measure.getName());
                System.out.println("        Conversion factor: " + conversionFactor);
                
                Map<Nutrient, Float> nutrients = food.getNutrientAmounts();
                System.out.println("        Found " + nutrients.size() + " nutrients for this food");
                
                for (Map.Entry<Nutrient, Float> entry : nutrients.entrySet()) {
                    Nutrient nutrient = entry.getKey();
                    Float amount = entry.getValue();
                    
                    if (amount != null && amount > 0) {
                        String nutrientName = nutrient.getNutrientName();
                        String nutrientUnit = nutrient.getNutrientUnit();
                        
                        System.out.println("      DEBUG: Processing nutrient '" + nutrientName + "' (" + nutrientUnit + ")");
                        
                        // Skip water and bulk nutrients that would skew visualization
                        if (StatisticsService.instance().isWaterOrBulk(nutrientName)) {
                            continue;
                        }
                        
                        // Correct scaling: base_amount × conversion_factor × quantity
                        double scaledAmount = amount * conversionFactor * quantity;
                        
                        // Convert to grams for consistent comparison
                        double amountInGrams = StatisticsService.instance().convertToGrams(scaledAmount, nutrientUnit);
                        
                        // Debug for carbohydrate specifically
                        if (nutrientName.toUpperCase().contains("CARBOHYDRATE")) {
                            System.out.println("      >>> CARB DEBUG: '" + nutrientName + "'");
                            System.out.println("          Base amount: " + amount + " " + nutrientUnit);
                            System.out.println("          Conversion factor: " + conversionFactor);
                            System.out.println("          Quantity: " + quantity);
                            System.out.println("          Scaled amount: " + scaledAmount + " " + nutrientUnit + " (= " + amount + " × " + conversionFactor + " × " + quantity + ")");
                            System.out.println("          Converted to grams: " + amountInGrams + "g");
                        }
                        
                        // Accumulate totals
                        System.out.println("      DEBUG: Adding nutrient '" + nutrientName + "' = " + amountInGrams + "g");
                        allNutrientTotals.merge(nutrientName, amountInGrams, Double::sum);
                    }
                }
            }
        }
        
        System.out.println("TOTAL: Total unique nutrients collected: " + allNutrientTotals.size());
        
        // DEBUG: Show what's actually in the map
        System.out.println("DEBUG: Final nutrient map contents:");
        for (Map.Entry<String, Double> entry : allNutrientTotals.entrySet()) {
            System.out.println("  '" + entry.getKey() + "' = " + entry.getValue() + "g");
        }
        
        // Get top 7 nutrients and group the rest
        return getTopNutrientsWithOthers(allNutrientTotals, 7);
    }
    
    /**
     * Takes the top N nutrients by amount and groups the rest as "Other nutrients".
     * Copied from NutrientBreakdownPresenter.
     */
    private Map<String, Double> getTopNutrientsWithOthers(Map<String, Double> allNutrients, int topCount) {
        Map<String, Double> result = new HashMap<>();
        double otherSum = 0.0;
        
        List<Map.Entry<String, Double>> sortedEntries = allNutrients.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .collect(Collectors.toList());
        
        for (int i = 0; i < sortedEntries.size(); i++) {
            Map.Entry<String, Double> entry = sortedEntries.get(i);
            
            if (i < topCount) {
                result.put(entry.getKey(), entry.getValue());
                System.out.println("🥇 Top " + (i+1) + ": " + entry.getKey() + " = " + 
                                 String.format("%.3f", entry.getValue()) + "g");
            } else {
                otherSum += entry.getValue();
            }
        }
        
        if (otherSum > 0) {
            result.put("Other nutrients", otherSum);
            System.out.println("📦 Other nutrients: " + String.format("%.3f", otherSum) + "g");
        }
        
        return result;
    }
}
