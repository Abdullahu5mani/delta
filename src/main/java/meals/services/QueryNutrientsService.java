package meals.services;

import data.Comparison;
import data.DatabaseException;
import data.IRecord;
import data.SelectQuery;
import meals.models.nutrient.Nutrient;
import shared.AppBackend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Service that queries the database and constructs nutrients.
 * Caches query results. Even if we used the entire CSV, there are a relatively small number of nutrients.
 * Given that nutrients are immutable and there are a small number, we can cache the results to limit the number
 * of queries we have to make.
 */
public class QueryNutrientsService {
    private static final String QUERY_NUTRIENT_SERVICE_ERROR_MESSAGE = "An error occurred when fetching nutrients";

    private static final Map<Integer, Nutrient> cache = new TreeMap<>();

    /**
     * Exception to be raised when an error occurs when executing the service.
     */
    public static class QueryNutrientServiceException extends Exception {
        public QueryNutrientServiceException(String errorMessage) {
            super(errorMessage);
        }
    }

    private static QueryNutrientsService instance;

    private QueryNutrientsService() {
    }

    /**
     * @return Singleton instance for the service.
     */
    public static QueryNutrientsService instance() {
        if (instance == null) instance = new QueryNutrientsService();
        return instance;
    }

    /**
     * @return A list of all nutrients.
     * @throws QueryNutrientServiceException Thrown if an error occurs when fetching nutrients.
     */
    public List<Nutrient> fetchAll() throws QueryNutrientServiceException {
        // if values are in the cache, then avoid a database call
        if (!cache.isEmpty()) return cache.values().stream().toList();
        // otherwise query the database
        try {
            List<IRecord> records = AppBackend.db().execute(new SelectQuery(Nutrient.getTableName()));
            List<Nutrient> nutrients = records.stream().map(Nutrient::new).toList();
            // update cache
            for (Nutrient nutrient : nutrients) cache.put(nutrient.getNutrientId(), nutrient);
            return nutrients;
        } catch (DatabaseException e) {
            throw new QueryNutrientServiceException(QUERY_NUTRIENT_SERVICE_ERROR_MESSAGE);
        }
    }

    /**
     * Returns the nutrient with the specified ID.
     * @param id The id of the nutrient to query for.
     * @return The nutrient with the specified ID.
     */
    public Nutrient findById(int id) throws QueryNutrientServiceException {
        // if value in the cache, use the cache
        if (cache.containsKey(id)) return cache.get(id);
        QueryNutrientsService.instance().fetchAll(); // load all values into the cache
        return cache.get(id);
    }

    /**
     * Queries for the nutrients and their quantities for the specified food.
     * @param foodId The food ID to query nutrient amounts for.
     * @return The nutrients and their amounts for the specified food.
     */
    public Map<Nutrient, Float> findNutrientQuantitiesForFood(int foodId) throws QueryNutrientServiceException {
        Map<Nutrient, Float> nutrientAmountsMap = new HashMap<>();
        try {
            SelectQuery query = new SelectQuery(Nutrient.getNutrientAmountsTableName())
                    .filter("food_id", Comparison.EQUAL, foodId);
            List<IRecord> nutrientAmountRecords = AppBackend.db().execute(query);
            for (IRecord nutrientAmountRecord : nutrientAmountRecords) {
                Float amount = ((Double) nutrientAmountRecord.getValue("nutrient_value")).floatValue();
                Nutrient nutrient = QueryNutrientsService.instance()
                        .findById((int) nutrientAmountRecord.getValue("nutrient_id"));
                nutrientAmountsMap.put(nutrient, amount);
            }
        } catch (DatabaseException e) {
            throw new QueryNutrientServiceException(QUERY_NUTRIENT_SERVICE_ERROR_MESSAGE);
        }
        return nutrientAmountsMap;
    }
}
