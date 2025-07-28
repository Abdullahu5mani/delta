package meals.ui;


import meals.models.food.Food;
import meals.models.food.Measure;

/**
 * Represents a food option with corresponding quantity and measure to add to a meal.
 * @param food The food to add to the meal.
 * @param quantity The quantity of the food.
 * @param measure The measure used for the food (ex. "1 Cup").
 */
public record SelectedFoodListItem(Food food, float quantity, Measure measure) {
    @Override
    public String toString() {
        String measureName = measure != null ? measure.getName() : "Unknown Measure";
        return "%s %.2f %s".formatted(food.getFoodDescription(), quantity, measureName);
    }
}
