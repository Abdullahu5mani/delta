package swaps.ui.swap_statistics;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import statistics.presenter.SwapComparisonPresenter;
import statistics.view.SwapComparisonView;
import statistics.service.StatisticsService;
import swaps.models.SwapWithMealContext;
import meals.models.meal.Meal;

/**
 * The view that renders statistics for meals before and after the swap.
 * Shows both individual food comparison AND whole meal list comparison.
 */
public class SwapStatisticsView extends JPanel {
    private SwapComparisonPresenter swapComparisonPresenter;
    private SwapComparisonView swapComparisonView;
    private JPanel contentPanel;
    private JTabbedPane tabbedPane;
    
    public SwapStatisticsView() {
        this.setLayout(new BorderLayout());
        
        // Create the view and presenter with proper dependency injection
        this.swapComparisonView = new SwapComparisonView();
        this.swapComparisonPresenter = new SwapComparisonPresenter(swapComparisonView, StatisticsService.instance());
        this.swapComparisonPresenter.initialize();
        
        // Create tabbed pane for different comparison views
        this.tabbedPane = new JTabbedPane();
        
        // Create content panel for the comparison charts
        this.contentPanel = new JPanel(new BorderLayout());
        this.contentPanel.add(tabbedPane, BorderLayout.CENTER);
        this.add(contentPanel, BorderLayout.CENTER);
        
        // Initial placeholder
        showPlaceholder();
    }
    
