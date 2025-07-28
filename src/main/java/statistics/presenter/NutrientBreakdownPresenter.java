package statistics.presenter;

import meals.models.meal.Meal;
import meals.services.QueryMealsService;
import statistics.service.StatisticsService;
import statistics.view.INutrientBreakdownView;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class NutrientBreakdownPresenter {
    private final INutrientBreakdownView view;
    private final StatisticsService statisticsService;
    
    public NutrientBreakdownPresenter(INutrientBreakdownView view, StatisticsService statisticsService) {
        this.view = view;
        this.statisticsService = statisticsService;
    }
    
    /**
     * Initialize the presenter and set up view callbacks
     */
    public void initialize() {
        view.setOnGenerateChartRequested(this::handleGenerateChartRequest);
    }
    
    /**
     * Handle chart generation requests from the view
     */
    private void handleGenerateChartRequest(Date startDate, Date endDate) {
        try {
            // Get meals for the date range
            QueryMealsService.QueryMealsServiceOutput result = QueryMealsService.instance().getMealsByDate(startDate, endDate);
            List<Meal> meals = result.getMeals();
            
            if (meals.isEmpty()) {
                view.showNoData();
                return;
            }
            
            // Calculate nutrient totals from real meal data
            Map<String, Double> nutrientTotals = statisticsService.calculateNutrientTotalsFromMeals(meals);
            
            if (nutrientTotals.isEmpty()) {
                view.showNoData();
                return;
            }
            
            // Calculate number of days in the selected range
            long daysDifference = (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24) + 1;
            double numberOfDays = Math.max(1.0, (double) daysDifference); // At least 1 day
            
            // Calculate excluded compounds (alcohol, caffeine, theobromine, calories)
            Map<String, Double> excludedCompounds = statisticsService.calculateExcludedCompounds(meals);
            
            // Convert totals to daily averages if multiple days
            Map<String, Double> displayNutrients = nutrientTotals;
            Map<String, Double> displayExcluded = excludedCompounds;
            
            if (numberOfDays > 1) {
                displayNutrients = statisticsService.convertToDailyAverages(nutrientTotals, numberOfDays);
                displayExcluded = statisticsService.convertToDailyAverages(excludedCompounds, numberOfDays);
                System.out.println("📊 Converting to daily averages for " + numberOfDays + " days");
            }
            
            // Delegate UI creation to the view - NO JPanel creation here!
            // The view will handle creating the chart panel and updating the display
            ((statistics.view.NutrientBreakdownView) view).handleNutrientBreakdownData(displayNutrients, meals.size(), displayExcluded, numberOfDays);
            
        } catch (Exception e) {
            System.err.println("❌ Error in NutrientBreakdownPresenter: " + e.getMessage());
            e.printStackTrace();
            view.showError("Failed to generate nutrient breakdown: " + e.getMessage());
        }
    }
}
