package meals.models.meal;

import data.IRecord;
import meals.models.food.Food;
import meals.models.food.Measure;

import java.util.Collection;
import java.util.List;

/**
 * Represents an item that is part of a meal.
 */
public class MealItem implements IRecord {

    private static final String TABLE_NAME = "meal_items";

    private int id;
    private Food food;
    // given measures like "10 Chips", "1 Head of Lettuce", "10 mL"
    // a quantity of 5 would represent "50 chips", "5 Heads of Lettuce", "50 mL"
    private Measure selectedMeasure;
    private float quantity;
    private Meal parentMeal;

    public static String getTableName() {
        return TABLE_NAME;
    }

    /**
     * @param id               The ID of the meal item.
     * @param food             The food associated with the meal item.
     * @param quantity         The quantity of the food for this meal item.
     */
    public MealItem(Integer id, Food food, Float quantity, Measure selectedMeasure) {
        this.id = id;
        this.food = food;
        this.quantity = quantity;
        this.selectedMeasure = selectedMeasure;
    }

    /**
     * @return The ID of the meal item.
     */
    public int getId() {
        return id;
    }

    /**
     * @return The food associated with the meal item.
     */
    public Food getFood() {
        return food;
    }

    /**
     * @return The quantity of the food for this meal item.
     */
    public float getQuantity() {
        return quantity;
    }

    /**
     * @return The measure the user selected for this meal item.
     */
    public Measure getSelectedMeasure() {
        return selectedMeasure;
    }

    /**
     * Sets the meal this meal item is part of.
     * This is not often needed, but necessary to save the meal_id column in the database.
     * @param parentMeal The meal this meal item is part of.
     */
    public void setParentMeal(Meal parentMeal) {
        this.parentMeal = parentMeal;
    }

    @Override
    public String toString() {
        return "MealItem(id: %s, food: %s, quantity: %.2f, measure: %s)".formatted(id, food, quantity, selectedMeasure);
    }

    @Override
    public Object getValue(String field) {
        return switch (field) {
            case "id" -> id;
            case "meal_id" -> parentMeal.getId();
            case "food_id" -> food.getFoodId();
            case "quantity" -> quantity;
            default -> null;
        };
    }

    @Override
    public Collection<String> fieldNames() {
        return List.of("id", "meal_id", "food_id", "quantity");
    }
}