    /**
     * Updates the view to show comparison statistics for the selected swap.
     * @param selectedSwap The swap to analyze (oldFood vs newFood)
     */
    public void updateSwapComparison(SwapWithMealContext selectedSwap) {
        if (selectedSwap == null) {
            showPlaceholder();
            return;
        }
        
        try {
            // Clear existing tabs
            tabbedPane.removeAll();
            
            // Tab 1: Individual Food Comparison (current functionality)
            JPanel foodComparisonPanel = swapComparisonView.createSwapComparisonFromFoods(
                selectedSwap.oldFood(), 
                selectedSwap.newFood(),
                "Before Swap: " + selectedSwap.oldFood().getFoodDescription(),
                "After Swap: " + selectedSwap.newFood().getFoodDescription()
            );
            tabbedPane.addTab("🍽️ Food Comparison", foodComparisonPanel);
            
            // Tab 2: Whole Meal List Comparison (NEW FUNCTIONALITY)
            // Note: We'll need the beforeSwapMeals and afterSwapMeals from the caller
            // For now, show a placeholder until we get the meal lists
            JPanel mealListPlaceholder = createMealListPlaceholder();
            tabbedPane.addTab("📊 Meal List Impact", mealListPlaceholder);
            
            // Tab 3: Goal Nutrient Daily Trends (NEW FUNCTIONALITY)
            JPanel lineChartPlaceholder = createLineChartPlaceholder();
            tabbedPane.addTab("📈 Goal Nutrient Trends", lineChartPlaceholder);
            
            // Make tabs visible
            contentPanel.removeAll();
            contentPanel.add(tabbedPane, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
            
        } catch (Exception e) {
            showError("Error generating swap comparison: " + e.getMessage());
        }
    }
    
    /**
     * NEW: Updates the meal list comparison tab with before and after meals.
     * @param beforeSwapMeals The original meals before any swaps
     * @param afterSwapMeals The meals after applying the swap
     */
    public void updateMealListComparison(List<Meal> beforeSwapMeals, List<Meal> afterSwapMeals) {
        if (beforeSwapMeals == null || afterSwapMeals == null) {
            return;
        }
        
        try {
            // Calculate nutrient totals for both meal lists
            Map<String, Double> beforeNutrients = StatisticsService.instance().calculateNutrientTotalsFromMeals(beforeSwapMeals);
            Map<String, Double> afterNutrients = StatisticsService.instance().calculateNutrientTotalsFromMeals(afterSwapMeals);
            
            // Create the meal list comparison using the view
            JPanel mealComparisonPanel = swapComparisonView.createBarChartPanel(beforeNutrients, afterNutrients);
            
            // Update the second tab (index 1) if it exists
            if (tabbedPane.getTabCount() > 1) {
                tabbedPane.setComponentAt(1, mealComparisonPanel);
                tabbedPane.setTitleAt(1, "📊 Meal List Impact (" + beforeSwapMeals.size() + " vs " + afterSwapMeals.size() + " meals)");
            }
            
        } catch (Exception e) {
            System.err.println("Error updating meal list comparison: " + e.getMessage());
        }
    }
    
    /**
     * NEW: Updates the meal list comparison tab with a pre-built panel.
     * This version is used when goal nutrients are prioritized.
     * @param comparisonPanel Pre-built comparison panel with goal nutrients prioritized
     */
    public void updateMealListComparisonWithPanel(JPanel comparisonPanel) {
        if (comparisonPanel == null) {
            return;
        }
        
        try {
            // Update the second tab (index 1) if it exists
            if (tabbedPane.getTabCount() > 1) {
                tabbedPane.setComponentAt(1, comparisonPanel);
                tabbedPane.setTitleAt(1, "📊 Meal List Impact (Goal Nutrients Prioritized)");
            }
            
        } catch (Exception e) {
            System.err.println("Error updating meal list comparison with panel: " + e.getMessage());
        }
    }
    
    /**
     * NEW: Updates the meal list comparison with goal nutrients prioritized.
     * This is the proper MVP approach - view creates UI, presenter provides data.
     * @param beforeSwapMeals The original meals before any swaps
     * @param afterSwapMeals The meals after applying the swap
     * @param goalNutrientNames List of nutrient names from user's goals
     * @param selectedSwap The swap context to apply for the line chart
     */
    public void updateMealListComparisonWithGoals(List<Meal> beforeSwapMeals, List<Meal> afterSwapMeals, List<String> goalNutrientNames, swaps.models.SwapWithMealContext selectedSwap) {
        if (beforeSwapMeals == null || afterSwapMeals == null) {
            return;
        }
        
        try {
            // Calculate nutrient totals for both meal lists
            Map<String, Double> beforeNutrients = StatisticsService.instance().calculateNutrientTotalsFromMeals(beforeSwapMeals);
            Map<String, Double> afterNutrients = StatisticsService.instance().calculateNutrientTotalsFromMeals(afterSwapMeals);
            
            // Create the goal-prioritized meal list comparison using the view
            JPanel mealComparisonPanel = swapComparisonView.createBarChartPanelWithGoals(beforeNutrients, afterNutrients, goalNutrientNames);
            
            // Update the second tab (index 1) if it exists
            if (tabbedPane.getTabCount() > 1) {
                tabbedPane.setComponentAt(1, mealComparisonPanel);
                tabbedPane.setTitleAt(1, "📊 Meal List Impact (Goal Nutrients Prioritized)");
            }
            
            // Update the third tab (index 2) with the goal nutrient trends
            if (tabbedPane.getTabCount() > 2) {
                JPanel goalTrendsPanel = createGoalNutrientTrendsPanel(beforeNutrients, afterNutrients, goalNutrientNames, beforeSwapMeals, selectedSwap);
                tabbedPane.setComponentAt(2, goalTrendsPanel);
                tabbedPane.setTitleAt(2, "📈 Goal Nutrient Trends");
            }
            
        } catch (Exception e) {
            System.err.println("Error updating meal list comparison with goals: " + e.getMessage());
        }
    }
    
    /**
     * Creates the goal nutrient trends panel with actual meal data for dates and swap context.
     */
    private JPanel createGoalNutrientTrendsPanel(Map<String, Double> beforeNutrients, Map<String, Double> afterNutrients, List<String> goalNutrientNames, List<Meal> meals, swaps.models.SwapWithMealContext swapContext) {
        try {
            // Show the line chart with proper time-based visualization using actual meal dates and swap context
            return swapComparisonView.createLineChartPanel(beforeNutrients, afterNutrients, goalNutrientNames, meals, swapContext);
            
        } catch (Exception e) {
            // Fallback to error panel
            JPanel errorPanel = new JPanel(new BorderLayout());
            JLabel errorLabel = new JLabel("Error creating goal nutrient trends: " + e.getMessage());
            errorLabel.setHorizontalAlignment(JLabel.CENTER);
            errorPanel.add(errorLabel, BorderLayout.CENTER);
            return errorPanel;
        }
    }
    
    /**
     * Creates a placeholder for the meal list comparison tab.
     */
    private JPanel createMealListPlaceholder() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(
            "<html><center>" +
            "<h3>Whole Meal List Comparison</h3>" +
            "<p>This will show the nutritional impact of your swap across all selected meals.</p>" +
            "<p><i>Waiting for meal data...</i></p>" +
            "</center></html>", 
            JLabel.CENTER
        );
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 12f));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Creates a placeholder for the line chart tab.
     */
    private JPanel createLineChartPlaceholder() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(
            "<html><center>" +
            "<h3>Goal Nutrient Daily Trends</h3>" +
            "<p>This will show how your swap affects your chosen goal nutrients over time.</p>" +
            "<p><i>Waiting for goal data...</i></p>" +
            "</center></html>", 
            JLabel.CENTER
        );
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 12f));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Shows a placeholder message when no swap is selected.
     */
    private void showPlaceholder() {
        contentPanel.removeAll();
        JLabel placeholderLabel = new JLabel("Select a swap to see nutritional comparison", JLabel.CENTER);
        placeholderLabel.setFont(placeholderLabel.getFont().deriveFont(Font.ITALIC, 14f));
        contentPanel.add(placeholderLabel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Shows an error message.
     */
    private void showError(String message) {
        contentPanel.removeAll();
        JLabel errorLabel = new JLabel(message, JLabel.CENTER);
        errorLabel.setForeground(Color.RED);
        contentPanel.add(errorLabel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
