package meals;

import meals.models.nutrient.Nutrient;
import meals.services.QueryNutrientsService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestQueryNutrientsService {
    private static final Nutrient protein = new Nutrient(203, "PROT", "g", "PROTEIN");

    @Test
    void testFetchAll() throws QueryNutrientsService.QueryNutrientServiceException {
        List<Nutrient> nutrients = QueryNutrientsService.instance().fetchAll();
        assertTrue(nutrients.size() > 1);
        assertEquals(protein, nutrients.getFirst());
    }

    @Test
    void testFindById() throws QueryNutrientsService.QueryNutrientServiceException {
        Nutrient nutrient203 = QueryNutrientsService.instance().findById(203);
        assertEquals(protein, nutrient203);
    }

    @Test
    void testFetchAllGetResultFromCache() throws QueryNutrientsService.QueryNutrientServiceException {
        QueryNutrientsService.instance().fetchAll();
        List<Nutrient> nutrients = QueryNutrientsService.instance().fetchAll();
        assertEquals(protein, nutrients.getFirst());
    }

    @Test
    void testFindByIdGetResultFromCache() throws QueryNutrientsService.QueryNutrientServiceException {
        QueryNutrientsService.instance().findById(203);
        Nutrient nutrient203 = QueryNutrientsService.instance().findById(203);
        assertEquals(protein, nutrient203);
    }
}
