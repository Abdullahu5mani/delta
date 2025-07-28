package statistics.presenter;

import meals.models.meal.Meal;
import meals.services.QueryMealsService;
import statistics.service.StatisticsService;
import statistics.view.ISwapComparisonView;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class SwapComparisonPresenter {
    private final ISwapComparisonView view;
    private final StatisticsService statisticsService;
    
    public SwapComparisonPresenter(ISwapComparisonView view, StatisticsService statisticsService) {
        this.view = view;
        this.statisticsService = statisticsService;
    }
    
    /**
     * Initialize the presenter and set up view callbacks
     */
    public void initialize() {
        view.setOnUpdateSwapComparison(this::handleSwapComparison);
        view.setOnUpdateMealListComparison(this::handleMealListComparison);
    }
    
    /**
     * Handle swap comparison requests from the view
     */
    private void handleSwapComparison(Object selectedSwap) {
        try {
            // This would be implemented based on the actual swap object structure
            // For now, we'll show an error since we don't have proper swap data
            view.showError("Swap comparison not yet implemented - needs proper swap data structure");
        } catch (Exception e) {
            System.err.println("❌ Error in SwapComparisonPresenter: " + e.getMessage());
            e.printStackTrace();
            view.showError("Error in swap comparison: " + e.getMessage());
        }
    }
    
    /**
     * Handle meal list comparison requests from the view
     */
    private void handleMealListComparison(List<Object> originalMeals, List<Object> afterSwapMeals, List<String> goalNutrientNames) {
        try {
            // Convert Object lists to Meal lists
            List<Meal> beforeMeals = (List<Meal>) (List<?>) originalMeals;
            List<Meal> afterMeals = (List<Meal>) (List<?>) afterSwapMeals;
            
            // Calculate nutrient totals for both meal lists
            Map<String, Double> beforeNutrients = statisticsService.calculateNutrientTotalsFromMeals(beforeMeals);
            Map<String, Double> afterNutrients = statisticsService.calculateNutrientTotalsFromMeals(afterMeals);
            
            // Delegate UI creation to the view - NO JPanel creation here!
            // The view will handle creating the chart panel and updating the display
            ((statistics.view.SwapComparisonView) view).handleMealListComparisonWithData(beforeNutrients, afterNutrients, goalNutrientNames);
            
        } catch (Exception e) {
            System.err.println("❌ Error in meal list comparison: " + e.getMessage());
            e.printStackTrace();
            view.showError("Error in meal list comparison: " + e.getMessage());
        }
    }
} 