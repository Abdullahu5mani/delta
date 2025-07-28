package statistics.view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import meals.models.food.Food;
import meals.models.meal.Meal;
import meals.services.QueryFoodsService;

public class SwapComparisonView implements ISwapComparisonView {
    private JPanel mainPanel;
    private JTabbedPane tabbedPane;
    
    private SwapComparisonCallback swapComparisonCallback;
    private MealListComparisonCallback mealListComparisonCallback;
    
    public SwapComparisonView() {
        this.mainPanel = new JPanel(new BorderLayout());
        this.mainPanel.setBorder(BorderFactory.createTitledBorder("Food Swap Comparison"));
        initializeUI();
    }
    
    @Override
    public void setOnUpdateSwapComparison(SwapComparisonCallback callback) {
        this.swapComparisonCallback = callback;
    }
    
    @Override
    public void setOnUpdateMealListComparison(MealListComparisonCallback callback) {
        this.mealListComparisonCallback = callback;
    }
    
    @Override
    public JPanel getMainPanel() {
        return mainPanel;
    }
    
    @Override
    public void updateSwapComparison(JPanel newChartPanel) {
        tabbedPane.setComponentAt(0, newChartPanel);
    }
    
    @Override
    public void updateMealListComparison(JPanel newChartPanel) {
        tabbedPane.setComponentAt(1, newChartPanel);
    }
    
    @Override
    public void updateGoalNutrientTrends(JPanel newChartPanel) {
        tabbedPane.setComponentAt(2, newChartPanel);
    }
    
    @Override
    public void showError(String errorMessage) {
        JPanel errorPanel = createErrorPanel(errorMessage);
        updateSwapComparison(errorPanel);
    }
    
