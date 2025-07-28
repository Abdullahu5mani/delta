package statistics.presenter;

import meals.services.QueryMealsService;
import meals.models.meal.Meal;
import meals.models.meal.MealItem;
import meals.models.food.Food;
import meals.models.nutrient.Nutrient;
import meals.models.food.Measure;

import statistics.service.StatisticsService;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.labels.StandardPieToolTipGenerator;
import org.jfree.data.general.PieDataset;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Presenter for creating nutrient breakdown visualizations from real meal data.
 */
public class NutrientBreakdownPresenter {

    private JPanel chartDisplayPanel;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Creates a complete UI with date selection controls and chart display area.
     * @return JPanel containing the full nutrient breakdown UI
     */
    public JPanel createNutrientBreakdownUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder("Nutrient Breakdown Analysis"));
        
        // Create date selection panel at the top
        JPanel dateSelectionPanel = createDateSelectionPanel();
        mainPanel.add(dateSelectionPanel, BorderLayout.NORTH);
        
        // Create chart display area
        chartDisplayPanel = new JPanel(new BorderLayout());
        chartDisplayPanel.setPreferredSize(new Dimension(800, 600));
        chartDisplayPanel.setBorder(BorderFactory.createTitledBorder("Chart Display"));
        
        // Show initial message
        JLabel initialLabel = new JLabel("<html><div style='text-align: center;'>" +
                "<h2>📊 Nutrient Breakdown</h2>" +
                "<p>Select a date range above and click 'Generate Chart' to view nutrient analysis.</p>" +
                "</div></html>");
        initialLabel.setHorizontalAlignment(SwingConstants.CENTER);
        chartDisplayPanel.add(initialLabel, BorderLayout.CENTER);
        
        mainPanel.add(chartDisplayPanel, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    /**
     * Creates the date selection panel with calendar controls.
     */
    private JPanel createDateSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Date Range Selection"));
        
        // Main row with date controls
        JPanel mainRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        // Start Date
        JLabel startLabel = new JLabel("From:");
        startLabel.setFont(startLabel.getFont().deriveFont(Font.BOLD, 11f));
        
        // Default to 7 days ago
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -7);
        Date defaultStartDate = cal.getTime();
        
        JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel());
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));
        startDateSpinner.setValue(defaultStartDate);
        startDateSpinner.setPreferredSize(new Dimension(110, 25));
        
        // End Date  
        JLabel endLabel = new JLabel("To:");
        endLabel.setFont(endLabel.getFont().deriveFont(Font.BOLD, 11f));
        
        Date defaultEndDate = new Date(); // Today
        
        JSpinner endDateSpinner = new JSpinner(new SpinnerDateModel());
        endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd"));
        endDateSpinner.setValue(defaultEndDate);
        endDateSpinner.setPreferredSize(new Dimension(110, 25));
        
        // Generate Button (smaller)
        JButton generateButton = new JButton("Generate");
        generateButton.setFont(generateButton.getFont().deriveFont(Font.BOLD, 11f));
        generateButton.setBackground(new Color(70, 130, 180));
        generateButton.setForeground(Color.WHITE);
        generateButton.setPreferredSize(new Dimension(90, 25));
        
        // Today quick button (bigger)
        JButton todayButton = createQuickDateButton("Today", startDateSpinner, endDateSpinner, 0);
        todayButton.setPreferredSize(new Dimension(80, 25));
        todayButton.setFont(todayButton.getFont().deriveFont(10f));
        
        // Last 7 days button
        JButton last7DaysButton = createQuickDateButton("Last 7 days", startDateSpinner, endDateSpinner, -7);
        last7DaysButton.setPreferredSize(new Dimension(90, 25));
        last7DaysButton.setFont(last7DaysButton.getFont().deriveFont(10f));
        
        // Add action listener to generate button
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Date startDate = (Date) startDateSpinner.getValue();
                Date endDate = (Date) endDateSpinner.getValue();
                
                System.out.println("🗓️ Generating chart for date range: " + 
                                 dateFormat.format(startDate) + " to " + dateFormat.format(endDate));
                
                // Generate and display the chart
                JPanel chartPanel = presentNutrientBreakdown(startDate, endDate);
                
                // Update the display area
                chartDisplayPanel.removeAll();
                chartDisplayPanel.add(chartPanel, BorderLayout.CENTER);
                chartDisplayPanel.revalidate();
                chartDisplayPanel.repaint();
            }
        });
        
        // Add components to main row
        mainRow.add(startLabel);
        mainRow.add(startDateSpinner);
        mainRow.add(endLabel);
        mainRow.add(endDateSpinner);
        mainRow.add(generateButton);
        mainRow.add(todayButton);
        mainRow.add(last7DaysButton);
        
        panel.add(mainRow, BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Creates quick date selection buttons.
     */
    private JButton createQuickDateButton(String text, JSpinner startSpinner, JSpinner endSpinner, int daysBack) {
        JButton button = new JButton(text);
        button.setFont(button.getFont().deriveFont(10f));
        button.addActionListener(e -> {
            Calendar cal = Calendar.getInstance();
            Date endDate = new Date();
            
            if (text.equals("Today")) {
                // Today only: start and end are the same
                startSpinner.setValue(endDate);
                endSpinner.setValue(endDate);
            } else if (daysBack == 0) {
                // This month
                cal.set(Calendar.DAY_OF_MONTH, 1);
                Date startDate = cal.getTime();
                startSpinner.setValue(startDate);
                endSpinner.setValue(endDate);
            } else {
                // Days back
                cal.add(Calendar.DAY_OF_MONTH, daysBack);
                Date startDate = cal.getTime();
                startSpinner.setValue(startDate);
                endSpinner.setValue(endDate);
            }
        });
        return button;
    }

    /**
     * Creates a nutrient breakdown visualization from meal data for the specified date range.
     * @param startDate The start date for meal data.
     * @param endDate The end date for meal data.
     * @return JPanel containing the pie chart and summary.
     */
    public JPanel presentNutrientBreakdown(Date startDate, Date endDate) {
        try {
            // Fetch meals using the service
            QueryMealsService.QueryMealsServiceOutput result = 
                QueryMealsService.instance().getMealsByDate(startDate, endDate);
            
            if (!result.ok()) {
                return createErrorPanel("Failed to fetch meals: " + result.errors());
            }
            
            List<Meal> meals = result.getMeals();
            
            if (meals.isEmpty()) {
                return createNoDataPanel("No meals found for the specified date range.");
            }
            
            // Calculate nutrient totals from real meal data
            Map<String, Double> nutrientTotals = StatisticsService.instance().calculateNutrientTotalsFromMeals(meals);
            
            if (nutrientTotals.isEmpty()) {
                return createNoDataPanel("No nutrient data available for the selected meals.");
            }
            
            // Calculate number of days in the selected range
            long daysDifference = (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24) + 1;
            double numberOfDays = Math.max(1.0, (double) daysDifference); // At least 1 day
            
            // Convert totals to daily averages
            Map<String, Double> dailyNutrientAverages = StatisticsService.instance().convertToDailyAverages(nutrientTotals, numberOfDays);
            
            // Calculate excluded compounds (alcohol, caffeine, theobromine, calories)
            Map<String, Double> excludedCompounds = StatisticsService.instance().calculateExcludedCompounds(meals);
            Map<String, Double> dailyExcludedAverages = StatisticsService.instance().convertToDailyAverages(excludedCompounds, numberOfDays);
            
            // Create visualization with daily averages
            return createVisualization(dailyNutrientAverages, meals.size(), dailyExcludedAverages, numberOfDays);
            
        } catch (Exception e) {
            e.printStackTrace();
            return createErrorPanel("Error generating nutrient breakdown: " + e.getMessage());
        }
    }
    
    /**
     * Creates the main visualization panel with pie chart and summary.
     */
    private JPanel createVisualization(Map<String, Double> nutrientTotals, int mealCount, Map<String, Double> excludedCompounds, double numberOfDays) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        String title = numberOfDays > 1 ? 
            String.format("Daily Average Nutrient Distribution (%.0f days)", numberOfDays) :
            "Nutrient Breakdown for Selected Meals";
        mainPanel.setBorder(BorderFactory.createTitledBorder(title));
        
        // Calculate percentages for pie chart
        double totalWeight = nutrientTotals.values().stream().mapToDouble(Double::doubleValue).sum();
        Map<String, Double> percentages = new HashMap<>();
        for (Map.Entry<String, Double> entry : nutrientTotals.entrySet()) {
            double percentage = (entry.getValue() / totalWeight) * 100.0;
            percentages.put(entry.getKey(), percentage);
        }
        
        // Create pie chart
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        for (Map.Entry<String, Double> entry : percentages.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }
        
        String chartTitle = numberOfDays > 1 ? 
            "Daily Average Nutrient Distribution" : 
            "Nutrient Distribution by Weight";
            
        JFreeChart chart = ChartFactory.createPieChart(
                chartTitle,
                dataset,
                true, true, false
        );
        
        // Customize tooltips to show more useful information
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setToolTipGenerator(new StandardPieToolTipGenerator() {
            @Override
            public String generateToolTip(PieDataset dataset, Comparable key) {
                String nutrientName = key.toString();
                double percentage = dataset.getValue(key).doubleValue();
                double weightInGrams = nutrientTotals.get(nutrientName);
                
                String unit = numberOfDays > 1 ? "g/day" : "g";
                return String.format("<html><b>%s</b><br/>%.3f%s (%.1f%%)</html>", 
                                   nutrientName, weightInGrams, unit, percentage);
            }
        });
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 500));
        
        mainPanel.add(chartPanel, BorderLayout.CENTER);
        mainPanel.add(createSummaryPanel(nutrientTotals, percentages, mealCount, excludedCompounds, numberOfDays), BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    /**
     * Creates a summary panel showing top nutrients and meal information.
     */
    private JPanel createSummaryPanel(Map<String, Double> totals, Map<String, Double> percentages, int mealCount, Map<String, Double> excludedCompounds, double numberOfDays) {
        JPanel mainSummaryPanel = new JPanel(new BorderLayout());
        String summaryTitle = numberOfDays > 1 ? 
            String.format("Daily Averages (%.0f days)", numberOfDays) : 
            "Summary";
        mainSummaryPanel.setBorder(BorderFactory.createTitledBorder(summaryTitle));
        
        // Top nutrients panel
        JPanel topNutrientsPanel = new JPanel();
        topNutrientsPanel.setLayout(new BoxLayout(topNutrientsPanel, BoxLayout.X_AXIS));
        topNutrientsPanel.setPreferredSize(new Dimension(600, 70));
        
        // Show top 4 nutrients by weight
        List<Map.Entry<String, Double>> topNutrients = totals.entrySet().stream()
                .filter(entry -> !entry.getKey().equals("Other nutrients"))
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(4)
                .collect(Collectors.toList());
        
        for (Map.Entry<String, Double> entry : topNutrients) {
            String nutrientName = entry.getKey();
            double percentage = percentages.get(nutrientName);
            double weight = entry.getValue();
            
            String unit = numberOfDays > 1 ? "g/day" : "g";
            JLabel label = new JLabel(String.format(
                    "<html><b>%s</b><br/>%.1f%% (%.3f%s)</html>", 
                    nutrientName, percentage, weight, unit
            ));
            label.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
            topNutrientsPanel.add(label);
        }
        
        topNutrientsPanel.add(Box.createHorizontalStrut(20));
        String mealInfo = numberOfDays > 1 ? 
            String.format("%.1f meals/day", (double)mealCount / numberOfDays) :
            "Total Meals: " + mealCount;
        JLabel mealCountLabel = new JLabel(mealInfo);
        mealCountLabel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        topNutrientsPanel.add(mealCountLabel);
        
        // Excluded compounds panel with proper text wrapping
        JPanel excludedPanel = new JPanel(new BorderLayout());
        excludedPanel.setPreferredSize(new Dimension(600, 50));
        
        String excludedSummary = StatisticsService.instance().getExcludedCompoundsSummary(excludedCompounds, numberOfDays);
        
        // Create a properly sized text area for wrapping
        JTextArea excludedTextArea = new JTextArea(excludedSummary);
        excludedTextArea.setWrapStyleWord(true);
        excludedTextArea.setLineWrap(true);
        excludedTextArea.setOpaque(false);
        excludedTextArea.setEditable(false);
        excludedTextArea.setFocusable(false);
        excludedTextArea.setFont(excludedTextArea.getFont().deriveFont(Font.ITALIC));
        excludedTextArea.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        excludedPanel.add(excludedTextArea, BorderLayout.CENTER);
        
        mainSummaryPanel.add(topNutrientsPanel, BorderLayout.CENTER);
        mainSummaryPanel.add(excludedPanel, BorderLayout.SOUTH);
        
        return mainSummaryPanel;
    }
    
    /**
     * Creates an error panel when something goes wrong.
     */
    private JPanel createErrorPanel(String errorMessage) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Error"));
        
        JLabel errorLabel = new JLabel("<html><div style='text-align: center;'>" +
                "<h2>❌ Error</h2>" +
                "<p>" + errorMessage + "</p>" +
                "</div></html>");
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(errorLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates a panel when no data is available.
     */
    private JPanel createNoDataPanel(String message) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("No Data"));
        
        JLabel noDataLabel = new JLabel("<html><div style='text-align: center;'>" +
                "<h2>📊 No Data Available</h2>" +
                "<p>" + message + "</p>" +
                "<p>Please select a different date range or add some meal data.</p>" +
                "</div></html>");
        noDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(noDataLabel, BorderLayout.CENTER);
        
        return panel;
    }
}
