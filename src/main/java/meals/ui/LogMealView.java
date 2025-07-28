package meals.ui;


import meals.models.food.Food;
import meals.models.food.Measure;
import meals.models.meal.Meal;
import meals.services.QueryFoodsService;
import shared.ui.searchable_list.SearchableListView;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static shared.ui.searchable_list.UIConstants.*;

/**
 * View enabling the user to create a meal through the UI.
 */
public class LogMealView extends JPanel {
    private static final String TITLE = "Log Meal";
    private static final String ADD_FOOD_BUTTON_LABEL = "Add Food";
    private static final String REMOVE_ITEM_BUTTON_LABEL = "Remove Item";
    private static final String CREATE_MEAL_BUTTON_LABEL = "Create Meal";
    private static final String QUANTITY_TEXT_BOX_LABEL = "Quantity: ";
    private static final String DEFAULT_QUANTITY_TEXT_FIELD_VALUE = "1.0";

    private final JButton addFoodButton;
    private final JButton removeFoodButton;
    private SearchableListView<FoodSearchBoxOption> foodSearchBox;
    private final JComboBox<Measure> measureOptions;
    private DefaultListModel<SelectedFoodListItem> selectedFoodListModel;
    private JList<SelectedFoodListItem> selectedFoodList;
    private JPanel quantityFormFieldContainer;
    private JComboBox<Meal.MealType> mealTypeDropdown;
    private JTextField quantityField;
    private final JButton createMealButton;