    private void initializeUI() {
        tabbedPane = new JTabbedPane();
        
        // Create placeholder tabs
        tabbedPane.addTab("Food Comparison", createFoodComparisonPlaceholder());
        tabbedPane.addTab("Meal List Impact", createMealListPlaceholder());
        tabbedPane.addTab("Goal Nutrient Trends", createLineChartPlaceholder());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createFoodComparisonPlaceholder() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel label = new JLabel("🍽️ Food comparison will appear here when a swap is selected", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(Color.GRAY);
        
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createMealListPlaceholder() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel label = new JLabel("📊 Meal list impact comparison will appear here", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(Color.GRAY);
        
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createLineChartPlaceholder() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel label = new JLabel("📈 Goal nutrient trends will appear here", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(Color.GRAY);
        
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createErrorPanel(String message) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Error"));
        
        JLabel errorLabel = new JLabel("<html><div style='text-align: center;'>" +
                "<h2>❌ Error</h2>" +
                "<p>" + message + "</p>" +
                "</div></html>");
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(errorLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates a bar chart panel for comparing two nutrient maps.
     */
    public JPanel createBarChartPanel(Map<String, Double> before, Map<String, Double> after) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Add all nutrients from both before and after
        Set<String> allNutrients = new java.util.HashSet<>(before.keySet());
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
        
        return panel;
    }
    
    /**
     * Creates a bar chart panel with goal nutrients prioritized.
     */
    public JPanel createBarChartPanelWithGoals(Map<String, Double> before, Map<String, Double> after, List<String> goalNutrientNames) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Get prioritized nutrients (goals first, then top by amount)
        Map<String, Double> prioritizedBefore = getPrioritizedNutrientsForGoals(before, goalNutrientNames, 7);
        Map<String, Double> prioritizedAfter = getPrioritizedNutrientsForGoals(after, goalNutrientNames, 7);
        
        // Add all nutrients from both prioritized maps
        Set<String> allNutrients = new java.util.HashSet<>(prioritizedBefore.keySet());
        allNutrients.addAll(prioritizedAfter.keySet());
        
        // Add data to dataset in the order they appear in prioritizedBefore (goals first)
        for (String nutrient : prioritizedBefore.keySet()) {
            double beforeVal = prioritizedBefore.getOrDefault(nutrient, 0.0);
            double afterVal = prioritizedAfter.getOrDefault(nutrient, 0.0);
            dataset.addValue(beforeVal, "Before Swap", nutrient);
            dataset.addValue(afterVal, "After Swap", nutrient);
        }
        
        // Add any nutrients that are only in prioritizedAfter
        for (String nutrient : prioritizedAfter.keySet()) {
            if (!prioritizedBefore.containsKey(nutrient)) {
                double beforeVal = 0.0;
                double afterVal = prioritizedAfter.get(nutrient);
                dataset.addValue(beforeVal, "Before Swap", nutrient);
                dataset.addValue(afterVal, "After Swap", nutrient);
            }
        }
        
        JFreeChart barChart = ChartFactory.createBarChart(
            "Meal List Impact: Before vs After Swap (Goal Nutrients Prioritized)",
            "Nutrient",
            "Amount (g)",
            dataset
        );
        
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);
        
        // Add summary label with goal highlighting
        JLabel summaryLabel = createGoalAwareSummaryLabel(before, after, goalNutrientNames);
        panel.add(summaryLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Creates a summary label that highlights the impact on goal nutrients.
     */
    private JLabel createGoalAwareSummaryLabel(Map<String, Double> before, Map<String, Double> after, List<String> goalNutrientNames) {
        double beforeTotal = before.values().stream().mapToDouble(Double::doubleValue).sum();
        double afterTotal = after.values().stream().mapToDouble(Double::doubleValue).sum();
        double change = afterTotal - beforeTotal;
        
        StringBuilder html = new StringBuilder("<html><div style='text-align: center;'>");
        
        // Add goal nutrient specific changes
        if (!goalNutrientNames.isEmpty()) {
            html.append("<br/><b>Goal Nutrient Changes:</b><br/>");
            for (String goalNutrient : goalNutrientNames) {
                double beforeVal = before.getOrDefault(goalNutrient, 0.0);
                double afterVal = after.getOrDefault(goalNutrient, 0.0);
                double nutrientChange = afterVal - beforeVal;
                String changeSymbol = nutrientChange > 0 ? "+" : "";
                html.append(String.format("• %s: %s%.2fg<br/>", goalNutrient, changeSymbol, nutrientChange));
            }
        }
        
        html.append(String.format("<br/><b>Total Change:</b> %s%.2fg", change > 0 ? "+" : "", change));
        html.append("</div></html>");
        
        JLabel label = new JLabel(html.toString());
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        return label;
    }
    

    
    /**
     * Creates a food comparison summary panel.
     */
    public JPanel createFoodComparisonSummary(Food beforeFood, Food afterFood, 
                                            Map<String, Double> beforeNutrients, 
                                            Map<String, Double> afterNutrients) {
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary"));
        
        // Create summary text
        StringBuilder summary = new StringBuilder();
        summary.append(String.format("🍽️ <b>%s</b> vs <b>%s</b><br/><br/>", 
                                   beforeFood.getFoodDescription(), afterFood.getFoodDescription()));
        
        // Show top 3 nutrients by difference
        List<Map.Entry<String, Double>> topDifferences = new java.util.ArrayList<>();
        for (String nutrient : beforeNutrients.keySet()) {
            if (afterNutrients.containsKey(nutrient)) {
                double difference = afterNutrients.get(nutrient) - beforeNutrients.get(nutrient);
                topDifferences.add(new java.util.AbstractMap.SimpleEntry<>(nutrient, difference));
            }
        }
        
        topDifferences.sort((e1, e2) -> Double.compare(Math.abs(e2.getValue()), Math.abs(e1.getValue())));
        
        summary.append("<b>Top Changes:</b><br/>");
        for (int i = 0; i < Math.min(3, topDifferences.size()); i++) {
            Map.Entry<String, Double> entry = topDifferences.get(i);
            String change = entry.getValue() > 0 ? "+" : "";
            summary.append(String.format("• %s: %s%.2fg<br/>", entry.getKey(), change, entry.getValue()));
        }
        
        JLabel summaryLabel = new JLabel("<html>" + summary.toString() + "</html>");
        summaryLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        summaryPanel.add(summaryLabel, BorderLayout.CENTER);
        return summaryPanel;
    }
    
    /**
     * Gets prioritized nutrients for goals (goals first, then top by amount).
     */
    private Map<String, Double> getPrioritizedNutrientsForGoals(
            Map<String, Double> allNutrients, 
            List<String> goalNutrientNames, 
            int totalCount) {
        
        Map<String, Double> result = new java.util.LinkedHashMap<>(); // Preserve insertion order
        double otherSum = 0.0;
        Set<String> processedNutrients = new java.util.HashSet<>();
        
        System.out.println("🎯 Prioritizing goal nutrients: " + goalNutrientNames);
        
        // Step 1: Add goal nutrients first (if they exist in the data)
        for (String goalNutrient : goalNutrientNames) {
            if (allNutrients.containsKey(goalNutrient)) {
                result.put(goalNutrient, allNutrients.get(goalNutrient));
                processedNutrients.add(goalNutrient);
                System.out.println("🏆 Goal nutrient: " + goalNutrient + " = " + 
                                 String.format("%.3f", allNutrients.get(goalNutrient)) + "g");
            }
        }
        
        // Step 2: Fill remaining slots with top nutrients by amount (excluding goal nutrients)
        int remainingSlots = totalCount - result.size();
        List<Map.Entry<String, Double>> sortedEntries = allNutrients.entrySet().stream()
                .filter(entry -> !processedNutrients.contains(entry.getKey())) // Exclude already processed goal nutrients
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue())) // Sort by amount descending
                .collect(Collectors.toList());
        
        // Add top nutrients to fill remaining slots
        for (int i = 0; i < sortedEntries.size(); i++) {
            Map.Entry<String, Double> entry = sortedEntries.get(i);
            
            if (i < remainingSlots) {
                result.put(entry.getKey(), entry.getValue());
                processedNutrients.add(entry.getKey());
                System.out.println("📊 Top nutrient: " + entry.getKey() + " = " + 
                                 String.format("%.3f", entry.getValue()) + "g");
            } else {
                otherSum += entry.getValue();
            }
        }
        
        // Step 3: Add "Other nutrients" if there are any remaining
        if (otherSum > 0) {
            result.put("Other nutrients", otherSum);
            System.out.println("📦 Other nutrients: " + String.format("%.3f", otherSum) + "g");
        }
        
        System.out.println("✅ Final chart will show " + result.size() + " categories");
        return result;
    }
    
    /**
     * Creates swap comparison from two Food objects.
     */
    public JPanel createSwapComparisonFromFoods(Food beforeFood, Food afterFood, String beforeLabel, String afterLabel) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder("Nutritional Comparison"));
        
        try {
            // Calculate nutrient totals for each food (using standard serving size)
            Map<String, Double> beforeNutrients = calculateNutrientTotalsFromFood(beforeFood);
            Map<String, Double> afterNutrients = calculateNutrientTotalsFromFood(afterFood);
            
            // Get top 7 nutrients for better visualization
            Map<String, Double> prioritizedBefore = getPrioritizedNutrientsForGoals(beforeNutrients, new java.util.ArrayList<>(), 7);
            Map<String, Double> prioritizedAfter = getPrioritizedNutrientsForGoals(afterNutrients, new java.util.ArrayList<>(), 7);
            
            // Create the comparison chart with top 7 nutrients
            JPanel chartPanel = createBarChartPanel(prioritizedBefore, prioritizedAfter);
            mainPanel.add(chartPanel, BorderLayout.CENTER);
            
            // Add summary information
            JPanel summaryPanel = createFoodComparisonSummary(beforeFood, afterFood, beforeNutrients, afterNutrients);
            mainPanel.add(summaryPanel, BorderLayout.SOUTH);
            
        } catch (Exception e) {
            JLabel errorLabel = new JLabel("Error creating food comparison: " + e.getMessage());
            errorLabel.setHorizontalAlignment(JLabel.CENTER);
            mainPanel.add(errorLabel, BorderLayout.CENTER);
        }
        
        return mainPanel;
    }
    
