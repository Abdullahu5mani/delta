package statistics.view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.labels.StandardPieToolTipGenerator;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NutrientBreakdownView implements INutrientBreakdownView {
    private JPanel mainPanel;
    private JPanel chartDisplayPanel;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    private GenerateChartCallback generateChartCallback;
    
    public NutrientBreakdownView() {
        this.mainPanel = new JPanel(new BorderLayout());
        this.mainPanel.setBorder(BorderFactory.createTitledBorder("Nutrient Breakdown Analysis"));
        initializeUI();
    }
    
    @Override
    public void setOnGenerateChartRequested(GenerateChartCallback callback) {
        this.generateChartCallback = callback;
    }
    
    @Override
    public JPanel getMainPanel() {
        return mainPanel;
    }
    
    @Override
    public void updateChart(JPanel newChartPanel) {
        chartDisplayPanel.removeAll();
        chartDisplayPanel.add(newChartPanel, BorderLayout.CENTER);
        chartDisplayPanel.revalidate();
        chartDisplayPanel.repaint();
    }
    
    @Override
    public void showError(String errorMessage) {
        JPanel errorPanel = createErrorPanel(errorMessage);
        updateChart(errorPanel);
    }
    
    @Override
    public void showNoData() {
        JPanel noDataPanel = createNoDataPanel("No meal data found for the selected date range.");
        updateChart(noDataPanel);
    }
    
    @Override
    public DateRange getSelectedDateRange() {
        Date startDate = (Date) startDateSpinner.getValue();
        Date endDate = (Date) endDateSpinner.getValue();
        return new DateRange(startDate, endDate);
    }
    
    private void initializeUI() {
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
    }
    
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
        
        startDateSpinner = new JSpinner(new SpinnerDateModel());
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));
        startDateSpinner.setValue(defaultStartDate);
        startDateSpinner.setPreferredSize(new Dimension(110, 25));
        
        // End Date  
        JLabel endLabel = new JLabel("To:");
        endLabel.setFont(endLabel.getFont().deriveFont(Font.BOLD, 11f));
        
        Date defaultEndDate = new Date(); // Today
        
        endDateSpinner = new JSpinner(new SpinnerDateModel());
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
                
                // Notify the presenter to handle the business logic
                if (generateChartCallback != null) {
                    generateChartCallback.onGenerateChartRequested(startDate, endDate);
                }
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
    
    private JButton createQuickDateButton(String text, JSpinner startSpinner, JSpinner endSpinner, int daysBack) {
        JButton button = new JButton(text);
        button.setFont(button.getFont().deriveFont(10f));
        
        // Store the text in a final variable to make it effectively final
        final String buttonText = text;
        
        button.addActionListener(e -> {
            Calendar cal = Calendar.getInstance();
            Date endDate = new Date();
            
            if (buttonText.equals("Today")) {
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
    
    /**
     * Creates the main visualization panel with pie chart and summary.
     */
    public JPanel createVisualization(Map<String, Double> nutrientTotals, int mealCount, Map<String, Double> excludedCompounds, double numberOfDays) {
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
    public JPanel createSummaryPanel(Map<String, Double> totals, Map<String, Double> percentages, int mealCount, Map<String, Double> excludedCompounds, double numberOfDays) {
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
        
        String excludedSummary = statistics.service.StatisticsService.instance().getExcludedCompoundsSummary(excludedCompounds, numberOfDays);
        
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
     * Handles nutrient breakdown data from the presenter and creates the UI.
     * This method is called by the presenter with calculated data.
     */
    public void handleNutrientBreakdownData(Map<String, Double> nutrientTotals, int mealCount, Map<String, Double> excludedCompounds, double numberOfDays) {
        try {
            // Create the visualization using the calculated data
            JPanel chartPanel = createVisualization(nutrientTotals, mealCount, excludedCompounds, numberOfDays);
            
            // Update the chart display
            updateChart(chartPanel);
            
        } catch (Exception e) {
            System.err.println("Error handling nutrient breakdown data: " + e.getMessage());
            showError("Error creating nutrient breakdown visualization: " + e.getMessage());
        }
    }
} 