package meals.ui;


import app.AppMainPresenter;
import app.LeftNavItem;
import meals.models.food.Food;
import meals.models.meal.Meal;
import meals.models.meal.MealItem;
import meals.services.CreateMealService;
import shared.navigation.NavigationPresenter;

import java.util.*;

/**
 * Controls the meal logging view.
 * Handles front-end logic and making calls to the backend for creating a meal given the information entered via the UI.
 */
public class LogMealPresenter {
    private static final String INVALID_QUANTITY_MESSAGE = "Invalid quantity. Value must be a number > 0.";
    private static final String DUPLICATE_FOOD_MESSAGE = "Foods for a meal must be unique.";

    private final LogMealView view;

    /**
     * @param view The view to control.
     */
    public LogMealPresenter(LogMealView view) {
        this.view = view;
        view.registerAddFoodListener(this::addFoodListener);
        view.registerRemoveItemListener(this::removeItemListener);
        view.getFoodSearchBox().addSelectionChangedListener(option -> {
            Food food = option.food();
            view.setAvailableMeasures(food.getPossibleMeasures());
        });
        // set initial options for measures
        FoodSearchBoxOption initial = view.getSelectedFood();
        if (initial != null) {
            view.setAvailableMeasures(initial.food().getPossibleMeasures());
        }
        view.registerCreateMealButtonListener(this::createMeal);
    }

    /**
     * Handles the logic for creating a meal given the information entered via the UI using the backend service.
     */
    private void createMeal() {
        System.out.println("🍽️ === STARTING MEAL CREATION ===");
        
        List<MealItem> mealItems = buildMealItemsFromForm();
        // only support logging meals for the current day
        int randomId = new Random().nextInt(10_000);
        Meal.MealType mealType = view.getSelectedMealType();
        Date today = new Date();
        Meal meal = new Meal(randomId, mealType, mealItems, today);
        
        System.out.println("📋 Meal Details:");
        System.out.println("   🆔 ID: " + randomId);
        System.out.println("   🍳 Type: " + mealType);
        System.out.println("   📅 Date: " + today);
        System.out.println("   🥘 Items: " + mealItems.size());
        for (int i = 0; i < mealItems.size(); i++) {
            MealItem item = mealItems.get(i);
            System.out.println("      " + (i+1) + ". " + item.getFood().getFoodDescription() + 
                             " (" + item.getQuantity() + " " + item.getSelectedMeasure().getName() + ")");
        }
        
        System.out.println("💾 Saving meal to database...");
        CreateMealService.CreateMealServiceOutput result = CreateMealService.instance().createMeal(meal);
        
        if (result.ok()) {
            System.out.println("✅ SUCCESS: Meal created successfully!");
            System.out.println("🧭 Navigating to meal list view...");
            AppMainPresenter.instance().navigateTo(LeftNavItem.VIEW_MULTIPLE_MEALS);
        } else {
            System.out.println("❌ FAILED: Meal creation failed with errors:");
            result.errors().forEach(error -> System.out.println("   - " + error));
            List<String> errors = result.errors()
                    .stream()
                    .map(Object::toString)
                    .toList();
            view.showErrorMessage(String.join("\n", errors));
        }
        System.out.println("🍽️ === MEAL CREATION COMPLETE ===\n");
    }

    /**
     * Builds a list of meal items from the form input.
     */
    private List<MealItem> buildMealItemsFromForm() {
        List<MealItem> mealItems = new ArrayList<>();
        for (SelectedFoodListItem selectedFoodListItem: view.getSelectedItemsAddedToMeal()) {
            mealItems.add(
                    new MealItem(new Random().nextInt(10000),
                            selectedFoodListItem.food(),
                            selectedFoodListItem.quantity(),
                            selectedFoodListItem.measure())
            );
        }
        return mealItems;
    }

    /**
     * Adds the selected food to the meal if the information entered is valid.
     */
    private void addFoodListener() {
        Food selectedFood = view.getSelectedFood().food();
        Float quantity = view.getSelectedQuantity();
        if (quantity == null) {
            view.showErrorMessage(INVALID_QUANTITY_MESSAGE);
            return;
        }
        if (foodAlreadyPartOfMeal(selectedFood)) {
            view.showErrorMessage(DUPLICATE_FOOD_MESSAGE);
            return;
        }
        view.addSelectedFood(new SelectedFoodListItem(selectedFood, quantity, view.getSelectedMeasure()));
    }

    /**
     * Helper function to determine whether the selected food is already part of the meal.
     * @param food The food to check if it is already part of the meal.
     * @return True if the food is already part of the meal and false otherwise.
     */
    private boolean foodAlreadyPartOfMeal(Food food) {
        List<SelectedFoodListItem> currentFoods = view.getSelectedItemsAddedToMeal();
        for (SelectedFoodListItem lisItem: currentFoods) {
            // we can compare IDs, since we know foods are frozen records that are never updated in the database
            if (lisItem.food().getFoodId().equals(food.getFoodId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes the selected food from the meal.
     */
    private void removeItemListener() {
        SelectedFoodListItem selectedFoodListItem = view.getSelectedItemAddedToMeal();
        view.removeItem(selectedFoodListItem);
    }
}