    /**
     * Calculates nutrient totals from a Food object.
     */
    private Map<String, Double> calculateNutrientTotalsFromFood(Food food) {
        Map<String, Double> nutrientTotals = new java.util.HashMap<>();
        
        if (food == null || food.getNutrientAmounts() == null) {
            return nutrientTotals;
        }
        
        // Sum up all nutrients for this food, excluding bioactive compounds and moisture/ash
        for (Map.Entry<meals.models.nutrient.Nutrient, Float> entry : food.getNutrientAmounts().entrySet()) {
            String nutrientName = entry.getKey().getNutrientName();
            String nutrientUnit = entry.getKey().getNutrientUnit();
            Float amount = entry.getValue();
            
            if (amount != null && amount > 0) {
                // Skip water and bulk nutrients that would skew visualization
                if (statistics.service.StatisticsService.instance().isWaterOrBulk(nutrientName)) {
                    continue;
                }
                
                // Skip bioactive compounds (alcohol, caffeine, theobromine, calories)
                if (statistics.service.StatisticsService.instance().isBioactiveCompound(nutrientName)) {
                    continue;
                }
                
                // Convert to grams for consistent comparison
                double amountInGrams = statistics.service.StatisticsService.instance().convertToGrams(amount, nutrientUnit);
                nutrientTotals.put(nutrientName, amountInGrams);
            }
        }
        
        return nutrientTotals;
    }
    
