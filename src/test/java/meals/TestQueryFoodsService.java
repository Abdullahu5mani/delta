package meals;

import meals.models.food.Food;
import meals.models.food.FoodGroup;
import meals.models.food.Measure;
import meals.services.QueryFoodsService;
import meals.services.QueryNutrientsService;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestQueryFoodsService {
    private static Food buildChocolateDrink() {
        List<Measure> possibleMeasures = List.of(
                new Measure(341, "100ml", 1.03128f),
                new Measure(415, "250ml (1 cup)", 2.57819f)
        );
        FoodGroup foodGroup = new FoodGroup(14, "Beverages");
        return new Food(
                5589,
                "Chocolate flavour drink, whey and milk based",
                foodGroup,
                Collections.emptyMap(),
                possibleMeasures
        );
    }

    @Test
    void testFetchAll() throws QueryFoodsService.QueryFoodsServiceException {
        Food chocolateDrink = buildChocolateDrink();
        List<Food> foods = QueryFoodsService.instance().fetchAll();
        assertTrue(foods.size() > 1);
        Food food5589 = foods.stream().filter(food -> food.getFoodId().equals(5589)).toList().getFirst();
        // basic properties
        assertEquals(chocolateDrink.getFoodId(), food5589.getFoodId());
        assertEquals(chocolateDrink.getFoodDescription(), food5589.getFoodDescription());
        // associations
        compareFoodGroups(chocolateDrink.getFoodGroup(), food5589.getFoodGroup());
        compareMeasureList(chocolateDrink.getPossibleMeasures(), food5589.getPossibleMeasures());
    }

    @Test
    void testFindById() throws QueryFoodsService.QueryFoodsServiceException, QueryNutrientsService.QueryNutrientServiceException {
        Food chocolateDrink = buildChocolateDrink();
        Food food5589 = QueryFoodsService.instance().findById(5589);
        assertEquals(chocolateDrink.getFoodId(), food5589.getFoodId());
        assertEquals(chocolateDrink.getFoodDescription(), food5589.getFoodDescription());
        // associations
        compareFoodGroups(chocolateDrink.getFoodGroup(), food5589.getFoodGroup());
        compareMeasureList(chocolateDrink.getPossibleMeasures(), food5589.getPossibleMeasures());
        // FATTY ACIDS, MONOUNSATURATED, TOTAL
        float actualAmount = food5589.getNutrientAmount(QueryNutrientsService.instance().findById(645));
        assertEquals(0.049F, actualAmount, 0001F);
    }

    void compareFoodGroups(FoodGroup expectedFoodGroup, FoodGroup actualFoodGroup) {
        assertEquals(expectedFoodGroup.getFoodGroupId(), actualFoodGroup.getFoodGroupId());
        assertEquals(expectedFoodGroup.getFoodGroupName(), actualFoodGroup.getFoodGroupName());
    }

    void compareMeasureList(List<Measure> expectedMeasures, List<Measure> actualMeasures) {
        assertEquals(expectedMeasures.size(), actualMeasures.size());
        for (int i = 0; i < expectedMeasures.size(); i++) {
            compareMeasures(expectedMeasures.get(i), actualMeasures.get(i));
        }
    }

    void compareMeasures(Measure expectedMeasure, Measure actualMeasure) {
        assertEquals(expectedMeasure.getId(), actualMeasure.getId());
        assertEquals(expectedMeasure.getName(), actualMeasure.getName());
        assertEquals(expectedMeasure.getConversionValue(), actualMeasure.getConversionValue());
    }
}
