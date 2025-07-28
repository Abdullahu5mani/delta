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
import shared.utils.DateRangeUtils;

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
            // Get current user ID
            int currentUserId = 1; // Default fallback
            try {
                var currentUser = shared.ServiceFactory.getProfileService().getCurrentSession();
                if (currentUser.isPresent()) {
                    currentUserId = currentUser.get().getId();
                    System.out.println("🔍 Querying meals for user ID: " + currentUserId);
                } else {
                    System.out.println("WARNING: No active user session, using default user ID: " + currentUserId);
                }
            } catch (Exception e) {
                System.out.println("ERROR: Error getting current user: " + e.getMessage());
            }
            
            // Create proper datetime ranges for the query using utility methods
            Date startOfDay = DateRangeUtils.getStartOfDay(fromDate);
            Date endOfDay = DateRangeUtils.getEndOfDay(toDate);
            
            List<IRecord> mealRecords = AppBackend.db().execute(
                    new SelectQuery(Meal.getTableName())
                            .filter("created_on", Comparison.GREATER_EQUAL, DateToString.call(startOfDay))
                            .filter("created_on", Comparison.LESS_EQUAL, DateToString.call(endOfDay))
                            .filter("user_id", Comparison.EQUAL, currentUserId)
            );
            List<Meal> meals = new ArrayList<>();
            for (IRecord mealRecord: mealRecords) {
                meals.add(buildMealForRecord(mealRecord));
            }
            return new QueryMealsServiceOutput(meals, Collections.emptyList());
        } catch (DatabaseException | QueryFoodsService.QueryFoodsServiceException e) {
            System.out.println("ERROR: Database/Service Exception in QueryMealsService:");
            System.out.println("   Error: " + e.getMessage());
            System.out.println("   Exception type: " + e.getClass().getSimpleName());
            e.printStackTrace();
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
        String mealTypeString = (String) mealRecord.getValue("meal_type");
        Meal.MealType mealType = Meal.MealType.fromString(mealTypeString);
        
        // Get created_on as LocalDateTime (DATETIME field)
        java.time.LocalDateTime localDateTime = (java.time.LocalDateTime) mealRecord.getValue("created_on");
        Date createdAt = Date.from(localDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant());
        
        Integer userId = (Integer) mealRecord.getValue("user_id");
        List<MealItem> mealItems = buildMealItemsForMeal(mealRecord);
        return new Meal(id, mealType, mealItems, createdAt, userId);
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
        try {
            Integer id = (Integer) mealItemRecord.getValue("id");
            Integer foodId = (Integer) mealItemRecord.getValue("food_id");
            System.out.println("Building meal item - ID: " + id + ", Food ID: " + foodId);
            
            Food food = QueryFoodsService.instance().findById(foodId);
            System.out.println("Found food: " + food.getFoodDescription());
            
            Float quantity = (Float) mealItemRecord.getValue("quantity");
            Integer measureId = (Integer) mealItemRecord.getValue("measure_id");
            System.out.println("Looking for measure ID: " + measureId + " in food's possible measures");
            
            // we know the measure belonging to the meal item also belongs to the food,
            // so we can search the list of measures for the food instead of querying the database
            Measure measure = food.getPossibleMeasures()
                    .stream()
                    .filter(possibleMeasure -> possibleMeasure.getId() == measureId)
                    .toList()
                    .getFirst();
            
            if (measure == null) {
                System.out.println("ERROR: Measure not found for ID: " + measureId + " in food: " + food.getFoodDescription());
                throw new QueryFoodsService.QueryFoodsServiceException("Measure not found for ID: " + measureId);
            }
            System.out.println("Found measure: " + measure.getName());
            
            return new MealItem(id, food, quantity, measure);
        } catch (QueryFoodsService.QueryFoodsServiceException e) {
            System.out.println("ERROR: Food service error: " + e.getMessage());
            throw e;
        } catch (java.util.NoSuchElementException e) {
            System.out.println("ERROR: Measure not found in food's possible measures");
            throw new QueryFoodsService.QueryFoodsServiceException("Measure not found in food's possible measures");
        } catch (ClassCastException e) {
            System.out.println("ERROR: Data type error: " + e.getMessage());
            throw new QueryFoodsService.QueryFoodsServiceException("Invalid data type in meal item record");
        } catch (NullPointerException e) {
            System.out.println("ERROR: Null reference error: " + e.getMessage());
            throw new QueryFoodsService.QueryFoodsServiceException("Null reference in meal item data");
        }
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
