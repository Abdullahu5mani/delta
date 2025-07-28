package statistics.service;

import meals.models.food.Food;
import meals.models.food.FoodGroup;
import meals.models.food.Measure;
import meals.models.meal.Meal;
import meals.models.meal.MealItem;
import meals.models.nutrient.Nutrient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;


public class StatisticsService implements IStatisticsService {
    
    /**
     * Canada Food Guide categories based on the 2019 recommendations.
     */
    public enum CanadaFoodGuideCategory {
        VEGETABLES_AND_FRUITS("Vegetables & Fruits", 0.50), // 50%
        WHOLE_GRAINS("Whole Grains", 0.25),                 // 25%
        PROTEIN_FOODS("Protein Foods", 0.25),              // 25%
        OTHER("Other", 0.0);                                // 0% (not part of CFG recommendations)
        
        private final String displayName;
        private final double targetPercentage;
        
        CanadaFoodGuideCategory(String displayName, double targetPercentage) {
            this.displayName = displayName;
            this.targetPercentage = targetPercentage;
        }
        
        public String getDisplayName() { return displayName; }
        public double getTargetPercentage() { return targetPercentage; }
    }
    
    /**
     * Result of Canada Food Guide analysis.
     */
    public static class CFGAnalysisResult {
        private final Map<CanadaFoodGuideCategory, Double> actualPercentages;
        private final Map<CanadaFoodGuideCategory, Double> targetPercentages;
        private final Map<CanadaFoodGuideCategory, Double> actualGrams;
        private final double totalGrams;
        private final List<String> unrecognizedFoods;
        private final boolean hasDatasetLimitations;
        
        public CFGAnalysisResult(Map<CanadaFoodGuideCategory, Double> actualPercentages,
                                Map<CanadaFoodGuideCategory, Double> targetPercentages,
                                Map<CanadaFoodGuideCategory, Double> actualGrams,
                                double totalGrams,
                                List<String> unrecognizedFoods) {
            this.actualPercentages = actualPercentages;
            this.targetPercentages = targetPercentages;
            this.actualGrams = actualGrams;
            this.totalGrams = totalGrams;
            this.unrecognizedFoods = unrecognizedFoods;
            this.hasDatasetLimitations = true; // Our dataset is limited (9/22 food groups)
        }
        
        public Map<CanadaFoodGuideCategory, Double> getActualPercentages() { return actualPercentages; }
        public Map<CanadaFoodGuideCategory, Double> getTargetPercentages() { return targetPercentages; }
        public Map<CanadaFoodGuideCategory, Double> getActualGrams() { return actualGrams; }
        public double getTotalGrams() { return totalGrams; }
        public List<String> getUnrecognizedFoods() { return unrecognizedFoods; }
        public boolean hasDatasetLimitations() { return hasDatasetLimitations; }
    }

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
    
