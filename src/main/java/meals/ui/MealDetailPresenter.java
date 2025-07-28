package meals.ui;

import meals.models.meal.Meal;
import app.LeftNavItem;
import app.AppMainPresenter;

public class MealDetailPresenter {
    private final MealDetailView view;
    
    public MealDetailPresenter(MealDetailView view) {
        this.view = view;
        
        // Register back button listener
        view.registerBackButtonListener(this::navigateBack);
    }
    
    public void displayMeal(Meal meal) {
        view.displayMeal(meal);
    }
    
    private void navigateBack() {
        AppMainPresenter.instance().navigateTo(LeftNavItem.VIEW_MULTIPLE_MEALS);
    }
} 