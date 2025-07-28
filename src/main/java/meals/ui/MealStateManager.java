package meals.ui;

import meals.models.meal.Meal;

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