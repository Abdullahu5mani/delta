package meals.services;

import data.*;
import meals.models.MockDataFactory;
import meals.models.meal.Meal;
import meals.models.meal.MealItem;
import shared.AppBackend;
import shared.service_output.ServiceError;
import shared.service_output.ServiceOutput;
import shared.utils.DateToString;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Service that validates input and inserts a meal into the database. Including the meal items that are part of it.
 */
public class CreateMealService {
    private static final String DATABASE_ERROR_MESSAGE =
            "An error occurred when saving the meal to the database";
    private static final String VALIDATE_MEAL_LIMITS_ERROR_TEMPLATE =
            "You can only have one %s per day.";

    /**
     * Output of the service. Because we are inserting records, we just return a list of errors.
     */
    public static class CreateMealServiceOutput extends ServiceOutput {
        public CreateMealServiceOutput(List<ServiceError> errors) {
            super(errors);
        }
    }

    /**
     * Singleton instance of the service.
     */
    private static CreateMealService instance;

    /**
     * Prevent instantiation by clients to implement singleton pattern.
     */
    private CreateMealService() {
    }

    /**
     * @return The singleton instance of the service.
     */
    public static CreateMealService instance() {
        if (instance == null) {
            instance = new CreateMealService();
        }
        return instance;
    }

    /**
     * Inserts the specified meal into the database.
     * @param meal The meal to insert into the database.
     * @return The service output with a list of errors.
     */
    public CreateMealServiceOutput createMeal(Meal meal) {
        try {
            // validation
            ServiceError validationError = validateLimitsOnTypesOfMealPerDay(meal);
            if (validationError != null) return new CreateMealServiceOutput(List.of(validationError));
            // persist values
            AppBackend.db().execute(new InsertQuery(Meal.getTableName(), meal));
            for (MealItem mealItem : meal.getMealItems()) {
                createMealItem(meal, mealItem);
            }
        } catch (DatabaseException e) {
            ServiceError serviceError = new ServiceError(DATABASE_ERROR_MESSAGE + e.getMessage());
            return new CreateMealServiceOutput(List.of(serviceError));
        }
        // succeeded so return no errors
        return new CreateMealServiceOutput(Collections.emptyList());
    }

    /**
     * Validates that there are no meals that already have this type for this date.
     *
     * @param meal The meal that is being inserted.
     * @return An error if inserting the record would result in two of the specified meal for this day.
     * @throws DatabaseException Thrown if a database error occurs when querying the meals.
     */
    public ServiceError validateLimitsOnTypesOfMealPerDay(Meal meal) throws DatabaseException {
        Meal.MealType mealType = meal.getMealType();
        if (!mealType.equals(Meal.MealType.SNACK)) {
            boolean valid = validateNoneOfSpecifiedMealType(mealType, meal.getCreatedAt());
            if (!valid) {
                return new ServiceError(VALIDATE_MEAL_LIMITS_ERROR_TEMPLATE.formatted(mealType));
            }
        }
        return null;
    }

    /**
     * Helper method to validate if there is already a certain type of meal on a specified day.
     *
     * @param mealType The type of meal to validate.
     * @param date The date to verify.
     * @return True if there are no meals of this type for the specific day and false otherwise.
     * @throws DatabaseException Thrown if a database error occurs when querying the meals.
     */
    private static boolean validateNoneOfSpecifiedMealType(Meal.MealType mealType, Date date) throws DatabaseException {
        List<IRecord> rawMeals = AppBackend.db().execute(
                new SelectQuery("meals")
                        .filter("created_on", Comparison.EQUAL, DateToString.call(date))
        );
        String mealTypeString = mealType.toString();
        long count = rawMeals
                .stream()
                .filter(rawMeal -> rawMeal.getValue("meal_type").toString().equals(mealTypeString))
                .count();
        return count == 0;
    }

    /**
     * Inserts a meal item into the database.
     *
     * @param meal The meal that contains the meal item.
     * @param mealItem The meal item to be inserted.
     * @throws DatabaseException If a database error occurs when inserting the record.
     */
    private void createMealItem(Meal meal, MealItem mealItem) throws DatabaseException {
        // ensure we know which meal the meal items is part of, so we can save the meal_id as a foreign key
        mealItem.setParentMeal(meal);
        AppBackend.db().execute(new InsertQuery(MealItem.getTableName(), mealItem));
    }

    /**
     * Script demonstrating the usage of the service class.
     * @param args Command line args (unused).
     */
    public static void main(String[] args) {
        // insert valid meal
        CreateMealServiceOutput result =
                CreateMealService.instance().createMeal(MockDataFactory.createMockMeal(Meal.MealType.SNACK));
        if (result.ok()) System.out.println("Inserted meal into database.");
        else System.out.println(result.errors().getFirst());

        // insert second lunch should fail if there already exists one lunch for this day
        result =
                CreateMealService.instance().createMeal(MockDataFactory.createMockMeal(Meal.MealType.LUNCH));
        if (result.ok()) System.out.println("Inserted meal into database.");
        else System.out.println(result.errors().getFirst());
    }
}
