package statistics.presenter;

import meals.models.food.Food;
import meals.models.food.Measure;
import meals.models.meal.Meal;
import meals.models.meal.MealItem;
import meals.models.nutrient.Nutrient;
import meals.services.QueryFoodsService;
import meals.services.QueryMealsService;
import statistics.service.StatisticsService;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Presenter for comparing nutrition between before and after swap meal lists using bar charts.
 */
public class SwapComparisonPresenter {
    
    /**
     * Creates a complete UI for swap comparison with demo data.
     * @return JPanel containing the swap comparison UI
     */
    public JPanel createSwapComparisonUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder("Swap Comparison Analysis"));
        
        // Create info panel at the top
        JPanel infoPanel = createInfoPanel();
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        
        // Create and display demo comparison
        JPanel chartPanel = presentDemoSwapComparison();
        mainPanel.add(chartPanel, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    /**
     * Creates an info panel explaining the demo.
     */
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Demo Comparison"));
        
        JLabel infoLabel = new JLabel("<html>" +
                "<b>Demo:</b> Real meals from database (2025-07-25 to 2025-07-26) vs 10 servings of Chow Mein<br/>" +
                "<i>This shows how nutrient intake changes when comparing real meal data to a modified portion</i>" +
                "</html>");
        
        panel.add(infoLabel);
        return panel;
    }
    
    /**
     * Creates and returns a panel with bar chart comparing nutrition before/after swap.
     * 
     * @param beforeSwapMeals The meals before the swap
     * @param afterSwapMeals The meals after the swap
     * @return JPanel containing the comparison bar chart
     */
    public JPanel presentSwapComparison(List<Meal> beforeSwapMeals, List<Meal> afterSwapMeals) {
        try {
            // Calculate nutrition totals for both meal lists
            Map<String, Double> beforeTotals = StatisticsService.instance().calculateNutrientTotalsFromMeals(beforeSwapMeals);
            Map<String, Double> afterTotals = StatisticsService.instance().calculateNutrientTotalsFromMeals(afterSwapMeals);
            
            // Create bar chart comparing the totals
            return createBarChartPanel(beforeTotals, afterTotals);
            
        } catch (Exception e) {
            return createErrorPanel("Error calculating swap comparison: " + e.getMessage());
        }
    }
    
    private JPanel createBarChartPanel(Map<String, Double> before, Map<String, Double> after) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Add all nutrients from both before and after
        Set<String> allNutrients = new HashSet<>(before.keySet());
        allNutrients.addAll(after.keySet());
        
        for (String nutrient : allNutrients) {
            double beforeVal = before.getOrDefault(nutrient, 0.0);
            double afterVal = after.getOrDefault(nutrient, 0.0);
            dataset.addValue(beforeVal, "Before Swap", nutrient);
            dataset.addValue(afterVal, "After Swap", nutrient);
        }
        
        JFreeChart barChart = ChartFactory.createBarChart(
            "Nutrient Comparison: Before vs After Swap",
            "Nutrient",
            "Amount (g)",
            dataset
        );
        
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);
        
        // Add summary label
        JLabel summaryLabel = createSummaryLabel(before, after);
        panel.add(summaryLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JLabel createSummaryLabel(Map<String, Double> before, Map<String, Double> after) {
        double beforeTotal = before.values().stream().mapToDouble(Double::doubleValue).sum();
        double afterTotal = after.values().stream().mapToDouble(Double::doubleValue).sum();
        double change = afterTotal - beforeTotal;
        
        String changeText = change >= 0 ? 
            String.format("+%.1fg (+%.1f%%)", change, (change/beforeTotal)*100) :
            String.format("%.1fg (%.1f%%)", change, (change/beforeTotal)*100);
            
        String summaryText = String.format(
            "<html><div style='text-align: center; padding: 10px;'>" +
            "<b>Total Nutrition Change:</b> %s<br/>" +
            "Before: %.1fg | After: %.1fg" +
            "</div></html>",
            changeText, beforeTotal, afterTotal
        );
        
        JLabel label = new JLabel(summaryText, JLabel.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return label;
    }
    
    private JPanel createErrorPanel(String message) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel errorLabel = new JLabel("<html><div style='text-align: center; color: red;'>" + 
                                       message + "</div></html>", JLabel.CENTER);
        panel.add(errorLabel, BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Demo method that creates sample data: 5 vs 10 * 100mL * chow mein comparison
     */
    public JPanel presentDemoSwapComparison() {
        try {
            // Create demo meal lists
            List<Meal> beforeMeals = createDemoBeforeMeals(); // 5 * 100mL * chow mein
            List<Meal> afterMeals = createDemoAfterMeals();   // 10 * 100mL * chow mein

            return presentSwapComparison(beforeMeals, afterMeals);

        } catch (Exception e) {
            return createErrorPanel("Failed to create demo swap comparison: " + e.getMessage());
        }
    }
    
    private List<Meal> createDemoBeforeMeals() throws Exception {
        // Use real data from database: 2025-07-25 to 2025-07-26
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = sdf.parse("2025-07-25");
        Date endDate = sdf.parse("2025-07-26");
        
        System.out.println("📅 [Demo Before] Fetching real meals from database: " + 
                         sdf.format(startDate) + " to " + sdf.format(endDate));
        
        // Query meals from the database
        QueryMealsService.QueryMealsServiceOutput result = 
            QueryMealsService.instance().getMealsByDate(startDate, endDate);
        
        if (!result.ok()) {
            System.err.println("❌ Failed to fetch meals: " + result.errors());
            throw new Exception("Failed to fetch meals for demo: " + result.errors());
        }
        
        List<Meal> meals = result.getMeals();
        System.out.println("✅ [Demo Before] Found " + meals.size() + " real meals from database");
        
        if (meals.isEmpty()) {
            System.out.println("⚠️  No meals found in database for the specified date range. Creating fallback demo data...");
            return createFallbackDemoMeals();
        }
        
        return meals;
    }
    
    private List<Meal> createFallbackDemoMeals() throws QueryFoodsService.QueryFoodsServiceException {
        // Fallback: Create demo data if no real meals exist
        System.out.println("🔄 Creating fallback demo meal with chow mein...");
        
        // Get chow mein from the database
        Food chowMein = QueryFoodsService.instance().findById(5); // "Chinese dish, chow mein, chicken"
        
        // Find 100mL measure or use first available
        Measure measure = chowMein.getPossibleMeasures().stream()
            .filter(m -> m.getName().toLowerCase().contains("ml") || 
                        m.getName().toLowerCase().contains("100"))
            .findFirst()
            .orElse(chowMein.getPossibleMeasures().get(0));
        
        // Create meal item: 5 * 100mL * chow mein
        MealItem chowMeinItem = new MealItem(1, chowMein, 5.0f, measure);
        
        // Create breakfast meal
        Meal breakfast = new Meal(
            1, 
            Meal.MealType.BREAKFAST, 
            List.of(chowMeinItem), 
            new Date()
        );
        
        return List.of(breakfast);
    }
    
    private List<Meal> createDemoAfterMeals() throws QueryFoodsService.QueryFoodsServiceException {
        // Get same chow mein from the database
        Food chowMein = QueryFoodsService.instance().findById(5); // "Chinese dish, chow mein, chicken"
        
        // Find same measure
        Measure measure = chowMein.getPossibleMeasures().stream()
            .filter(m -> m.getName().toLowerCase().contains("ml") || 
                        m.getName().toLowerCase().contains("100"))
            .findFirst()
            .orElse(chowMein.getPossibleMeasures().get(0));
        
        // Create meal item: 10 * 100mL * chow mein (doubled quantity)
        MealItem chowMeinItem = new MealItem(2, chowMein, 10.0f, measure);
        
        // Create breakfast meal (after "swap" - really just increased quantity)
        Meal breakfast = new Meal(
            2, 
            Meal.MealType.BREAKFAST, 
            List.of(chowMeinItem), 
            new Date()
        );
        
        return List.of(breakfast);
    }
} 