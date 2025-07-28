package statistics.presenter;

import meals.models.meal.Meal;
import meals.services.QueryMealsService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import statistics.service.StatisticsService;
import statistics.service.StatisticsService.CanadaFoodGuideCategory;
import statistics.service.StatisticsService.CFGAnalysisResult;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Presenter for Canada Food Guide alignment analysis.
 */
public class FoodGuideAlignmentPresenter {
    
    /**
     * Creates the main UI panel for Canada Food Guide analysis.
     */
    public JPanel createFoodGuideAlignmentUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder("Canada Food Guide Analysis"));
        
        // Create date selection panel
        JPanel dateSelectionPanel = createDateSelectionPanel();
        mainPanel.add(dateSelectionPanel, BorderLayout.NORTH);
        
        // Create chart display area
        JPanel chartDisplayPanel = new JPanel(new BorderLayout());
        chartDisplayPanel.setPreferredSize(new Dimension(800, 600));
        
        // Show initial message
        JLabel initialLabel = new JLabel("<html><div style='text-align: center;'>" +
            "<h2>Canada Food Guide Analysis</h2>" +
            "<p>Select a date range to analyze how well your meals align with Canada Food Guide recommendations.</p>" +
            "<p><b>Target:</b> 50% Vegetables & Fruits, 25% Whole Grains, 25% Protein Foods</p>" +
            "</div></html>");
        initialLabel.setHorizontalAlignment(SwingConstants.CENTER);
        chartDisplayPanel.add(initialLabel, BorderLayout.CENTER);
        
        mainPanel.add(chartDisplayPanel, BorderLayout.CENTER);
        
        return mainPanel;
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
        
        // Generate Button
        JButton generateButton = new JButton("Analyze");
        generateButton.setFont(generateButton.getFont().deriveFont(Font.BOLD, 11f));
        generateButton.setBackground(new Color(76, 175, 80)); // Green
        generateButton.setForeground(Color.WHITE);
        generateButton.setPreferredSize(new Dimension(100, 30));
        generateButton.setMinimumSize(new Dimension(100, 30));
        
        // Today quick button
        JButton todayButton = createQuickDateButton("Today", startDateSpinner, endDateSpinner, 0);
        JButton weekButton = createQuickDateButton("Last 7 Days", startDateSpinner, endDateSpinner, -7);
        JButton monthButton = createQuickDateButton("Last 30 Days", startDateSpinner, endDateSpinner, -30);
        
        // Add generate button action
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Date startDate = (Date) startDateSpinner.getValue();
                Date endDate = (Date) endDateSpinner.getValue();
                
                if (startDate.after(endDate)) {
                    JOptionPane.showMessageDialog(panel, 
                        "Start date must be before or equal to end date.", 
                        "Invalid Date Range", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Find the parent container and update the chart
                Container parent = panel.getParent();
                if (parent instanceof JPanel) {
                    JPanel mainPanel = (JPanel) parent;
                    Component[] components = mainPanel.getComponents();
                    for (Component comp : components) {
                        if (comp != panel) { // Find the chart panel
                            mainPanel.remove(comp);
                            JPanel newChart = presentFoodGuideAlignment(startDate, endDate);
                            mainPanel.add(newChart, BorderLayout.CENTER);
                            mainPanel.revalidate();
                            mainPanel.repaint();
                            break;
                        }
                    }
                }
            }
        });
        
        // Layout components
        mainRow.add(startLabel);
        mainRow.add(startDateSpinner);
        mainRow.add(Box.createRigidArea(new Dimension(15, 0)));
        mainRow.add(endLabel);
        mainRow.add(endDateSpinner);
        mainRow.add(Box.createRigidArea(new Dimension(20, 0)));
        mainRow.add(generateButton);
        
        // Quick date buttons row
        JPanel quickButtonsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        quickButtonsRow.add(new JLabel("Quick select:"));
        quickButtonsRow.add(Box.createRigidArea(new Dimension(5, 0)));
        quickButtonsRow.add(todayButton);
        quickButtonsRow.add(weekButton);
        quickButtonsRow.add(monthButton);
        
        panel.add(mainRow, BorderLayout.NORTH);
        panel.add(quickButtonsRow, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JButton createQuickDateButton(String text, JSpinner startSpinner, JSpinner endSpinner, int daysFromToday) {
        JButton button = new JButton(text);
        button.setFont(button.getFont().deriveFont(10f));
        button.setPreferredSize(new Dimension(100, 25));
        button.setMinimumSize(new Dimension(100, 25));
        
        button.addActionListener(e -> {
            Calendar cal = Calendar.getInstance();
            Date endDate = cal.getTime(); // Today
            
            if (daysFromToday < 0) {
                cal.add(Calendar.DAY_OF_MONTH, daysFromToday);
            }
            Date startDate = cal.getTime();
            
            startSpinner.setValue(startDate);
            endSpinner.setValue(endDate);
        });
        
        return button;
    }
    
    /**
     * Generates Canada Food Guide alignment analysis for the given date range.
     */
    public JPanel presentFoodGuideAlignment(Date startDate, Date endDate) {
        try {
            // Fetch meals using the service
            QueryMealsService.QueryMealsServiceOutput result = 
                QueryMealsService.instance().getMealsByDate(startDate, endDate);
            
            if (!result.ok()) {
                return createErrorPanel("Failed to fetch meals: " + result.errors());
            }
            
            List<Meal> meals = result.getMeals();
            
            if (meals.isEmpty()) {
                return createNoDataPanel("No meals found for the specified date range. Please log some meals first.");
            }
            
            // Perform CFG analysis
            CFGAnalysisResult analysis = StatisticsService.instance().analyzeCanadaFoodGuide(meals);
            
            if (analysis.getTotalGrams() == 0) {
                return createNoDataPanel("No food data available for analysis.");
            }
            
            // Create visualization
            return createCFGVisualizationPanel(analysis);
            
        } catch (Exception e) {
            e.printStackTrace();
            return createErrorPanel("Error generating Canada Food Guide analysis: " + e.getMessage());
        }
    }
    
    private JPanel createCFGVisualizationPanel(CFGAnalysisResult analysis) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create chart panel
        JPanel chartPanel = createCFGChart(analysis);
        mainPanel.add(chartPanel, BorderLayout.CENTER);
        
        // Create summary panel
        JPanel summaryPanel = createCFGSummary(analysis);
        mainPanel.add(summaryPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JPanel createCFGChart(CFGAnalysisResult analysis) {
        // Create pie chart dataset
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        for (CanadaFoodGuideCategory category : CanadaFoodGuideCategory.values()) {
            double actual = analysis.getActualPercentages().get(category);
            double target = analysis.getTargetPercentages().get(category);
            String label = String.format("%s (%.1f%% vs %.0f%% target)", 
                category.getDisplayName(), actual, target);
            dataset.setValue(label, actual);
        }
        
        // Create chart
        JFreeChart chart = ChartFactory.createPieChart(
            "Canada Food Guide Alignment", 
            dataset, 
            true, 
            true, 
            false
        );
        
        // Customize the plot
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        
        // Set colors for CFG categories
        plot.setSectionPaint(0, new Color(76, 175, 80));    // Green for Vegetables & Fruits
        plot.setSectionPaint(1, new Color(255, 193, 7));    // Yellow for Whole Grains
        plot.setSectionPaint(2, new Color(244, 67, 54));    // Red for Protein Foods
        plot.setSectionPaint(3, new Color(158, 158, 158));  // Gray for Other
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 400));
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCFGSummary(CFGAnalysisResult analysis) {
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Analysis Summary"));
        
        // Create summary text
        JTextArea summaryText = new JTextArea();
        summaryText.setEditable(false);
        summaryText.setWrapStyleWord(true);
        summaryText.setLineWrap(true);
        summaryText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        summaryText.setBackground(summaryPanel.getBackground());
        
        StringBuilder text = new StringBuilder();
        text.append("Canada Food Guide Analysis Summary:\n\n");
        
        // Add dataset limitation warning
        if (analysis.hasDatasetLimitations()) {
            text.append("DATASET LIMITATION: Our database contains only 9 out of 22 official CNF food groups.\n");
            text.append("   Missing: Grains/Breads, Fruits, Fish, Nuts, etc. This affects analysis accuracy.\n\n");
        }
        
        for (CanadaFoodGuideCategory category : CanadaFoodGuideCategory.values()) {
            double actual = analysis.getActualPercentages().get(category);
            double target = analysis.getTargetPercentages().get(category);
            double grams = analysis.getActualGrams().get(category);
            
            text.append(String.format("• %s: %.1f%% (%.0fg)", 
                category.getDisplayName(), actual, grams));
            
            if (category == CanadaFoodGuideCategory.OTHER) {
                text.append(" (not part of CFG recommendations)");
            } else {
                double difference = actual - target;
                            if (Math.abs(difference) <= 5) {
                text.append(" [ON TARGET]");
            } else if (difference > 0) {
                text.append(String.format(" [+%.1f%% above target]", difference));
            } else {
                text.append(String.format(" [-%.1f%% below target]", Math.abs(difference)));
            }
            }
            text.append("\n");
        }
        
        text.append(String.format("\nTotal food analyzed: %.0fg", analysis.getTotalGrams()));
        
        if (!analysis.getUnrecognizedFoods().isEmpty()) {
            text.append(String.format("\n\n%d unrecognized foods (not included in analysis):\n", 
                analysis.getUnrecognizedFoods().size()));
            for (String food : analysis.getUnrecognizedFoods()) {
                text.append("   • ").append(food).append("\n");
            }
        }
        
        summaryText.setText(text.toString());
        
        JScrollPane scrollPane = new JScrollPane(summaryText);
        scrollPane.setPreferredSize(new Dimension(600, 150));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        summaryPanel.add(scrollPane, BorderLayout.CENTER);
        
        return summaryPanel;
    }
    
    private JPanel createErrorPanel(String errorMessage) {
        JPanel errorPanel = new JPanel(new BorderLayout());
        JLabel errorLabel = new JLabel("<html><div style='text-align: center;'>" +
            "<h3 style='color: red;'>Error</h3>" +
            "<p>" + errorMessage + "</p>" +
            "</div></html>");
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorPanel.add(errorLabel, BorderLayout.CENTER);
        return errorPanel;
    }
    
    private JPanel createNoDataPanel(String message) {
        JPanel noDataPanel = new JPanel(new BorderLayout());
        JLabel noDataLabel = new JLabel("<html><div style='text-align: center;'>" +
            "<h3>No Data</h3>" +
            "<p>" + message + "</p>" +
            "</div></html>");
        noDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        noDataPanel.add(noDataLabel, BorderLayout.CENTER);
        return noDataPanel;
    }
} 