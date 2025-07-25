package meals.services;

import data.DatabaseException;
import data.IRecord;
import data.SelectQuery;
import meals.models.food.FoodGroup;
import shared.AppBackend;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Service that queries the database and builds food group instances.
 * Caches query results.
 * Because fod groups are immutable and there are a small number of them,
 * it makes sense for these to be cached in memory rather than querying the database each time.
 */
public class QueryFoodGroupsService {
    private static final String QUERY_FOOD_GROUP_SERVICE_ERROR_MESSAGE = "An error occurred when fetching food groups";

    // use a tree map for caching to allow traversing records by ascending id as if returned form the database
    private static final Map<Integer, FoodGroup> cache = new TreeMap<>();

    /**
     * Exception raised if an error occurs while performing a service.
     */
    public static class QueryFoodGroupServiceException extends Exception {
        public QueryFoodGroupServiceException(String errorMessage) {
            super(errorMessage);
        }
    }

    private static QueryFoodGroupsService instance;

    private QueryFoodGroupsService() {}

    /**
     * @return The singleton instance representing the service.
     */
    public static QueryFoodGroupsService instance() {
        if (instance == null) instance = new QueryFoodGroupsService();
        return instance;
    }

    /**
     * @return A list of all food groups.
     * @throws QueryFoodGroupServiceException Thrown if an error occurs while fetching records.
     */
    public List<FoodGroup> fetchAll() throws QueryFoodGroupServiceException {
        // if values are in the cache, then avoid a database call
        if (!cache.isEmpty()) return cache.values().stream().toList();
        // otherwise query the database
        try {
            List<IRecord> records = AppBackend.db().execute(new SelectQuery(FoodGroup.getTableName()));
            List<FoodGroup> foodGroups = records.stream().map(FoodGroup::new).toList();
            // update cache
            for (FoodGroup foodGroup : foodGroups) cache.put(foodGroup.getFoodGroupId(), foodGroup);
            return foodGroups;
        } catch (DatabaseException e) {
            throw new QueryFoodGroupServiceException(QUERY_FOOD_GROUP_SERVICE_ERROR_MESSAGE);
        }
    }

    /**
     * Fetches the food group with the specified ID.
     * @param id The id of the food group to query for.
     * @return The food group with the specified id.
     * @throws QueryFoodGroupServiceException Thrown if an error occurs when executing the service.
     */
    public FoodGroup findById(int id) throws QueryFoodGroupServiceException {
        if (cache.containsKey(id)) return cache.get(id);
        QueryFoodGroupsService.instance().fetchAll(); // load all data into cache
        return cache.get(id);
    }
}
