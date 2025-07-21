package meals.models.meal;

import meals.models.food.Food;
import meals.models.food.Measure;

/**
 * Represents an item that is part of a meal.
 */
public class MealItem {

    private int id;
    private Food food;
    // given measures like "10 Chips", "1 Head of Lettuce", "10 mL"
    // a quantity of 5 would represent "50 chips", "5 Heads of Lettuce", "50 mL"
    private Measure selectedMeasure;
    private float quantity;

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

    public Measure getSelectedMeasure() {
        return selectedMeasure;
    }
}
