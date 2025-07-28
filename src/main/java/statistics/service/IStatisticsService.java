package statistics.service;

import meals.models.meal.Meal;
import java.util.List;
import java.util.Map;

/**
 * Interface for statistics service operations.
 * Provides methods for calculating and analyzing nutritional data from meals.
 */
public interface IStatisticsService {
    
    /**
     * Converts nutrient amounts to grams for consistent comparison.
     * 
     * @param value The amount to convert
     * @param unit The unit of measurement (g, mg, µg, IU, kcal, etc.)
     * @return The amount converted to grams
     */
    double convertToGrams(double value, String unit);
    
    /**
     * Filters out water and bulk nutrients that would dominate the visualization.
     * 
     * @param nutrientName The name of the nutrient to check
     * @return true if the nutrient should be excluded from main visualization
     */
    boolean isWaterOrBulk(String nutrientName);
    
    /**
     * Checks if a nutrient is a bioactive compound that we track separately.
     * 
     * @param nutrientName The name of the nutrient to check
     * @return true if the nutrient is a bioactive compound
     */
    boolean isBioactiveCompound(String nutrientName);
    
    /**
     * Calculates totals for excluded bioactive compounds (alcohol, caffeine, theobromine, calories).
     * These are tracked separately from main nutrition visualization.
     * 
     * @param meals List of meals to analyze
     * @return Map containing totals for alcohol, caffeine, theobromine, and calories
     */
    Map<String, Double> calculateExcludedCompounds(List<Meal> meals);
    
    /**
     * Creates a formatted summary message for excluded compounds.
     * 
     * @param excludedCompounds Map of excluded compound totals
     * @return Formatted summary string
     */
    String getExcludedCompoundsSummary(Map<String, Double> excludedCompounds);
    
    /**
     * Creates a formatted summary message for excluded compounds with daily averaging.
     * 
     * @param excludedCompounds Map of excluded compound totals
     * @param numberOfDays Number of days to average over
     * @return Formatted summary string with daily averages
     */
    String getExcludedCompoundsSummary(Map<String, Double> excludedCompounds, double numberOfDays);
    
    /**
     * Calculates total nutrients from all meals, converting units to grams for comparison.
     * 
     * @param meals List of meals to analyze
     * @return Map of nutrient names to total amounts in grams
     */
    Map<String, Double> calculateNutrientTotalsFromMeals(List<Meal> meals);
    
    /**
     * Converts cumulative nutrient totals to daily averages.
     * 
     * @param totals Cumulative nutrient totals
     * @param numberOfDays Number of days in the selected range
     * @return Daily average nutrient amounts
     */
    Map<String, Double> convertToDailyAverages(Map<String, Double> totals, double numberOfDays);
} 