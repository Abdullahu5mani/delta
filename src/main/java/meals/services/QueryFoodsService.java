package meals.services;

import data.Comparison;
import data.DatabaseException;
import data.IRecord;
import data.SelectQuery;
import meals.models.food.Food;
import meals.models.food.FoodGroup;
import meals.models.food.Measure;
import meals.models.nutrient.Nutrient;
import shared.AppBackend;

import java.util.*;

/**
 * Service that queries foods.
 */
public class QueryFoodsService {
    private static final String QUERY_FOODS_SERVICE_ERROR_MESSAGE = "An error occurred when fetching foods.";

    /**
     * Exception raised if an error occurs while executing the service.
     */
    public static class QueryFoodsServiceException extends Exception {
        public QueryFoodsServiceException(String errorMessage) {
            super(errorMessage);
        }
    }

    private static QueryFoodsService instance;

    private QueryFoodsService() {
    }

    /**
     * @return Singleton instance of the service.
     */
    public static QueryFoodsService instance() {
        if (instance == null) instance = new QueryFoodsService();
        return instance;
    }

    /**
     * Returns a list of all foods.
     * @return A list of all foods in the database.
     * @throws QueryFoodsServiceException Thrown if an error occurs while executing the service.
     */
    public List<Food> fetchAll() throws QueryFoodsServiceException {
        try {
            List<IRecord> records = AppBackend.db().execute(new SelectQuery(Food.getTableName()));
            List<Food> foods = new ArrayList<>();
            for (IRecord record : records) foods.add(buildFoodFromRecord(record));
            return foods;
        } catch (DatabaseException | QueryFoodGroupsService.QueryFoodGroupServiceException |
                 QueryNutrientsService.QueryNutrientServiceException e) {
            throw new QueryFoodsServiceException(QUERY_FOODS_SERVICE_ERROR_MESSAGE);
        }
    }

    /**
     * Queries for the food with the specified id.
     * @param id The id of the food to query for.
     * @return The food with the specified id.
     * @throws QueryFoodsServiceException Thrown if an error occurs while executing the service.
     */
    public Food findById(int id) throws QueryFoodsServiceException {
        try {
            SelectQuery query = new SelectQuery(Food.getTableName()).filter("id", Comparison.EQUAL, id);
            List<IRecord> records = AppBackend.db().execute(query);
            
            if (records.isEmpty()) {
                System.out.println("⚠️ Food not found with ID: " + id);
                throw new QueryFoodsServiceException("Food not found with ID: " + id);
            }
            
            IRecord record = records.getFirst();
            return buildFoodFromRecord(record);
        } catch (DatabaseException | QueryFoodGroupsService.QueryFoodGroupServiceException |
                 QueryNutrientsService.QueryNutrientServiceException e) {
            throw new QueryFoodsServiceException(QUERY_FOODS_SERVICE_ERROR_MESSAGE);
        }
    }

    /**
     * Builds a food instance from a database record.
     * @param food The raw data of the food.
     * @return The food instance corresponding to the record.
     */
    private static Food buildFoodFromRecord(IRecord food) throws QueryFoodGroupsService.QueryFoodGroupServiceException, DatabaseException, QueryNutrientsService.QueryNutrientServiceException {
        int foodId = (int) food.getValue("id");
        String description = (String) food.getValue("description");
        int foodGroupId = (int) food.getValue("food_group_id");
        FoodGroup foodGroup = QueryFoodGroupsService.instance().findById(foodGroupId);
        Map<Nutrient, Float> nutrientAmounts = QueryNutrientsService.instance().findNutrientQuantitiesForFood(foodId);
        return new Food(foodId, description, foodGroup, nutrientAmounts, getMeasures(foodId));
    }

    /**
     * Returns a list of measures for the specified food.
     * @param foodId The food to find the measures for.
     * @return The measures used for the specified food.
     */
    private static List<Measure> getMeasures(int foodId) throws DatabaseException {
        SelectQuery query = new SelectQuery(Measure.getConversionFactorsMeasuresTableName())
                .filter("food_id", Comparison.EQUAL, foodId);
        List<IRecord> records = AppBackend.db().execute(query);
        List<Measure> measures = new ArrayList<>();
        for (IRecord measureRecord : records) {
            int measureId = (int) measureRecord.getValue("measure_id");
            String measureName = (String) measureRecord.getValue("measure_name");
            // we are fine with losing precision because the value is constant and a small number of decimal places
            Double conversionFactorValue = (Double) measureRecord.getValue("conversion_factor_value");
            measures.add(new Measure(measureId, measureName, conversionFactorValue.floatValue()));
        }
        return measures;
    }
}
