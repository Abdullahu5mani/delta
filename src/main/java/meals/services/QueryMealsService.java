package meals.services;


import data.Comparison;
import data.DatabaseException;
import data.IRecord;
import data.SelectQuery;
import meals.models.food.Food;
import meals.models.food.Measure;
import meals.models.meal.Meal;
import meals.models.meal.MealItem;
import shared.AppBackend;
import shared.service_output.ServiceError;
import shared.service_output.ServiceOutput;
import shared.utils.DateToString;

import java.util.*;

/**
 * Service to query meals from the database.
 */
public class QueryMealsService {
    private static final String DATABASE_EXCEPTION_MESSAGE = "An error occurred when fetching meals from the database.";
    private static QueryMealsService instance;

    /**
     * Output of the service consisting of a list of meals and errors.
     */
    public static class QueryMealsServiceOutput extends ServiceOutput {
        private final List<Meal> meals;

        public QueryMealsServiceOutput(List<Meal> meals, List<ServiceError> errors) {
            super(errors);
            this.meals = meals;
        }

        public List<Meal> getMeals() {
            return meals;
        }
    }

    /**
     * @return Singleton instance of the service.
     */
    public static QueryMealsService instance() {
        if (instance == null) {
            instance = new QueryMealsService();
        }
        return instance;
    }

    /**
     * Prevent instantiation by clients following singleton pattern.
     */
    private QueryMealsService() {}

    /**
     * Queries the database and builds a list of meals for the specified date.
     * @param fromDate The start date to query (inclusive).
     * @param toDate The end date to query (inclusive).
     * @return A list of meals for the specified date range.
     */
    public QueryMealsServiceOutput getMealsByDate(Date fromDate, Date toDate) {
        try {
            // TO DO: filter on current user id
            List<IRecord> mealRecords = AppBackend.db().execute(
                    new SelectQuery(Meal.getTableName())
                            .filter("created_on", Comparison.GREATER_EQUAL, DateToString.call(fromDate))
                            .filter("created_on", Comparison.LESS_EQUAL, DateToString.call(toDate))
            );
            List<Meal> meals = new ArrayList<>();
            for (IRecord mealRecord: mealRecords) {
                meals.add(buildMealForRecord(mealRecord));
            }
            return new QueryMealsServiceOutput(meals, Collections.emptyList());
        } catch (DatabaseException | QueryFoodsService.QueryFoodsServiceException e) {
            List<ServiceError> errors = List.of(new ServiceError(DATABASE_EXCEPTION_MESSAGE + e.getMessage()));
            return new QueryMealsServiceOutput(Collections.emptyList(), errors);
        }
    }

    /**
     * Builds a meal object given a record containing the raw data.
     * @param mealRecord The raw meal data.
     * @return A meal object built using the raw data.
     * @throws DatabaseException Thrown if a database error occurs when querying the database.
     */
    private static Meal buildMealForRecord(IRecord mealRecord) throws DatabaseException, QueryFoodsService.QueryFoodsServiceException {
        Integer id = (Integer) mealRecord.getValue("id");
        Meal.MealType mealType = Meal.MealType.fromString((String) mealRecord.getValue("meal_type"));
        Date createdAt = (Date) mealRecord.getValue("created_on");
        List<MealItem> mealItems = buildMealItemsForMeal(mealRecord);
        return new Meal(id, mealType,mealItems, createdAt);
    }

    /**
     * Builds a list of meal items for the given meal record.
     * @param mealRecord The raw data for the meal item.
     * @return A meal item built using the raw data.
     * @throws DatabaseException Thrown if a database error occurs when querying the database.
     */
    private static List<MealItem> buildMealItemsForMeal(IRecord mealRecord) throws DatabaseException, QueryFoodsService.QueryFoodsServiceException {
        List<IRecord> mealItemRecords = AppBackend.db().execute(
                new SelectQuery(MealItem.getTableName())
                        .filter("meal_id", Comparison.EQUAL, mealRecord.getValue("id"))
        );
        List<MealItem> mealItems = new ArrayList<>();
        for (IRecord mealItemRecord: mealItemRecords) {
            mealItems.add(buildMealItemForRecord(mealItemRecord));
        }
        return mealItems;
    }

    /**
     * Builds a meal item for the given record.
     * @param mealItemRecord The raw data to use to build the meal item.
     * @return The meal item built using the raw data.
     */
    private static MealItem buildMealItemForRecord(IRecord mealItemRecord) throws QueryFoodsService.QueryFoodsServiceException {
        Integer id = (Integer) mealItemRecord.getValue("id");
        Integer foodId = (Integer) mealItemRecord.getValue("food_id");
        Food food = QueryFoodsService.instance().findById(foodId);
        Float quantity = (Float) mealItemRecord.getValue("quantity");
        Integer measureId = (Integer) mealItemRecord.getValue("measure_id");
        // we know the measure belonging to the meal item also belongs to the food,
        // so we can search the list of measures for the food instead of querying the database
        Measure measure = food.getPossibleMeasures()
                .stream()
                .filter(possibleMeasure -> possibleMeasure.getId() == measureId)
                .toList()
                .getFirst();
        return new MealItem(id, food, quantity, measure);
    }

    /**
     * Example script demonstrating usage of the service.
     * @param args Command line args (unused).
     */
    public static void main(String[] args) {
        QueryMealsServiceOutput result = QueryMealsService.instance().getMealsByDate(new Date(), new Date());
        if (result.ok()) {
            for (Meal meal: result.meals) {
                System.out.println(meal);
                for (MealItem mealItem: meal.getMealItems()) {
                    System.out.println(mealItem);
                }
            }
        }
        else {
            System.out.println(result.errors());
        }
    }
}