    /**
     * Handles meal list comparison data from the presenter and creates the UI.
     * This method is called by the presenter with calculated data.
     */
    public void handleMealListComparisonWithData(Map<String, Double> beforeNutrients, Map<String, Double> afterNutrients, List<String> goalNutrientNames) {
        try {
            // Create the goal-prioritized meal list comparison
            JPanel mealComparisonPanel = createBarChartPanelWithGoals(beforeNutrients, afterNutrients, goalNutrientNames);
            
            // Update the meal list comparison tab
            updateMealListComparison(mealComparisonPanel);
            
        } catch (Exception e) {
            System.err.println("Error handling meal list comparison data: " + e.getMessage());
            showError("Error creating meal list comparison: " + e.getMessage());
        }
    }
    
    /**
     * Creates a line chart panel for goal nutrient trends over time.
     * This shows daily trends with actual dates on X-axis and nutrient amounts on Y-axis.
     * Each goal nutrient gets separate lines for before/after swap.
     */
    public JPanel createLineChartPanel(Map<String, Double> beforeNutrients, Map<String, Double> afterNutrients, List<String> goalNutrientNames, List<meals.models.meal.Meal> meals, swaps.models.SwapWithMealContext swapContext) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder("Goal Nutrient Daily Trends"));
        
        try {
            // Create line chart dataset with actual dates on X-axis
            DefaultCategoryDataset dataset = createDailyTrendsDataset(beforeNutrients, afterNutrients, goalNutrientNames, meals, swapContext);
            
            // Create line chart
            JFreeChart lineChart = ChartFactory.createLineChart(
                "Goal Nutrient Trends Over Time",
                "Date",
                "Amount (g)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
            );
            
            // Customize the plot
            CategoryPlot plot = (CategoryPlot) lineChart.getPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setRangeGridlinePaint(Color.GRAY);
            plot.setDomainGridlinePaint(Color.GRAY);
            
            // Use line and shape renderer for better visualization
            LineAndShapeRenderer renderer = new LineAndShapeRenderer();
            
            // Set different colors for each nutrient line
            Color[] colors = {
                new Color(255, 99, 132),   // Red
                new Color(54, 162, 235),   // Blue
                new Color(255, 205, 86),   // Yellow
                new Color(75, 192, 192),   // Teal
                new Color(153, 102, 255),  // Purple
                new Color(255, 159, 64),   // Orange
                new Color(201, 203, 207)   // Gray
            };
            
            // Apply colors to series
            for (int i = 0; i < dataset.getRowCount(); i++) {
                renderer.setSeriesPaint(i, colors[i % colors.length]);
            }
            
            plot.setRenderer(renderer);
            
            ChartPanel chartPanel = new ChartPanel(lineChart);
            chartPanel.setPreferredSize(new Dimension(800, 600));
            
            mainPanel.add(chartPanel, BorderLayout.CENTER);
            
            // Add summary
            JLabel summaryLabel = createGoalNutrientLineSummary(beforeNutrients, afterNutrients, goalNutrientNames);
            mainPanel.add(summaryLabel, BorderLayout.SOUTH);
            
        } catch (Exception e) {
            JLabel errorLabel = new JLabel("Error creating line chart: " + e.getMessage());
            errorLabel.setHorizontalAlignment(JLabel.CENTER);
            mainPanel.add(errorLabel, BorderLayout.CENTER);
        }
        
        return mainPanel;
    }
    
    /**
     * Fallback method for creating line chart without meal data (for backward compatibility).
     */
    public JPanel createLineChartPanel(Map<String, Double> beforeNutrients, Map<String, Double> afterNutrients, List<String> goalNutrientNames) {
        return createLineChartPanel(beforeNutrients, afterNutrients, goalNutrientNames, new java.util.ArrayList<>(), null);
    }
    
    /**
     * Fallback method for creating line chart with meal data but no swap context.
     */
    public JPanel createLineChartPanel(Map<String, Double> beforeNutrients, Map<String, Double> afterNutrients, List<String> goalNutrientNames, List<meals.models.meal.Meal> meals) {
        return createLineChartPanel(beforeNutrients, afterNutrients, goalNutrientNames, meals, null);
    }
    
    /**
     * Creates a dataset for daily trends with actual dates on X-axis and nutrient amounts on Y-axis.
     * Each goal nutrient gets separate lines for before/after swap.
     */
    private DefaultCategoryDataset createDailyTrendsDataset(Map<String, Double> beforeNutrients, Map<String, Double> afterNutrients, List<String> goalNutrientNames, List<meals.models.meal.Meal> meals, swaps.models.SwapWithMealContext swapContext) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        if (meals.isEmpty()) {
            // Fallback: show before/after as two time points
            for (String goalNutrient : goalNutrientNames) {
                double beforeVal = beforeNutrients.getOrDefault(goalNutrient, 0.0);
                double afterVal = afterNutrients.getOrDefault(goalNutrient, 0.0);
                
                String beforeSeriesName = goalNutrient + " (Before)";
                String afterSeriesName = goalNutrient + " (After)";
                
                dataset.addValue(beforeVal, beforeSeriesName, "Before Swap");
                dataset.addValue(afterVal, afterSeriesName, "After Swap");
            }
            return dataset;
        }
        
        // Extract unique dates from meals and sort them
        java.util.Set<java.util.Date> uniqueDates = new java.util.TreeSet<>();
        for (meals.models.meal.Meal meal : meals) {
            uniqueDates.add(truncateToDay(meal.getCreatedAt()));
        }
        
        java.util.List<java.util.Date> sortedDates = new java.util.ArrayList<>(uniqueDates);
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
        
        // Calculate daily nutrient totals for each date
        // Before: original meals without swap
        java.util.Map<java.util.Date, java.util.Map<String, Double>> dailyBeforeNutrients = calculateDailyNutrientTotals(meals, null);
        // After: meals with swap applied
        java.util.Map<java.util.Date, java.util.Map<String, Double>> dailyAfterNutrients = calculateDailyNutrientTotals(meals, swapContext);
        
        // For each goal nutrient, create separate lines for before and after
        for (String goalNutrient : goalNutrientNames) {
            String beforeSeriesName = goalNutrient + " (Before)";
            String afterSeriesName = goalNutrient + " (After)";
            
            // Add data points for each date
            for (java.util.Date date : sortedDates) {
                String dateStr = dateFormat.format(date);
                
                // Get daily totals for this specific date
                double beforeVal = dailyBeforeNutrients.getOrDefault(date, new java.util.HashMap<>()).getOrDefault(goalNutrient, 0.0);
                double afterVal = dailyAfterNutrients.getOrDefault(date, new java.util.HashMap<>()).getOrDefault(goalNutrient, 0.0);
                
                dataset.addValue(beforeVal, beforeSeriesName, dateStr);
                dataset.addValue(afterVal, afterSeriesName, dateStr);
            }
        }
        
        return dataset;
    }
    
    /**
     * Calculates daily nutrient totals for each date in the meal list.
     * @param meals List of meals to analyze
     * @param swapContext The swap context to apply (null for no swap)
     * @return Map of date -> nutrient totals for that date
     */
    private java.util.Map<java.util.Date, java.util.Map<String, Double>> calculateDailyNutrientTotals(List<meals.models.meal.Meal> meals, swaps.models.SwapWithMealContext swapContext) {
        java.util.Map<java.util.Date, java.util.Map<String, Double>> dailyTotals = new java.util.HashMap<>();
        
        for (meals.models.meal.Meal meal : meals) {
            java.util.Date mealDate = truncateToDay(meal.getCreatedAt());
            
            // Get or create the daily totals map for this date
            java.util.Map<String, Double> dateTotals = dailyTotals.computeIfAbsent(mealDate, k -> new java.util.HashMap<>());
            
            // Calculate nutrient totals for this meal
            for (meals.models.meal.MealItem item : meal.getMealItems()) {
                meals.models.food.Food food = item.getFood();
                Float quantity = item.getQuantity();
                meals.models.food.Measure measure = item.getSelectedMeasure();
                
                if (food != null && food.getNutrientAmounts() != null && quantity != null && measure != null) {
                    
                    // Apply swap simulation if swap context is provided
                    if (swapContext != null && food.getFoodId().equals(swapContext.oldFood().getFoodId())) {
                        food = swapContext.newFood(); // Replace with new food
                    }
                    
                    Float conversionFactor = measure.getConversionValue();
                    
                    // Add nutrient amounts to daily totals with proper scaling
                    for (java.util.Map.Entry<meals.models.nutrient.Nutrient, Float> entry : food.getNutrientAmounts().entrySet()) {
                        String nutrientName = entry.getKey().getNutrientName();
                        String nutrientUnit = entry.getKey().getNutrientUnit();
                        Float amount = entry.getValue();
                        
                        if (amount != null && amount > 0) {
                            // Skip water and bulk nutrients
                            if (statistics.service.StatisticsService.instance().isWaterOrBulk(nutrientName)) {
                                continue;
                            }
                            
                            // Skip bioactive compounds
                            if (statistics.service.StatisticsService.instance().isBioactiveCompound(nutrientName)) {
                                continue;
                            }
                            
                            // Correct scaling: base_amount × conversion_factor × quantity
                            double scaledAmount = amount * conversionFactor * quantity;
                            
                            // Convert to grams for consistent comparison
                            double amountInGrams = statistics.service.StatisticsService.instance().convertToGrams(scaledAmount, nutrientUnit);
                            
                            // Add to daily total
                            dateTotals.merge(nutrientName, amountInGrams, Double::sum);
                        }
                    }
                }
            }
        }
        
        return dailyTotals;
    }
    
    /**
     * Truncates a date to the start of the day (removes time component).
     */
    private java.util.Date truncateToDay(java.util.Date date) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(date);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    /**
     * Creates a summary label for goal nutrient line chart.
     */
    private JLabel createGoalNutrientLineSummary(Map<String, Double> beforeNutrients, Map<String, Double> afterNutrients, List<String> goalNutrientNames) {
        StringBuilder html = new StringBuilder("<html><div style='text-align: center;'>");
        html.append("<b>Goal Nutrient Changes:</b><br/>");
        
        for (String goalNutrient : goalNutrientNames) {
            double beforeVal = beforeNutrients.getOrDefault(goalNutrient, 0.0);
            double afterVal = afterNutrients.getOrDefault(goalNutrient, 0.0);
            double change = afterVal - beforeVal;
            String changeSymbol = change > 0 ? "+" : "";
            html.append(String.format("• %s: %s%.2fg<br/>", goalNutrient, changeSymbol, change));
        }
        
        html.append("</div></html>");
        
        JLabel label = new JLabel(html.toString());
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        return label;
    }
} 