    public LogMealView() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        addFoodButton = new JButton(ADD_FOOD_BUTTON_LABEL);
        removeFoodButton = new JButton(REMOVE_ITEM_BUTTON_LABEL);
        createMealButton = new JButton(CREATE_MEAL_BUTTON_LABEL);
        measureOptions = new JComboBox<>();
        measureOptions.setMaximumSize(new Dimension(DEFAULT_TEXT_FIELD_WIDTH, DEFAULT_TEXT_FIELD_HEIGHT));
        initializeMealTypeDropdown();
        initSelectedFoodList();
        initQuantityField();
        initFoodSearchBox();
        initComponents();
    }

    /* constructor helper methods */

    /**
     * Adds components to the UI.
     * Helper method to be called in the constructor. To be called after each component is initialized.
     */
    private void initComponents() {
        this.add(new JLabel(TITLE));
        this.add(mealTypeDropdown);
        this.add(quantityFormFieldContainer);
        this.add(measureOptions);
        this.add(foodSearchBox);
        this.add(selectedFoodList);
        this.add(addFoodButton);
        this.add(removeFoodButton);
        this.add(createMealButton);
    }

    /**
     * Initializes the meal type dropdown.
     */
    private void initializeMealTypeDropdown() {
        mealTypeDropdown = new JComboBox<>();
        mealTypeDropdown.addItem(Meal.MealType.BREAKFAST);
        mealTypeDropdown.addItem(Meal.MealType.LUNCH);
        mealTypeDropdown.addItem(Meal.MealType.DINNER);
        mealTypeDropdown.addItem(Meal.MealType.SNACK);
        mealTypeDropdown.setMaximumSize(new Dimension(DEFAULT_TEXT_FIELD_WIDTH, DEFAULT_TEXT_FIELD_HEIGHT));
    }

    /**
     * Initializes the quantity field.
     * Helper method to be called in the constructor.
     */
    private void initQuantityField() {
        quantityFormFieldContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        quantityField = new JTextField(DEFAULT_QUANTITY_TEXT_FIELD_VALUE);
        quantityField.setPreferredSize(new Dimension(DEFAULT_TEXT_FIELD_WIDTH, DEFAULT_TEXT_FIELD_HEIGHT));
        quantityField.setMaximumSize(new Dimension(DEFAULT_TEXT_FIELD_WIDTH, DEFAULT_TEXT_FIELD_HEIGHT));
        quantityFormFieldContainer.add(new JLabel(QUANTITY_TEXT_BOX_LABEL));
        quantityFormFieldContainer.add(quantityField);
    }

    /**
     * Initializes the search box allowing the user to search for foods.
     * Helper method to be called in the constructor.
     */
    private void initFoodSearchBox() {
        // TO DO: move this logic to the presenter
        // may need to modify the search box class to support setting the list of items
        try {
            List<Food> foodList = QueryFoodsService.instance().fetchAll();
            List<FoodSearchBoxOption> foodSearchBoxOptions = foodList.stream().map(FoodSearchBoxOption::new).toList();
            foodSearchBox = new SearchableListView<>(foodSearchBoxOptions);
        } catch (QueryFoodsService.QueryFoodsServiceException e) {
            showErrorMessage("An error occurred while getting the list of foods: " + e.getMessage());
        }
    }

    /**
     * Initializes the list of selected foods to be part of the meal.
     * Helper method to be called in the constructor.
     */
    private void initSelectedFoodList() {
        selectedFoodListModel = new DefaultListModel<>();
        selectedFoodList = new JList<>(selectedFoodListModel);
    }

    /* methods handling listener registration */

    /**
     * Registers a listener to be run when the add food button is clicked.
     * @param listener The function to run when the add food button is clicked.
     */
    protected void registerAddFoodListener(Runnable listener) {
        addFoodButton.addActionListener((e) -> {
            listener.run();
        });
    }

    /**
     * Registers a listener to run when the user clicks the remove item button.
     * @param listener The listener to run when the user clicks the remove item button.
     */
    protected void registerRemoveItemListener(Runnable listener) {
        removeFoodButton.addActionListener((e) -> {
            listener.run();
        });
    }

    /**
     * Registers a listener to run when the user clicks the create meal button.
     * @param listener The listener to run when the user clicks the create meal button.
     */
    protected void registerCreateMealButtonListener(Runnable listener) {
        createMealButton.addActionListener((e) -> listener.run());
    }

    /*  methods handling adding items to a meal */

    /**
     * @return The selected food to be added to the meal.
     */
    protected FoodSearchBoxOption getSelectedFood() {
        return foodSearchBox.getSelectedItem();
    }

    /**
     * @return The quantity selected from the dropdown. null if the quantity is invalid.
     */
    protected Float getSelectedQuantity() {
        float quantityFromFormField;
        try {
            quantityFromFormField = Float.parseFloat(quantityField.getText().trim());
        } catch (NumberFormatException e) {
            return null;
        }
        return quantityFromFormField > 0 ? quantityFromFormField : null;
    }

    /**
     * Adds the selected food to the meal, along with the quantity and selected measure.
     * @param selectedFoodListItem The food to add to the meal.
     */
    protected void addSelectedFood(SelectedFoodListItem selectedFoodListItem) {
        selectedFoodListModel.addElement(selectedFoodListItem);
    }

    /**
     * @return The search box allowing the user to search and select foods.
     */
    protected SearchableListView<FoodSearchBoxOption> getFoodSearchBox() {
        return foodSearchBox;
    }

    /**
     * @return The selected measure for the meal item. (ex. "1 Cup")
     */
    protected Measure getSelectedMeasure() {
        Measure selectedMeasure = (Measure) measureOptions.getSelectedItem();
        System.out.println("📏 getSelectedMeasure() called - Selected: " + (selectedMeasure != null ? selectedMeasure.getName() : "NULL"));
        System.out.println("📏 Available measures count: " + measureOptions.getItemCount());
        return selectedMeasure;
    }

    /**
     * Sets the list of measures to appear in the dropdown. This list will differ for different foods.
     * Selects the first item by default.
     * @param measures The list of measures to appear in the dropdown.
     */
    protected void setAvailableMeasures(List<Measure> measures) {
        System.out.println("📏 Setting available measures - Count: " + measures.size());
        measureOptions.removeAllItems();
        for (int i = 0; i < measures.size(); i++) {
            Measure measure = measures.get(i);
            System.out.println("   " + (i+1) + ". " + measure.getName());
            measureOptions.addItem(measure);
        }
        if (!measures.isEmpty()) {
            Measure firstMeasure = measures.getFirst();
            measureOptions.setSelectedItem(firstMeasure);
            System.out.println("✅ Selected first measure: " + firstMeasure.getName());
        } else {
            System.out.println("⚠️ No measures available!");
        }
    }

    /*  methods for items already added to meal */

    /**
     * Gets the list of foods added to the meal, along with the quantity and selected measure.
     * @return The list of selected foods.
     */
    protected List<SelectedFoodListItem> getSelectedItemsAddedToMeal() {
        List<SelectedFoodListItem> list = new ArrayList<>();
        for (int i = 0; i < selectedFoodListModel.size(); i ++) {
            list.add(selectedFoodListModel.getElementAt(i));
        }
        return list;
    }

    /**
     * Get the meal item that the user has selected.
     * @return The meal item that the user has selected. null if no meal item is selected
     */
    protected SelectedFoodListItem getSelectedItemAddedToMeal() {
        return selectedFoodList.getSelectedValue();
    }

    /**
     * Removes an item from the meal.
     * @param selectedFoodListItem The item to remove from the meal.
     */
    protected void removeItem(SelectedFoodListItem selectedFoodListItem) {
        selectedFoodListModel.removeElement(selectedFoodListItem);
    }

    /**
     * @return The selected type of the meal.
     */
    protected Meal.MealType getSelectedMealType() {
        return (Meal.MealType) mealTypeDropdown.getSelectedItem();
    }

    /* other methods */

    /**
     * Renders a flash message showing an error.
     * @param message The error message to render.
     */
    protected void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, ERROR_FLASH_MESSAGE_TITLE, JOptionPane.ERROR_MESSAGE);
    }
}
