package meals.ui;


import meals.models.food.Food;

/**
 * Represents an option in the meal logging search box allowing the user to select foods to add to a meal.
 * @param food The food associated with this search box option.
 */
public record FoodSearchBoxOption(Food food) {
   @Override
    public String toString() {
       return food.getFoodDescription();
   }
}
