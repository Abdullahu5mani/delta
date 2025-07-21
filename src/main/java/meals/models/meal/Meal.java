package meals.models.meal;

import java.util.Date;
import java.util.List;

/**
 * Represents a meal logged by the user.
 */
public class Meal {
    /**
     * The different types of meals.
     */
    public enum MealType {
        BREAKFAST("Breakfast"),
        LUNCH("Lunch"),
        DINNER("Dinner"),
        SNACK("Snack");

        private final String label;

        MealType(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private final int id;
    private final MealType mealType;
    private final List<MealItem> mealItems;

    /**
     * @param id        The unique identifier of the meal.
     * @param mealType  The type of meal.
     * @param mealItems The list of items for the meal (foods and their respective quantities).
     */
    public Meal(int id, MealType mealType, List<MealItem> mealItems, Date createdAt) {
        this.id = id;
        this.mealType = mealType;
        this.mealItems = mealItems;
    }

    /**
     * @return The unique identifier of the meal.
     */
    public int getId() {
        return id;
    }

    /**
     * @return The type of meal.
     */
    public MealType getMealType() {
        return mealType;
    }

    /**
     * @return The list of items for the meal (foods and their respective quantities).
     */
    public List<MealItem> getMealItems() {
        return mealItems;
    }
}