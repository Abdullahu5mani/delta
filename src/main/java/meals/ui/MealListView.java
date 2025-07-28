package meals.ui;

import meals.models.meal.Meal;
import shared.ui.searchable_list.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static shared.ui.searchable_list.UIConstants.*;

public class MealListView extends JPanel {
    private static final String TITLE = "View Meals";
    private static final String SELECT_DATE_LABEL = "Select Date:";
    private static final String LOAD_MEALS_BUTTON_LABEL = "Load Meals";
    private static final String NO_MEALS_MESSAGE = "No meals found for the selected date.";

    private JComboBox<String> dateSelector;
    private JButton loadMealsButton;
    private DefaultListModel<MealListItem> mealListModel;
    private JList<MealListItem> mealsList;
    private JLabel statusLabel;

    public MealListView() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        initializeDateSelector();
        initializeMealsList();
        initializeComponents();
    }

    private void initializeDateSelector() {
        dateSelector = new JComboBox<>();
        LocalDate today = LocalDate.now();
        
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(i);
            String displayText = i == 0 ? "Today" : 
                               i == 1 ? "Yesterday" : 
                               date.toString();
            dateSelector.addItem(displayText + " (" + date.toString() + ")");
        }
        
        dateSelector.setMaximumSize(new Dimension(DEFAULT_TEXT_FIELD_WIDTH, DEFAULT_TEXT_FIELD_HEIGHT));
        loadMealsButton = new JButton(LOAD_MEALS_BUTTON_LABEL);
    }

    private void initializeMealsList() {
        mealListModel = new DefaultListModel<>();
        mealsList = new JList<>(mealListModel);
        mealsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        statusLabel = new JLabel(" ");
    }

    private void initializeComponents() {
        JLabel titleLabel = new JLabel(TITLE);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
        this.add(titleLabel);
        this.add(Box.createVerticalStrut(10));

        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datePanel.add(new JLabel(SELECT_DATE_LABEL));
        datePanel.add(dateSelector);
        datePanel.add(loadMealsButton);
        this.add(datePanel);

        this.add(Box.createVerticalStrut(10));
        this.add(statusLabel);

        JScrollPane mealsScrollPane = new JScrollPane(mealsList);
        mealsScrollPane.setPreferredSize(new Dimension(400, 300));
        this.add(mealsScrollPane);
    }

    protected Date getSelectedDate() {
        String selectedItem = (String) dateSelector.getSelectedItem();
        if (selectedItem == null) return new Date();
        
        int startIndex = selectedItem.lastIndexOf('(') + 1;
        int endIndex = selectedItem.lastIndexOf(')');
        String dateString = selectedItem.substring(startIndex, endIndex);
        
        LocalDate localDate = LocalDate.parse(dateString);
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    protected void updateMealsList(List<Meal> meals) {
        mealListModel.clear();
        
        if (meals.isEmpty()) {
            statusLabel.setText(NO_MEALS_MESSAGE);
            statusLabel.setForeground(Color.GRAY);
        } else {
            statusLabel.setText("Found " + meals.size() + " meal(s)");
            statusLabel.setForeground(Color.BLACK);
            
            for (Meal meal : meals) {
                mealListModel.addElement(new MealListItem(meal));
            }
        }
    }

    protected void showLoading() {
        statusLabel.setText("Loading meals...");
        statusLabel.setForeground(Color.BLUE);
        mealListModel.clear();
    }

    protected void showError(String errorMessage) {
        statusLabel.setText("Error: " + errorMessage);
        statusLabel.setForeground(Color.RED);
        mealListModel.clear();
    }

    protected Meal getSelectedMeal() {
        MealListItem selectedItem = mealsList.getSelectedValue();
        return selectedItem != null ? selectedItem.meal() : null;
    }

    protected void registerLoadMealsListener(Runnable listener) {
        loadMealsButton.addActionListener(e -> listener.run());
    }

    protected void registerMealSelectionListener(Runnable listener) {
        mealsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                listener.run();
            }
        });
    }

    protected record MealListItem(Meal meal) {
        @Override
        public String toString() {
            // Format the creation time
            java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm");
            String timeString = timeFormat.format(meal.getCreatedAt());
            
            // Get the first few food names for a more descriptive display
            String foodNames = meal.getMealItems().stream()
                    .limit(2) // Show first 2 foods
                    .map(item -> item.getFood().getFoodDescription())
                    .reduce("", (a, b) -> a.isEmpty() ? b : a + ", " + b);
            
            if (meal.getMealItems().size() > 2) {
                foodNames += " +" + (meal.getMealItems().size() - 2) + " more";
            }
            
            return String.format("%s (%s) - %s [%d items]", 
                    meal.getMealType(), 
                    timeString,
                    foodNames.isEmpty() ? "No items" : foodNames,
                    meal.getMealItems().size());
        }
    }
} 