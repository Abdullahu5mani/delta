package statistics.view;

import java.util.List;
import meals.models.food.Food;
import meals.models.meal.Meal;

public interface ISwapComparisonView {
    
    /** Register a handler fired when swap comparison is requested. */
    void setOnUpdateSwapComparison(SwapComparisonCallback callback);
    
    /** Register a handler fired when meal list comparison is requested. */
    void setOnUpdateMealListComparison(MealListComparisonCallback callback);
    
    /** Show the main panel containing the UI. */
    javax.swing.JPanel getMainPanel();
    
    /** Update the food comparison tab with new chart. */
    void updateSwapComparison(javax.swing.JPanel chartPanel);
    
    /** Update the meal list impact tab with new chart. */
    void updateMealListComparison(javax.swing.JPanel chartPanel);
    
    /** Update the goal nutrient trends tab with new chart. */
    void updateGoalNutrientTrends(javax.swing.JPanel chartPanel);
    
    /** Show an error message to the user. */
    void showError(String errorMessage);
    
    /** Callback interface for swap comparison requests. */
    @FunctionalInterface
    interface SwapComparisonCallback {
        void onUpdateSwapComparison(Object selectedSwap);
    }
    
    /** Callback interface for meal list comparison requests. */
    @FunctionalInterface
    interface MealListComparisonCallback {
        void onUpdateMealListComparison(List<Object> originalMeals, List<Object> afterSwapMeals, List<String> goalNutrientNames);
    }
} 