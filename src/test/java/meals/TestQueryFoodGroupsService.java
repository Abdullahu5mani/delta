package meals;

import meals.models.food.FoodGroup;
import meals.services.QueryFoodGroupsService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestQueryFoodGroupsService {
    private static final FoodGroup dairyAndEggProducts = new FoodGroup(1, "Dairy and Egg Products");

    @Test
    void testFetchAll() throws QueryFoodGroupsService.QueryFoodGroupServiceException {
        List<FoodGroup> foodGroups = QueryFoodGroupsService.instance().fetchAll();
        assertTrue(foodGroups.size() > 1);  // test multiple records are returned
        assertEquals(dairyAndEggProducts, foodGroups.getFirst());
    }

    @Test
    void testFindById() throws QueryFoodGroupsService.QueryFoodGroupServiceException {
        FoodGroup foodGroup = QueryFoodGroupsService.instance().findById(1);
        assertEquals(dairyAndEggProducts, foodGroup);
    }

    @Test
    void testFetchAllGetResultFromCache() throws QueryFoodGroupsService.QueryFoodGroupServiceException {
        QueryFoodGroupsService.instance().fetchAll();
        List<FoodGroup> foodGroups = QueryFoodGroupsService.instance().fetchAll();
        assertEquals(dairyAndEggProducts, foodGroups.getFirst());
    }

    @Test
    void testFindByIdGetResultFromCache() throws QueryFoodGroupsService.QueryFoodGroupServiceException {
        QueryFoodGroupsService.instance().findById(1);
        FoodGroup foodGroup = QueryFoodGroupsService.instance().findById(1);
        assertEquals(dairyAndEggProducts, foodGroup);
    }
}
