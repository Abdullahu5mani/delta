package meals.ui;

import meals.models.meal.Meal;

/**
 * Simple state manager for meal selection.
 * This avoids using static fields for state sharing.
 */
public class MealStateManager {
    private static MealStateManager instance;
    private Meal selectedMeal;
    
    private MealStateManager() {}
    
    public static MealStateManager getInstance() {
        if (instance == null) {
            instance = new MealStateManager();
        }
        return instance;
    }
    
    public void setSelectedMeal(Meal meal) {
        this.selectedMeal = meal;
    }
    
    public Meal getSelectedMeal() {
        return selectedMeal;
    }
    
    public void clearSelectedMeal() {
        this.selectedMeal = null;
    }
} 