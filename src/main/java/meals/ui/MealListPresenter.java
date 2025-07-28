package meals.ui;

import meals.models.meal.Meal;
import meals.services.QueryMealsService;
import app.LeftNavItem;
import app.AppMainPresenter;

import java.util.Date;

public class MealListPresenter {
    private final MealListView view;
    private final QueryMealsService queryMealsService;

    public MealListPresenter(MealListView view) {
        this.view = view;
        this.queryMealsService = QueryMealsService.instance();
        
        view.registerLoadMealsListener(this::loadMeals);
        view.registerMealSelectionListener(this::onMealSelected);
        
        loadMeals();
    }

    private void loadMeals() {
        view.showLoading();
        
        try {
            Date selectedDate = view.getSelectedDate();
            System.out.println("📅 === LOADING MEALS ===");
            System.out.println("🔍 Querying meals for date: " + selectedDate);
            
            QueryMealsService.QueryMealsServiceOutput result = 
                queryMealsService.getMealsByDate(selectedDate, selectedDate);
            
            if (result.ok()) {
                System.out.println("✅ Found " + result.getMeals().size() + " meal(s):");
                for (int i = 0; i < result.getMeals().size(); i++) {
                    var meal = result.getMeals().get(i);
                    System.out.println("   " + (i+1) + ". " + meal.getMealType() + " (ID: " + meal.getId() + ") - " + meal.getMealItems().size() + " items");
                }
                view.updateMealsList(result.getMeals());
            } else {
                System.out.println("❌ Query failed with errors:");
                result.errors().forEach(error -> System.out.println("   - " + error));
                String errorMessages = result.errors().stream()
                    .map(Object::toString)
                    .reduce("", (a, b) -> a + (a.isEmpty() ? "" : "; ") + b);
                view.showError(errorMessages);
            }
            
        } catch (Exception e) {
            System.out.println("❌ Exception while loading meals: " + e.getMessage());
            System.out.println("🔍 Exception details:");
            e.printStackTrace();
            view.showError("Failed to load meals: " + e.getMessage());
        }
        System.out.println("📅 === MEAL LOADING COMPLETE ===\n");
    }

    private void onMealSelected() {
        Meal selectedMeal = view.getSelectedMeal();
        if (selectedMeal != null) {
            System.out.println("👆 === MEAL SELECTED ===");
            System.out.println("🍽️ Meal: " + selectedMeal.getMealType() + " (ID: " + selectedMeal.getId() + ")");
            System.out.println("📅 Date: " + selectedMeal.getCreatedAt());
            System.out.println("🥘 Items: " + selectedMeal.getMealItems().size());
            for (int i = 0; i < selectedMeal.getMealItems().size(); i++) {
                var item = selectedMeal.getMealItems().get(i);
                System.out.println("   " + (i+1) + ". " + item.getFood().getFoodDescription() + 
                                 " (" + item.getQuantity() + " " + item.getSelectedMeasure().getName() + ")");
            }
            System.out.println("🧭 Navigating to detailed meal view...");
            System.out.println("👆 === SELECTION COMPLETE ===\n");
            
            // Store the selected meal for the detail view
            MealStateManager.getInstance().setSelectedMeal(selectedMeal);
            AppMainPresenter.instance().navigateTo(LeftNavItem.VIEW_SINGLE_MEAL);
        }
    }
} 