package meals.models;

import meals.models.food.*;
import meals.models.meal.Meal;
import meals.models.meal.Meal.MealType;
import meals.models.meal.MealItem;
import meals.models.nutrient.Nutrient;

import java.util.*;

public class MockDataFactory {

    public static List<Nutrient> createMockNutrients() {
        return List.of(
                new Nutrient(1, "PRO", "g", "Protein"),
                new Nutrient(2, "CA", "mg", "Calcium"),
                new Nutrient(3, "VC", "mg", "Vitamin C")
        );
    }

    public static Map<Nutrient, Float> createNutrientAmounts() {
        Map<Nutrient, Float> amounts = new HashMap<>();
        for (Nutrient nutrient : createMockNutrients()) {
            amounts.put(nutrient, (float) (Math.random() * 100));
        }
        return amounts;
    }

    public static List<Measure> createMockMeasures(String defaultName) {
        return List.of(
                new Measure(new Random().nextInt(1000), defaultName, 10.0f),
                new Measure(new Random().nextInt(1000), "100 g", 100.0f)
        );
    }

    public static FoodGroup createMockFoodGroup(String name) {
        return new FoodGroup(new Random().nextInt(1000), name);
    }

    public static Food createMockFood(String description, String groupName, String defaultMeasureName) {
        return new Food(
                new Random().nextInt(1000),
                description,
                createMockFoodGroup(groupName),
                createNutrientAmounts(),
                createMockMeasures(defaultMeasureName)
        );
    }

    public static MealItem createMockMealItem(Food food, Measure measure, float quantity) {
        return new MealItem(
                new Random().nextInt(1000),
                food,
                quantity,
                measure
        );
    }

    public static List<Food> generateMockFoods() {
        return List.of(
                createMockFood("Grilled Chicken Breast", "Meat & Poultry", "1 Breast"),
                createMockFood("Steamed Broccoli", "Vegetables", "1 Cup"),
                createMockFood("Brown Rice", "Grains", "1 Bowl"),
                createMockFood("Greek Yogurt", "Dairy Products", "1 Tub"),
                createMockFood("Orange Juice", "Beverages", "1 Glass")
        );
    }

    public static Meal createMockMeal(MealType mealType) {
        List<MealItem> items = new ArrayList<>();
        for (Food food : generateMockFoods()) {
            Measure defaultMeasure = food.getPossibleMeasures().get(0);
            items.add(createMockMealItem(food, defaultMeasure, 1.0f));
        }
        int mealId = new Random().nextInt(10000);
        return new Meal(mealId, mealType, items, new Date());
    }

    public static void main(String[] args) {
        System.out.println("\nGenerating mock foods...\n");
        List<Food> mockFoods = generateMockFoods();
        for (Food mockFood : mockFoods) {
            System.out.printf("ID: %d%n", mockFood.getFoodId());
            System.out.printf("Description: %s%n", mockFood.getFoodDescription());
            System.out.printf("Food Group: %s%n", mockFood.getFoodGroup().getFoodGroupName());
            printFood(mockFood);
            System.out.println();
        }

        System.out.println("\nGenerating mock meal...\n");
        Meal mockMeal = createMockMeal(MealType.LUNCH);
        System.out.printf("Meal ID: %d%n", mockMeal.getId());
        System.out.printf("Meal Type: %s%n%n", mockMeal.getMealType());

        for (MealItem item : mockMeal.getMealItems()) {
            Food food = item.getFood();
            Measure measure = item.getSelectedMeasure();
            System.out.printf("MealItem ID: %d%n", item.getId());
            System.out.printf("Food: %s%n", food.getFoodDescription());
            System.out.printf("Food Group: %s%n", food.getFoodGroup().getFoodGroupName());
            System.out.printf("Quantity: %.2f (%s)%n", item.getQuantity(), measure.getName());
            printFood(food);
            System.out.println();
        }

        System.out.println("Mock meal generated successfully.");
    }

    private static void printFood(Food food) {
        System.out.println("Possible Measures:");
        for (Measure measure : food.getPossibleMeasures()) {
            System.out.printf(" • %s (%.2f)%n", measure.getName(), measure.getConversionValue());
        }
        System.out.println("Nutrient Composition:");
        for (Map.Entry<Nutrient, Float> entry : food.getNutrientAmounts().entrySet()) {
            Nutrient nutrient = entry.getKey();
            Float amount = entry.getValue();
            System.out.printf(" - %s: %.2f %s%n",
                    nutrient.getDisplayNameWithUnit(),
                    amount,
                    nutrient.getNutrientUnit());
        }
    }
}