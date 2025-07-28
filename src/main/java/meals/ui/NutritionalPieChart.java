package meals.ui;

import meals.models.nutrient.Nutrient;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.stream.Collectors;

public class NutritionalPieChart extends JPanel {
    
    public NutritionalPieChart(Map<Nutrient, Float> nutrientAmounts, float quantity) {
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(400, 300));
        
        try {
            // Create pie chart for main nutrients (protein, fat, carbs, energy)
            JFreeChart chart = createNutritionPieChart(nutrientAmounts, quantity);
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(400, 300));
            
            this.add(chartPanel, BorderLayout.CENTER);
            
        } catch (Exception e) {
            JLabel errorLabel = new JLabel("Error creating chart: " + e.getMessage());
            errorLabel.setForeground(Color.RED);
            this.add(errorLabel, BorderLayout.CENTER);
        }
    }
    
    private JFreeChart createNutritionPieChart(Map<Nutrient, Float> nutrientAmounts, float quantity) {
        // Filter for main nutrients (protein, fat, carbs, energy)
        Map<Nutrient, Float> mainNutrients = nutrientAmounts.entrySet().stream()
                .filter(entry -> {
                    String symbol = entry.getKey().getNutrientSymbol();
                    return symbol.equals("PROT") || symbol.equals("FAT") || 
                           symbol.equals("CARB") || symbol.equals("KCAL");
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        
        // Create dataset
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        for (Map.Entry<Nutrient, Float> entry : mainNutrients.entrySet()) {
            Nutrient nutrient = entry.getKey();
            Float amount = entry.getValue() * quantity; // Adjust for quantity
            
            String nutrientName = getDisplayName(nutrient.getNutrientSymbol());
            dataset.setValue(nutrientName, amount);
        }
        
        // Create chart
        JFreeChart chart = ChartFactory.createPieChart(
                "Nutritional Breakdown",  // chart title
                dataset,                  // data
                true,                     // include legend
                true,
                false
        );
        
        // Customize the plot
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        
        // Set colors for different nutrients
        plot.setSectionPaint("Protein", new Color(255, 99, 132));   // Red
        plot.setSectionPaint("Fat", new Color(54, 162, 235));       // Blue
        plot.setSectionPaint("Carbohydrates", new Color(255, 205, 86)); // Yellow
        plot.setSectionPaint("Energy", new Color(75, 192, 192));    // Teal
        
        return chart;
    }
    
    private String getDisplayName(String symbol) {
        return switch (symbol) {
            case "PROT" -> "Protein";
            case "FAT" -> "Fat";
            case "CARB" -> "Carbohydrates";
            case "KCAL" -> "Energy";
            default -> symbol;
        };
    }
} 