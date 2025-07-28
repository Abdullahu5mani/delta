package shared.utils;

import meals.models.nutrient.Nutrient;
import meals.models.meal.Meal;
import meals.models.meal.MealItem;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Utility class for calculating nutritional information.
 * Handles nutrient calculations and data processing for meal analysis.
 */
public class NutritionCalculator {
    
    private static final float ZERO_THRESHOLD = 0.001f;
    
    /**
     * Calculates the total nutritional content for a meal item.
     * @param nutrientAmounts The base nutrient amounts per unit
     * @param quantity The quantity of the food item
     * @return A list of nutrient entries sorted by amount (highest to lowest)
     */
    public static List<NutrientEntry> calculateNutrientsForItem(Map<Nutrient, Float> nutrientAmounts, float quantity) {
        return nutrientAmounts.entrySet().stream()
                .map(entry -> new NutrientEntry(
                    entry.getKey(),
                    entry.getValue() * quantity,
                    entry.getKey().getNutrientUnit()
                ))
                .filter(entry -> entry.amount > ZERO_THRESHOLD)
                .sorted((e1, e2) -> Float.compare(e2.amount, e1.amount))
                .collect(Collectors.toList());
    }
    
    /**
     * Calculates aggregate nutrient statistics for an entire meal.
     * @param meal The meal to calculate aggregate nutrition for
     * @return A list of nutrient entries representing the total nutrition for the meal
     */
    public static List<NutrientEntry> calculateAggregateNutrientsForMeal(Meal meal) {
        Map<Nutrient, Float> totalNutrients = new HashMap<>();
        
        // Sum up nutrients from all meal items
        for (MealItem item : meal.getMealItems()) {
            Map<Nutrient, Float> itemNutrients = item.getFood().getNutrientAmounts();
            float quantity = item.getQuantity();
            
            for (Map.Entry<Nutrient, Float> entry : itemNutrients.entrySet()) {
                Nutrient nutrient = entry.getKey();
                float amount = entry.getValue() * quantity;
                
                totalNutrients.merge(nutrient, amount, Float::sum);
            }
        }
        
        // Convert to sorted list
        return totalNutrients.entrySet().stream()
                .map(entry -> new NutrientEntry(
                    entry.getKey(),
                    entry.getValue(),
                    entry.getKey().getNutrientUnit()
                ))
                .filter(entry -> entry.amount > ZERO_THRESHOLD)
                .sorted((e1, e2) -> Float.compare(e2.amount, e1.amount))
                .collect(Collectors.toList());
    }
    
    /**
     * Represents a nutrient with its calculated amount and unit.
     */
    public static class NutrientEntry {
        private final Nutrient nutrient;
        private final float amount;
        private final String unit;
        
        public NutrientEntry(Nutrient nutrient, float amount, String unit) {
            this.nutrient = nutrient;
            this.amount = amount;
            this.unit = unit;
        }
        
        public Nutrient getNutrient() { return nutrient; }
        public float getAmount() { return amount; }
        public String getUnit() { return unit; }
        public String getNutrientName() { return nutrient.getNutrientName(); }
        public String getFormattedAmount() { return String.format("%.2f", amount); }
    }
} 