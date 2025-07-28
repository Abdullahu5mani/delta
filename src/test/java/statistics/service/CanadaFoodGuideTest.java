package statistics.service;

import meals.models.food.Food;
import meals.models.food.FoodGroup;
import meals.models.food.Measure;
import meals.models.meal.Meal;
import meals.models.meal.MealItem;
import meals.models.nutrient.Nutrient;
import org.junit.jupiter.api.Test;
import statistics.service.StatisticsService.CanadaFoodGuideCategory;
import statistics.service.StatisticsService.CFGAnalysisResult;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class CanadaFoodGuideTest {
    
    @Test
    public void testFoodGroupToCFGMapping() {
        StatisticsService service = StatisticsService.instance();
        
        // Test vegetables and fruits
        FoodGroup vegetableGroup = new FoodGroup(1, "Vegetables and Vegetable Products");
        assertEquals(CanadaFoodGuideCategory.VEGETABLES_AND_FRUITS, 
                    service.mapFoodGroupToCFG(vegetableGroup));
        
        // Test dairy/protein
        FoodGroup dairyGroup = new FoodGroup(2, "Dairy and Egg Products");
        assertEquals(CanadaFoodGuideCategory.PROTEIN_FOODS, 
                    service.mapFoodGroupToCFG(dairyGroup));
        
        // Test grains
        FoodGroup cerealGroup = new FoodGroup(3, "Breakfast cereals");
        assertEquals(CanadaFoodGuideCategory.WHOLE_GRAINS, 
                    service.mapFoodGroupToCFG(cerealGroup));
        
        // Test mixed dishes (new mapping)
        FoodGroup mixedGroup = new FoodGroup(4, "Mixed Dishes");
        assertEquals(CanadaFoodGuideCategory.PROTEIN_FOODS, 
                    service.mapFoodGroupToCFG(mixedGroup));
        
        // Test snacks (should return OTHER category)
        FoodGroup snackGroup = new FoodGroup(5, "Snacks");
        assertEquals(CanadaFoodGuideCategory.OTHER, service.mapFoodGroupToCFG(snackGroup));
    }
    
    @Test
    public void testRealWorldFoodMapping() {
        StatisticsService service = StatisticsService.instance();
        
        // Create real food groups from the CSV data
        FoodGroup mixedDishes = new FoodGroup(22, "Mixed Dishes");
        FoodGroup beverages = new FoodGroup(14, "Beverages");
        FoodGroup vegetables = new FoodGroup(11, "Vegetables and Vegetable Products");
        
        // Create sample nutrients and measures
        Map<Nutrient, Float> nutrients = new HashMap<>();
        nutrients.put(new Nutrient(1, "PROT", "g", "Protein"), 10.0f);
        List<Measure> measures = Arrays.asList(new Measure(1, "100ml", 1.0f));
        
        // Test mixed dishes with chicken (should go to protein)
        Food chowMein = new Food(5, "Chinese dish, chow mein, chicken", mixedDishes, nutrients, measures);
        CanadaFoodGuideCategory category = service.mapFoodToCFG(chowMein);
        assertEquals(CanadaFoodGuideCategory.PROTEIN_FOODS, category);
        
        // Test vegetable juice (should go to vegetables & fruits)
        Food vegJuice = new Food(5586, "Juice, tomato and vegetable, low sodium", beverages, nutrients, measures);
        category = service.mapFoodToCFG(vegJuice);
        assertEquals(CanadaFoodGuideCategory.VEGETABLES_AND_FRUITS, category);
        
        // Test coffee (should go to Other category)
        Food coffee = new Food(2873, "Coffee, brewed, prepared with tap water", beverages, nutrients, measures);
        category = service.mapFoodToCFG(coffee);
        assertEquals(CanadaFoodGuideCategory.OTHER, category);
        
        // Test actual vegetables (should go to vegetables & fruits)
        Food potatoes = new Food(5573, "Potatoes, canned, drained solids, unsalted", vegetables, nutrients, measures);
        category = service.mapFoodToCFG(potatoes);
        assertEquals(CanadaFoodGuideCategory.VEGETABLES_AND_FRUITS, category);
    }
    
    @Test
    public void testCFGAnalysisWithSampleMeals() {
        StatisticsService service = StatisticsService.instance();
        
        // Create sample meals with different food groups
        List<Meal> meals = createSampleMeals();
        
        // Analyze the meals
        CFGAnalysisResult result = service.analyzeCanadaFoodGuide(meals);
        
        // Verify the analysis
        assertNotNull(result);
        assertTrue(result.getTotalGrams() > 0);
        
        // Check that we have percentages for all categories
        Map<CanadaFoodGuideCategory, Double> percentages = result.getActualPercentages();
        assertEquals(4, percentages.size()); // Now includes OTHER category
        
        // Check target percentages are set correctly
        Map<CanadaFoodGuideCategory, Double> targets = result.getTargetPercentages();
        assertEquals(50.0, targets.get(CanadaFoodGuideCategory.VEGETABLES_AND_FRUITS));
        assertEquals(25.0, targets.get(CanadaFoodGuideCategory.WHOLE_GRAINS));
        assertEquals(25.0, targets.get(CanadaFoodGuideCategory.PROTEIN_FOODS));
        assertEquals(0.0, targets.get(CanadaFoodGuideCategory.OTHER)); // Other has 0% target
    }
    
    @Test
    public void testCFGAnalysisWithEmptyMeals() {
        StatisticsService service = StatisticsService.instance();
        
        List<Meal> emptyMeals = new ArrayList<>();
        CFGAnalysisResult result = service.analyzeCanadaFoodGuide(emptyMeals);
        
        assertNotNull(result);
        assertEquals(0.0, result.getTotalGrams());
        assertTrue(result.getUnrecognizedFoods().isEmpty());
    }
    
    private List<Meal> createSampleMeals() {
        List<Meal> meals = new ArrayList<>();
        
        // Create sample food groups
        FoodGroup vegetableGroup = new FoodGroup(1, "Vegetables and Vegetable Products");
        FoodGroup dairyGroup = new FoodGroup(2, "Dairy and Egg Products");
        FoodGroup cerealGroup = new FoodGroup(3, "Breakfast cereals");
        
        // Create sample nutrients
        Map<Nutrient, Float> nutrients = new HashMap<>();
        nutrients.put(new Nutrient(1, "PROT", "g", "Protein"), 10.0f);
        
        // Create sample measures
        List<Measure> measures = Arrays.asList(new Measure(1, "100g", 100.0f));
        
        // Create sample foods
        Food broccoli = new Food(1, "Broccoli", vegetableGroup, nutrients, measures);
        Food milk = new Food(2, "Milk, 2%", dairyGroup, nutrients, measures);
        Food oats = new Food(3, "Oats", cerealGroup, nutrients, measures);
        
        // Create meal items
        MealItem broccoliItem = new MealItem(1, broccoli, 1.0f, measures.get(0)); // 100g
        MealItem milkItem = new MealItem(2, milk, 0.5f, measures.get(0)); // 50g
        MealItem oatsItem = new MealItem(3, oats, 0.5f, measures.get(0)); // 50g
        
        // Create a meal
        List<MealItem> mealItems = Arrays.asList(broccoliItem, milkItem, oatsItem);
        Meal meal = new Meal(1, Meal.MealType.BREAKFAST, mealItems, new Date(), 1);
        
        // Set parent meal for items (required for database operations)
        broccoliItem.setParentMeal(meal);
        milkItem.setParentMeal(meal);
        oatsItem.setParentMeal(meal);
        
        meals.add(meal);
        return meals;
    }
} 