    @Override
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
            case "kcal" -> value;                      // Kilocalories (treat as grams for visualization)
            case "kj" -> value / 4184.0;               // Kilojoules to grams (rough energy conversion)
            case "ne" -> value / 1000.0;               // Niacin Equivalent (mg to g)
            default -> value;
        };
    }
    
    @Override
    public boolean isWaterOrBulk(String nutrientName) {
        String name = nutrientName.toLowerCase();
        return name.contains("moisture") || 
               name.contains("ash") || 
               name.contains("alcohol") ||
               name.contains("caffeine") ||
               name.contains("theobromine") ||
               name.contains("energy") || name.contains("kcal");
    }
    
    @Override
    public boolean isBioactiveCompound(String nutrientName) {
        String name = nutrientName.toLowerCase();
        return name.contains("alcohol") ||
               name.contains("caffeine") ||
               name.contains("theobromine") ||
               (name.contains("energy") && name.contains("kilocalories")) ||
               name.contains("kcal");
    }
    
    @Override
    public Map<String, Double> calculateExcludedCompounds(List<Meal> meals) {
        Map<String, Double> excludedTotals = new HashMap<>();
        excludedTotals.put("alcohol", 0.0);
        excludedTotals.put("caffeine", 0.0);
        excludedTotals.put("theobromine", 0.0);
        excludedTotals.put("calories", 0.0);
        
        System.out.println("🧪 Calculating excluded compound totals from " + meals.size() + " meals...");
        
        for (Meal meal : meals) {
            for (MealItem mealItem : meal.getMealItems()) {
                Food food = mealItem.getFood();
                Float quantity = mealItem.getQuantity();
                Measure measure = mealItem.getSelectedMeasure();
                Float conversionFactor = measure.getConversionValue();
                
                Map<Nutrient, Float> nutrients = food.getNutrientAmounts();
                
                for (Map.Entry<Nutrient, Float> entry : nutrients.entrySet()) {
                    Nutrient nutrient = entry.getKey();
                    Float amount = entry.getValue();
                    
                    if (amount != null && amount > 0) {
                        String nutrientName = nutrient.getNutrientName();
                        String nutrientUnit = nutrient.getNutrientUnit();
                        
                        // Only process bioactive compounds
                        if (isBioactiveCompound(nutrientName)) {
                            // Correct scaling: base_amount × conversion_factor × quantity
                            double scaledAmount = amount * conversionFactor * quantity;
                            
                            // Convert to grams for consistent comparison
                            double amountInGrams = convertToGrams(scaledAmount, nutrientUnit);
                            
                            // Categorize the compound
                            String name = nutrientName.toLowerCase();
                            if (name.contains("alcohol")) {
                                excludedTotals.merge("alcohol", amountInGrams, Double::sum);
                                System.out.println("🍷 Found alcohol: " + amountInGrams + "g");
                            } else if (name.contains("caffeine")) {
                                excludedTotals.merge("caffeine", amountInGrams, Double::sum);
                                System.out.println("☕ Found caffeine: " + amountInGrams + "g");
                            } else if (name.contains("theobromine")) {
                                excludedTotals.merge("theobromine", amountInGrams, Double::sum);
                                System.out.println("🍫 Found theobromine: " + amountInGrams + "g");
                            } else if (name.contains("energy") || name.contains("kcal")) {
                                // Only process kcal/kilocalories (kJ is filtered out by isBioactiveCompound)
                                excludedTotals.merge("calories", scaledAmount, Double::sum);
                                System.out.println("🔥 CALORIE DEBUG for '" + nutrientName + "':");
                                System.out.println("    Base amount: " + amount + " " + nutrientUnit);
                                System.out.println("    Conversion factor: " + conversionFactor);
                                System.out.println("    Quantity: " + quantity);
                                System.out.println("    Scaled amount: " + scaledAmount + " " + nutrientUnit + " (= " + amount + " × " + conversionFactor + " × " + quantity + ")");
                                System.out.println("    Final calories: " + scaledAmount + " kcal");
                                System.out.println("    Food: " + food.getFoodDescription());
                            }
                        }
                    }
                }
            }
        }
        
        System.out.println("🧪 EXCLUDED COMPOUNDS SUMMARY:");
        System.out.println("   Alcohol: " + String.format("%.3f", excludedTotals.get("alcohol")) + "g");
        System.out.println("   Caffeine: " + String.format("%.3f", excludedTotals.get("caffeine")) + "g");
        System.out.println("   Theobromine: " + String.format("%.3f", excludedTotals.get("theobromine")) + "g");
        System.out.println("   Calories: " + String.format("%.2f", excludedTotals.get("calories")) + "kcal");
        System.out.println("🔥 TOTAL CALORIES ACCUMULATED: " + String.format("%.2f", excludedTotals.get("calories")) + "kcal");
        
        return excludedTotals;
    }
    
    @Override
    public String getExcludedCompoundsSummary(Map<String, Double> excludedCompounds) {
        return getExcludedCompoundsSummary(excludedCompounds, 1.0);
    }
    
    @Override
    public String getExcludedCompoundsSummary(Map<String, Double> excludedCompounds, double numberOfDays) {
        double alcohol = excludedCompounds.getOrDefault("alcohol", 0.0);
        double caffeine = excludedCompounds.getOrDefault("caffeine", 0.0);
        double theobromine = excludedCompounds.getOrDefault("theobromine", 0.0);
        double calories = excludedCompounds.getOrDefault("calories", 0.0);
        
        if (numberOfDays > 1) {
            return String.format("You consumed %.2f kcal/day, %.3fg/day alcohol, %.3fg/day caffeine, and %.3fg/day theobromine. " +
                               "Calories and bioactive compounds are excluded from nutrient visualization. " +
                               "Moisture and ash from Canadian Nutrient File are also excluded.",
                               calories, alcohol, caffeine, theobromine);
        } else {
            return String.format("You consumed %.2f kcal, %.3fg alcohol, %.3fg caffeine, and %.3fg theobromine. " +
                               "Calories and bioactive compounds are excluded from nutrient visualization. " +
                               "Moisture and ash from Canadian Nutrient File are also excluded.",
                               calories, alcohol, caffeine, theobromine);
        }
    }
    
    @Override
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
    
    @Override
    public Map<String, Double> convertToDailyAverages(Map<String, Double> totals, double numberOfDays) {
        if (numberOfDays <= 0) {
            return totals; // Fallback to totals if invalid days
        }
        
        Map<String, Double> dailyAverages = new HashMap<>();
        for (Map.Entry<String, Double> entry : totals.entrySet()) {
            dailyAverages.put(entry.getKey(), entry.getValue() / numberOfDays);
        }
        return dailyAverages;
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
    
    /**
     * Maps food groups to Canada Food Guide categories.
     * Updated with more comprehensive mapping based on available CNF food groups.
     */
    public CanadaFoodGuideCategory mapFoodGroupToCFG(FoodGroup foodGroup) {
        if (foodGroup == null || foodGroup.getFoodGroupName() == null) {
            return null;
        }
        
        String groupName = foodGroup.getFoodGroupName().toLowerCase();
        
        // NOTE: Our dataset is limited - we only have 9 out of 22 official CNF food groups
        // Missing: Grains/Breads (Groups 1-4), Fruits (Group 9), Fish (Group 15), etc.
        // This affects the accuracy of our Canada Food Guide analysis
        
        // Vegetables & Fruits (50%)
        if (groupName.contains("vegetable") || groupName.contains("fruit")) {
            return CanadaFoodGuideCategory.VEGETABLES_AND_FRUITS;
        }
        
        // Whole Grains (25%) - includes cereals and grain-based foods
        if (groupName.contains("cereal") || groupName.contains("grain") || 
            groupName.contains("bread") || groupName.contains("pasta") ||
            groupName.contains("breakfast")) {
            return CanadaFoodGuideCategory.WHOLE_GRAINS;
        }
        
        // Protein Foods (25%) - includes dairy, eggs, meat, poultry, fish
        if (groupName.contains("dairy") || groupName.contains("egg") || 
            groupName.contains("poultry") || groupName.contains("pork") ||
            groupName.contains("beef") || groupName.contains("fish") ||
            groupName.contains("legume") || groupName.contains("nut") ||
            groupName.contains("meat") || groupName.contains("products")) {
            return CanadaFoodGuideCategory.PROTEIN_FOODS;
        }
        
        // Handle Mixed Dishes - categorize based on likely main component
        if (groupName.contains("mixed")) {
            // For mixed dishes, we'll assign them to the most likely category
            // This is a simplification, but better than excluding them entirely
            return CanadaFoodGuideCategory.PROTEIN_FOODS; // Most mixed dishes contain protein
        }
        
        // Handle Beverages - categorize based on type
        if (groupName.contains("beverages")) {
            // We'll return null for most beverages but handle specific cases in food-level mapping
            return null;
        }
        
        // Categorize fats, oils, snacks, and other non-CFG foods as "Other"
        if (groupName.contains("fats") || groupName.contains("oils") || 
            groupName.contains("snacks")) {
            return CanadaFoodGuideCategory.OTHER;
        }
        
        // For any other unrecognized groups, categorize as "Other"
        return CanadaFoodGuideCategory.OTHER;
    }
    
    /**
     * Maps individual foods to CFG categories when group-level mapping isn't sufficient.
     */
    public CanadaFoodGuideCategory mapFoodToCFG(Food food) {
        // First try group-level mapping
        CanadaFoodGuideCategory groupCategory = mapFoodGroupToCFG(food.getFoodGroup());
        if (groupCategory != null) {
            return groupCategory;
        }
        
        // If group mapping returns null, try food-level mapping
        String foodDescription = food.getFoodDescription().toLowerCase();
        String groupName = food.getFoodGroup().getFoodGroupName().toLowerCase();
        
        // Special handling for beverages
        if (groupName.contains("beverages")) {
            // Milk-based beverages go to protein
            if (foodDescription.contains("milk") || foodDescription.contains("dairy") || 
                foodDescription.contains("chocolate") && foodDescription.contains("milk")) {
                return CanadaFoodGuideCategory.PROTEIN_FOODS;
            }
            
            // Fruit/vegetable juices go to vegetables & fruits
            if (foodDescription.contains("juice") && !foodDescription.contains("coffee")) {
                return CanadaFoodGuideCategory.VEGETABLES_AND_FRUITS;
            }
            
            // Coffee, tea, soda, and other beverages go to "Other"
            return CanadaFoodGuideCategory.OTHER;
        }
        
        // Special handling for mixed dishes - try to categorize by main ingredient
        if (groupName.contains("mixed")) {
            // Dishes with chicken, beef, pork, turkey -> protein
            if (foodDescription.contains("chicken") || foodDescription.contains("beef") || 
                foodDescription.contains("pork") || foodDescription.contains("turkey") ||
                foodDescription.contains("ham")) {
                return CanadaFoodGuideCategory.PROTEIN_FOODS;
            }
            
            // Dishes with potatoes, vegetables -> vegetables
            if (foodDescription.contains("potato") || foodDescription.contains("vegetable") ||
                foodDescription.contains("peas") || foodDescription.contains("corn")) {
                return CanadaFoodGuideCategory.VEGETABLES_AND_FRUITS;
            }
            
            // Default mixed dishes to protein (most contain some protein)
            return CanadaFoodGuideCategory.PROTEIN_FOODS;
        }
        
        // For other categories, keep them unrecognized
        return null;
    }
    
    /**
     * Analyzes meals against Canada Food Guide recommendations.
     */
    public CFGAnalysisResult analyzeCanadaFoodGuide(List<Meal> meals) {
        Map<CanadaFoodGuideCategory, Double> categoryTotals = new HashMap<>();
        List<String> unrecognizedFoods = new ArrayList<>();
        double totalGrams = 0.0;
        
        // Initialize category totals
        for (CanadaFoodGuideCategory category : CanadaFoodGuideCategory.values()) {
            categoryTotals.put(category, 0.0);
        }
        
        System.out.println("Analyzing " + meals.size() + " meals for Canada Food Guide alignment...");
        
        for (Meal meal : meals) {
            for (MealItem item : meal.getMealItems()) {
                Food food = item.getFood();
                double grams = convertMealItemToGrams(item);
                
                // Use enhanced food-level mapping
                CanadaFoodGuideCategory category = mapFoodToCFG(food);
                
                if (category != null) {
                    categoryTotals.merge(category, grams, Double::sum);
                    System.out.println("MAPPED: " + food.getFoodDescription() + " -> " + category.getDisplayName() + " (" + grams + "g)");
                } else {
                    unrecognizedFoods.add(food.getFoodDescription() + " (" + food.getFoodGroup().getFoodGroupName() + ")");
                    System.out.println("UNRECOGNIZED: " + food.getFoodDescription() + " in group: " + food.getFoodGroup().getFoodGroupName());
                }
                
                totalGrams += grams;
            }
        }
        
        // Calculate percentages
        Map<CanadaFoodGuideCategory, Double> percentages = new HashMap<>();
        for (CanadaFoodGuideCategory category : CanadaFoodGuideCategory.values()) {
            double grams = categoryTotals.get(category);
            double percentage = totalGrams > 0 ? (grams / totalGrams) * 100 : 0.0;
            percentages.put(category, percentage);
        }
        
        // Get target percentages
        Map<CanadaFoodGuideCategory, Double> targets = new HashMap<>();
        for (CanadaFoodGuideCategory category : CanadaFoodGuideCategory.values()) {
            targets.put(category, category.getTargetPercentage() * 100);
        }
        
        System.out.println("CFG Analysis complete: " + totalGrams + "g total, " + unrecognizedFoods.size() + " unrecognized foods");
        
        return new CFGAnalysisResult(percentages, targets, categoryTotals, totalGrams, unrecognizedFoods);
    }
    
    /**
     * Converts a meal item to grams using existing conversion logic.
     */
    private double convertMealItemToGrams(MealItem item) {
        try {
            Measure measure = item.getSelectedMeasure();
            if (measure != null) {
                return item.getQuantity() * measure.getConversionValue();
            } else {
                // Fallback: assume quantity is already in grams
                return item.getQuantity();
            }
        } catch (Exception e) {
            System.err.println("Error converting meal item to grams: " + e.getMessage());
            return item.getQuantity(); // Fallback
        }
    }
}
