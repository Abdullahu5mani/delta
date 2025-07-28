package meals.models.meal;

import data.IRecord;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;


import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Represents a meal logged by the user.
 */
public class Meal implements IRecord {
    private static final String TABLE = "meals";

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

        /**
         * Returns the meal type corresponding to the given string.
         * Necessary to build a meal type from the string representation in the database.
         *
         * @param mealType The type of meal in string format.
         * @return The enum corresponding to this string value.
         */
        public static MealType fromString(String mealType) {
            if (mealType == null) return null;
            
            // Make case-insensitive comparison and handle common variations
            String normalized = mealType.trim().toLowerCase();
            return switch (normalized) {
                case "breakfast" -> MealType.BREAKFAST;
                case "lunch" -> MealType.LUNCH;
                case "dinner" -> MealType.DINNER;
                case "snack" -> MealType.SNACK;
                default -> null;
            };
        }

        @Override
        public String toString() {
            return label;
        }
    }

    /**
     * @return The name of the database table where meals are stored.
     */
    public static String getTableName() {
        return TABLE;
    }

    private final int id;
    private final MealType mealType;
    private final List<MealItem> mealItems;
    private final Date createdAt;
    private final int userId;

    /**
     * @param id        The unique identifier of the meal.
     * @param mealType  The type of meal.
     * @param mealItems The list of items for the meal (foods and their respective quantities).
     * @param createdAt The date when the meal was created.
     * @param userId    The ID of the user who created the meal.
     */
    public Meal(int id, MealType mealType, List<MealItem> mealItems, Date createdAt, int userId) {
        this.id = id;
        this.mealType = mealType;
        this.mealItems = mealItems;
        this.createdAt = createdAt;
        this.userId = userId;
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

    /**
     * @return The date of the meal.
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * @return The ID of the user who created the meal.
     */
    public int getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "Meal(id: %s, mealType: %s, createdAt: %s)".formatted(id, mealType, createdAt);
    }

    @Override
    public Object getValue(String field) {
        return switch (field) {
            case "id" -> id;
            case "meal_type" -> mealType.toString();
            case "user_id" -> userId;
            case "created_on" -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createdAt);
            default -> null;
        };
    }

    @Override
    public Collection<String> fieldNames() {
        return List.of("id", "meal_type", "user_id", "created_on");
    }
}
