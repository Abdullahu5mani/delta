package swaps.ui.swap_meal_details;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import meals.models.food.Food;
import swaps.models.SwapWithMealContext;

/**
 * View for comparing selected meals before/after a swap.
 */
public class SwapMealDetailsView extends JPanel {
    private JLabel titleLabel;
    private JLabel dateLabel;
    private JPanel oldMealPanel;
    private JPanel newMealPanel;
    private JTable oldNutritionTable;
    private JTable newNutritionTable;
    private DefaultTableModel oldNutritionModel;
    private DefaultTableModel newNutritionModel;
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEEE, MMMM dd, yyyy");

    public SwapMealDetailsView() {
        initializeComponents();
        initializeLayout();
    }
    
    private void initializeComponents() {
        // Title
        titleLabel = new JLabel("Meal Comparison: Before and After Swap");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        
        // Date label
        dateLabel = new JLabel("");
        dateLabel.setFont(dateLabel.getFont().deriveFont(Font.ITALIC, 14f));
        dateLabel.setForeground(Color.GRAY);
        
        // Old meal panel
        oldMealPanel = createMealPanel("Original Meal", Color.decode("#ffebeb"));
        
        // New meal panel  
        newMealPanel = createMealPanel("Meal with Swap", Color.decode("#ebffeb"));
        
        // Nutrition tables
        String[] columnNames = {"Nutrient", "Amount", "Unit"};
        oldNutritionModel = new DefaultTableModel(columnNames, 0);
        oldNutritionTable = new JTable(oldNutritionModel);
        
        newNutritionModel = new DefaultTableModel(columnNames, 0);
        newNutritionTable = new JTable(newNutritionModel);
    }
    
    private JPanel createMealPanel(String title, Color backgroundColor) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), title,
            0, 0, new Font(Font.SANS_SERIF, Font.BOLD, 14)));
        panel.setBackground(backgroundColor);
        return panel;
    }
    
    private void initializeLayout() {
        this.setLayout(new BorderLayout());
        
        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.add(titleLabel);
        headerPanel.add(dateLabel);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Main content panel with two columns
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        
        // Left column - Original meal
        gbc.gridx = 0;
        gbc.gridy = 0;
        JPanel oldMealContainer = new JPanel(new BorderLayout());
        oldMealContainer.add(oldMealPanel, BorderLayout.NORTH);
        oldMealContainer.add(new JScrollPane(oldNutritionTable), BorderLayout.CENTER);
        mainPanel.add(oldMealContainer, gbc);
        
        // Right column - New meal
        gbc.gridx = 1;
        gbc.gridy = 0;
        JPanel newMealContainer = new JPanel(new BorderLayout());
        newMealContainer.add(newMealPanel, BorderLayout.NORTH);
        newMealContainer.add(new JScrollPane(newNutritionTable), BorderLayout.CENTER);
        mainPanel.add(newMealContainer, gbc);
        
        this.add(headerPanel, BorderLayout.NORTH);
        this.add(mainPanel, BorderLayout.CENTER);
    }
    
    /**
     * Updates the view with swap information.
     */
    public void updateSwapDetails(SwapWithMealContext swapContext) {
        // Update date
        Date mealDate = swapContext.mealDate();
        String dateText = mealDate != null ? DATE_FORMAT.format(mealDate) : "Unknown date";
        dateLabel.setText("Meal from: " + dateText);
        
        // Update food information
        Food oldFood = swapContext.oldFood();
        Food newFood = swapContext.newFood();
        
        // Clear and update meal panels
        updateMealPanel(oldMealPanel, oldFood, "Remove:");
        updateMealPanel(newMealPanel, newFood, "Add:");
        
        // Update nutrition tables
        updateNutritionTable(oldNutritionModel, oldFood);
        updateNutritionTable(newNutritionModel, newFood);
    }
    
    private void updateMealPanel(JPanel panel, Food food, String prefix) {
        panel.removeAll();
        
        JLabel foodLabel = new JLabel(prefix + " " + food.getFoodDescription());
        foodLabel.setFont(foodLabel.getFont().deriveFont(Font.BOLD, 14f));
        panel.add(foodLabel);
        
        // Add some basic food information if available
        JLabel infoLabel = new JLabel("Food ID: " + food.getFoodId());
        infoLabel.setFont(infoLabel.getFont().deriveFont(Font.PLAIN, 12f));
        infoLabel.setForeground(Color.DARK_GRAY);
        panel.add(infoLabel);
        
        panel.revalidate();
        panel.repaint();
    }
    
    private void updateNutritionTable(DefaultTableModel model, Food food) {
        model.setRowCount(0); // Clear existing data
        
        // Add nutrition information
        food.getNutrientAmounts().forEach((nutrient, amount) -> {
            model.addRow(new Object[]{
                nutrient.getNutrientName(),
                String.format("%.2f", amount),
                nutrient.getNutrientUnit()
            });
        });
    }
}
