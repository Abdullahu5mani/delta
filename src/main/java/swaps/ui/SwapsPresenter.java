package swaps.ui;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import swaps.models.Goal;
import swaps.models.SwapWithMealContext;
import swaps.services.GoalValidationService;
import swaps.services.SwapGenerationService;
import swaps.ui.goals.CreateGoalsPresenter;
import swaps.ui.goals.CreateGoalsView;
import swaps.ui.goals.create_goal_form.GoalsFormPresenter;
import swaps.ui.goals.create_goal_form.form_fields.DropdownOptionGoalDirection;
import swaps.ui.goals.create_goal_form.form_fields.DropdownOptionGoalIntensity;
import swaps.ui.goals.create_goal_form.form_fields.DropdownOptionGoalType;
import swaps.ui.select_swap.SelectSwapPresenter;
import swaps.ui.select_swap.SelectSwapView;
import swaps.ui.swap_meal_details.SwapMealDetailsPresenter;

/**
 * The presenter that handles the logic and manages state for navigating between different steps in the swap workflow.
 */
public class SwapsPresenter {
    protected static final String DEFINE_GOALS_CARD_ID = "DEFINE_GOALS";
    protected static final String SELECT_SWAPS_CARD_ID = "SELECT_SWAPS";
    protected static final String SWAP_STATISTICS_CARD_ID = "SWAP_STATISTICS";
    protected static final String SWAP_MEAL_DETAILS_CARD_ID = "SWAP_MEAL_DETAILS";
    protected static final String[] CARD_IDS = {
            DEFINE_GOALS_CARD_ID,
            SELECT_SWAPS_CARD_ID,
            SWAP_MEAL_DETAILS_CARD_ID,
            SWAP_STATISTICS_CARD_ID
    };

    private SelectSwapPresenter selectSwapPresenter;
    private CreateGoalsPresenter createGoalsPresenter;
    private SwapMealDetailsPresenter swapMealDetailsPresenter;
    private GoalsFormPresenter goal1Presenter;
    private GoalsFormPresenter goal2Presenter;

    private final SwapsView view;
    private int currentCardIndex;
    private final int lastCardIndex;

    /**
     * @param view The view to manage through the presenter.
     */
    public SwapsPresenter(SwapsView view) {
        this.view = view;
        currentCardIndex = 0;
        lastCardIndex = Array.getLength(CARD_IDS) - 1;
        initDefineGoalsView();
        initSelectSwapView();
        initSwapMealDetailsView();
        addPreviousButtonActionListener();
        view.setPreviousButtonEnabled(false);
        addNextButtonActionListener();
        view.setNextButtonEnabled(true);
    }

    /**
     * Initializes the views/presenters for defining goals, which is the first step in the swaps process.
     * Helper method to be called in the constructor.
     */
    private void initDefineGoalsView() {
        CreateGoalsView createGoalsView = view.getDefineGoalsView();
        goal1Presenter = new GoalsFormPresenter(createGoalsView.getGoal1View());
        goal2Presenter = new GoalsFormPresenter(createGoalsView.getGoal2View());
        createGoalsPresenter = new CreateGoalsPresenter(createGoalsView, goal1Presenter, goal2Presenter);
    }

    /**
     * Initializes the select swap view. Helper method to be called in the constructor.
     */
    private void initSelectSwapView() {
        SelectSwapView selectSwapView = view.getSelectSwapView();
        // Initialize with empty list - will be populated when user navigates to this step
        selectSwapPresenter = new SelectSwapPresenter(selectSwapView, new ArrayList<>());
    }

    /**
     * Initializes the swap meal details view. Helper method to be called in the constructor.
     */
    private void initSwapMealDetailsView() {
        swapMealDetailsPresenter = new SwapMealDetailsPresenter(view.getSwapMealDetailsView());
    }

    /**
     * Defines what action is taken when the previous button is clicked.
     * Helper method to be called in the constructor.
     */
    private void addPreviousButtonActionListener() {
        view.addPreviousButtonActionListener(() -> {
            if (currentCardIndex == 0) return;
            currentCardIndex--;
            updateVisibleCardAndNavigationControls();
        });
    }

    /**
     * Defines what action is taken when the next button is clicked.
     * Helper method to be called in the constructor.
     */
    private void addNextButtonActionListener() {
        view.addNextButtonActionListener(() -> {
            if (currentCardIndex == lastCardIndex) return;
            
            // Validate goals before moving from goals to select swap
            if (currentCardIndex == 0 && CARD_IDS[1].equals(SELECT_SWAPS_CARD_ID)) {
                if (!validateGoalsBeforeProceeding()) {
                    return; // Don't proceed if validation fails
                }
                generateAndLoadSwaps();
            }
            
            // Validate swap selection before moving from select swap to meal details
            if (currentCardIndex == 1 && CARD_IDS[2].equals(SWAP_MEAL_DETAILS_CARD_ID)) {
                if (!validateSwapSelectionBeforeProceeding()) {
                    return; // Don't proceed if no swap is selected
                }
                prepareMealDetailsView();
            }
            
            // Update statistics view when moving to statistics card
            if (currentCardIndex == 2 && CARD_IDS[3].equals(SWAP_STATISTICS_CARD_ID)) {
                prepareStatisticsView();
            }
            
            currentCardIndex++;
            updateVisibleCardAndNavigationControls();
        });
    }

    /**
     * Validates goals before proceeding to swap generation.
     * @return true if goals are valid, false if there are conflicts or errors
     */
    private boolean validateGoalsBeforeProceeding() {
        try {
            List<String> allErrors = new ArrayList<>();
            
            // First validate individual goal inputs
            GoalValidationService.GoalValidationResult inputValidation = validateIndividualGoalInputs();
            allErrors.addAll(inputValidation.getErrors());
            
            // Validate date ranges for both goals
            String dateRangeError1 = goal1Presenter.getDateRangeValidationError();
            String dateRangeError2 = goal2Presenter.getDateRangeValidationError();
            
            if (dateRangeError1 != null) {
                allErrors.add("Goal 1 date range: " + dateRangeError1);
            }
            if (dateRangeError2 != null) {
                allErrors.add("Goal 2 date range: " + dateRangeError2);
            }
            
            // Try to extract goals for conflict validation (even if there are input errors)
            try {
                List<Goal> goals = extractValidGoalsOnly();
                
                if (goals.size() >= 2) {
                    GoalValidationService.GoalValidationResult goalValidation = 
                            GoalValidationService.instance().validateGoals(goals);
                    allErrors.addAll(goalValidation.getErrors());
                }
            } catch (Exception e) {
                // Don't add this as an error - input validation errors are more important
            }
            
            if (!allErrors.isEmpty()) {
                String errorMessage = String.join("\n", allErrors);
                showErrorDialog("Validation Error", errorMessage);
                return false;
            }
            
            return true;
        } catch (Exception e) {
            showErrorDialog("Validation Error", "Error validating goals: " + e.getMessage());
            return false;
        }
    }

    /**
     * Extracts only the goals that can be successfully created (ignoring invalid inputs).
     * This is used for conflict detection even when there are input validation errors.
     */
    private List<Goal> extractValidGoalsOnly() {
        List<Goal> goals = new ArrayList<>();
        
        // Try to extract Goal 1
        try {
            Goal goal1 = extractGoalFromPresenterIfValid(goal1Presenter);
            if (goal1 != null) {
                goals.add(goal1);
            }
        } catch (Exception e) {
        }
        
        // Try to extract Goal 2 if enabled
        if (createGoalsPresenter.isSecondGoalEnabled()) {
            try {
                Goal goal2 = extractGoalFromPresenterIfValid(goal2Presenter);
                if (goal2 != null) {
                    goals.add(goal2);
                }
            } catch (Exception e) {
            }
        }
        
        return goals;
    }

    /**
     * Extracts a goal from presenter only if the basic requirements are met (nutrient selected, direction set).
     */
    private Goal extractGoalFromPresenterIfValid(GoalsFormPresenter presenter) {
        var selectedNutrientOption = presenter.getSelectedNutrient();
        if (selectedNutrientOption == null) {
            return null; // Can't create goal without nutrient
        }
        
        var selectedNutrient = selectedNutrientOption.nutrient();
        DropdownOptionGoalDirection direction = presenter.getDirection();
        DropdownOptionGoalType type = presenter.getType();
        
        if (type == DropdownOptionGoalType.PRECISE) {
            Float amount = presenter.getPreciseAmount();
            if (amount == null || amount <= 0) {
                amount = 1.0f; // Dummy value for conflict detection
            }
            return new Goal(selectedNutrient, direction, amount);
        } else {
            DropdownOptionGoalIntensity intensity = presenter.getIntensity();
            if (intensity == null) {
                // Use a dummy intensity for conflict detection
                intensity = DropdownOptionGoalIntensity.LOW;
            }
            return new Goal(selectedNutrient, direction, intensity);
        }
    }

    /**
     * Validates individual goal form inputs using the validation service.
     */
    private GoalValidationService.GoalValidationResult validateIndividualGoalInputs() {
        List<String> allErrors = new ArrayList<>();
        
        // Validate Goal 1
        GoalValidationService.GoalValidationResult goal1Validation = validateSingleGoalInput(goal1Presenter, "Goal 1");
        allErrors.addAll(goal1Validation.getErrors());
        
        // Validate Goal 2 if second goal is enabled
        if (createGoalsPresenter.isSecondGoalEnabled()) {
            GoalValidationService.GoalValidationResult goal2Validation = validateSingleGoalInput(goal2Presenter, "Goal 2");
            allErrors.addAll(goal2Validation.getErrors());
        }
        
        return new GoalValidationService.GoalValidationResult(allErrors);
    }

    /**
     * Validates a single goal's input fields using the validation service.
     */
    private GoalValidationService.GoalValidationResult validateSingleGoalInput(GoalsFormPresenter presenter, String goalName) {
        boolean nutrientSelected = presenter.getSelectedNutrient() != null;
        DropdownOptionGoalType goalType = presenter.getType();
        Float preciseAmount = presenter.getPreciseAmount();
        boolean intensitySelected = presenter.getIntensity() != null;
        
        return GoalValidationService.instance().validateGoalInputs(
                nutrientSelected, goalType, preciseAmount, intensitySelected, goalName);
    }

    /**
     * Validates that a swap has been selected before proceeding to meal details.
     * @return true if a swap is selected, false otherwise
     */
    private boolean validateSwapSelectionBeforeProceeding() {
        SwapWithMealContext selectedSwap = selectSwapPresenter.getSelectedSwap();
        if (selectedSwap == null) {
            showErrorDialog("No Swap Selected", "Please select a food swap from the list before proceeding.");
            return false;
        }
        return true;
    }

    /**
     * Prepares the meal details view with the selected swap.
     * Called when navigating to the meal details card.
     */
    private void prepareMealDetailsView() {
        SwapWithMealContext selectedSwap = selectSwapPresenter.getSelectedSwap();
        if (selectedSwap != null) {
            swapMealDetailsPresenter.setSwap(selectedSwap);
        }
    }
    
    /**
     * Prepares the statistics view with the selected swap.
     * Called when navigating to the statistics card.
     */
    private void prepareStatisticsView() {
        SwapWithMealContext selectedSwap = selectSwapPresenter.getSelectedSwap();
        
        // Update the individual food comparison (existing functionality)
        view.getSwapStatisticsView().updateSwapComparison(selectedSwap);
        
        // NEW: Update the meal list comparison
        updateMealListComparison(selectedSwap);
    }
    
    /**
     * NEW: Updates the meal list comparison by fetching meals from the selected date range
     * and simulating the swap to create before/after meal lists.
     */
    private void updateMealListComparison(SwapWithMealContext selectedSwap) {
        if (selectedSwap == null) {
            return;
        }
        
        try {
            // Get the date range from the goals form
            Date fromDate = goal1Presenter.getFromDate();
            Date toDate = goal1Presenter.getToDate();
            
            if (fromDate == null || toDate == null) {
                System.out.println("⚠️  No valid date range selected for meal list comparison");
                return;
            }
            
            System.out.println("📅 Fetching meals for date range: " + fromDate + " to " + toDate);
            
            // Import required service
            meals.services.QueryMealsService.QueryMealsServiceOutput mealsResult = 
                meals.services.QueryMealsService.instance().getMealsByDate(fromDate, toDate);
            
            if (!mealsResult.ok()) {
                System.err.println("❌ Failed to fetch meals: " + mealsResult.errors());
                return;
            }
            
            List<meals.models.meal.Meal> originalMeals = mealsResult.getMeals();
            System.out.println("✅ Found " + originalMeals.size() + " meals in date range");
            
            if (originalMeals.isEmpty()) {
                System.out.println("⚠️  No meals found in the selected date range");
                return;
            }
            
            // NEW: Extract goal nutrients from the goals forms
            List<String> goalNutrientNames = extractGoalNutrientNames();
            
            // Create "after swap" meals by simulating the swap
            List<meals.models.meal.Meal> afterSwapMeals = simulateSwapInMeals(originalMeals, selectedSwap);
            
            // Update the meal list comparison view with goal nutrients prioritized
            // Delegate UI creation to the view layer (proper MVP separation)
            view.getSwapStatisticsView().updateMealListComparisonWithGoals(originalMeals, afterSwapMeals, goalNutrientNames, selectedSwap);
            
        } catch (Exception e) {
            System.err.println("❌ Error updating meal list comparison: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * NEW: Extracts the nutrient names from user's goals (goal1 and goal2).
     * @return List of nutrient names from the goals (1-2 items)
     */
    private List<String> extractGoalNutrientNames() {
        List<String> goalNutrients = new ArrayList<>();
        
        try {
            System.out.println("🔍 Starting goal nutrient extraction...");
            
            // Get nutrient from goal 1
            System.out.println("🔍 Checking Goal 1...");
            swaps.ui.goals.create_goal_form.form_fields.DropdownOptionNutrient goal1Nutrient = goal1Presenter.getSelectedNutrient();
            if (goal1Nutrient != null) {
                if (goal1Nutrient.nutrient() != null) {
                    String nutrientName = goal1Nutrient.nutrient().getNutrientName();
                    goalNutrients.add(nutrientName);
                    System.out.println("🎯 Goal 1 nutrient: " + nutrientName);
                } else {
                    System.out.println("⚠️  Goal 1 nutrient object is null");
                }
            } else {
                System.out.println("⚠️  Goal 1 DropdownOptionNutrient is null");
            }
            
            // Get nutrient from goal 2 (if it exists and is different)
            System.out.println("🔍 Checking Goal 2...");
            if (createGoalsPresenter.isSecondGoalEnabled()) {
                swaps.ui.goals.create_goal_form.form_fields.DropdownOptionNutrient goal2Nutrient = goal2Presenter.getSelectedNutrient();
                if (goal2Nutrient != null) {
                    if (goal2Nutrient.nutrient() != null) {
                        String nutrientName = goal2Nutrient.nutrient().getNutrientName();
                        System.out.println("🔍 Goal 2 nutrient found: " + nutrientName);
                        if (!goalNutrients.contains(nutrientName)) { // Avoid duplicates
                            goalNutrients.add(nutrientName);
                            System.out.println("🎯 Goal 2 nutrient added: " + nutrientName);
                        } else {
                            System.out.println("⚠️  Goal 2 nutrient '" + nutrientName + "' is same as Goal 1 - skipping duplicate");
                        }
                    } else {
                        System.out.println("⚠️  Goal 2 nutrient object is null");
                    }
                } else {
                    System.out.println("⚠️  Goal 2 DropdownOptionNutrient is null - user might not have set a second goal");
                }
            } else {
                System.out.println("⚠️  Goal 2 is not enabled - skipping");
            }
            
        } catch (Exception e) {
            System.err.println("⚠️  Error extracting goal nutrients: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("✅ Final extracted " + goalNutrients.size() + " goal nutrients: " + goalNutrients);
        return goalNutrients;
    }
    
    /**
     * NEW: Simulates applying a swap to a list of meals, creating new meals with the swapped food.
     * @param originalMeals The original meals before any swaps
     * @param swap The swap to apply (oldFood -> newFood)
     * @return A new list of meals with the swap applied
     */
    private List<meals.models.meal.Meal> simulateSwapInMeals(List<meals.models.meal.Meal> originalMeals, SwapWithMealContext swap) {
        List<meals.models.meal.Meal> afterSwapMeals = new ArrayList<>();
        
        for (meals.models.meal.Meal originalMeal : originalMeals) {
            // Create a new meal with potentially swapped items
            List<meals.models.meal.MealItem> swappedItems = new ArrayList<>();
            boolean swapApplied = false;
            
            for (meals.models.meal.MealItem originalItem : originalMeal.getMealItems()) {
                // Check if this item contains the food we want to swap
                if (originalItem.getFood().getFoodId().equals(swap.oldFood().getFoodId())) {
                    // Apply the swap: replace with new food, keep same quantity and measure
                    meals.models.meal.MealItem swappedItem = new meals.models.meal.MealItem(
                        originalItem.getId(),
                        swap.newFood(),
                        originalItem.getQuantity(),
                        originalItem.getSelectedMeasure()
                    );
                    swappedItems.add(swappedItem);
                    swapApplied = true;
                    System.out.println("🔄 Swapped: " + swap.oldFood().getFoodDescription() + 
                                     " → " + swap.newFood().getFoodDescription());
                } else {
                    // Keep the original item unchanged
                    swappedItems.add(originalItem);
                }
            }
            
            // Create new meal with swapped items
            meals.models.meal.Meal swappedMeal = new meals.models.meal.Meal(
                originalMeal.getId(),
                originalMeal.getMealType(),
                swappedItems,
                originalMeal.getCreatedAt(),
                originalMeal.getUserId()
            );
            
            afterSwapMeals.add(swappedMeal);
            
            if (swapApplied) {
                System.out.println("✅ Swap applied to meal: " + originalMeal.getMealType() + 
                                 " on " + originalMeal.getCreatedAt());
            }
        }
        
        return afterSwapMeals;
    }

    /**
     * Handles logic for updating the UI when navigating to a new card.
     */
    private void updateVisibleCardAndNavigationControls() {
        view.setPreviousButtonEnabled(currentCardIndex > 0);
        view.setNextButtonEnabled(currentCardIndex < lastCardIndex);
        view.showCard(CARD_IDS[currentCardIndex]);
    }

    /**
     * Generates swaps based on current goals and loads them into the select swap view.
     */
    private void generateAndLoadSwaps() {
        try {
            List<Goal> goals = extractGoalsFromForm();
            
            // Use the selected date range from the first goal's form
            Date fromDate = goal1Presenter.getFromDate();
            Date toDate = goal1Presenter.getToDate();
            
            if (fromDate == null || toDate == null) {
                // Fallback to today if no date range selected
                fromDate = toDate = new Date();
            }
            
            SwapGenerationService.SwapWithMealContextResult result;
            if (fromDate.equals(toDate)) {
                // Single date generation with meal context
                result = SwapGenerationService.instance().generateSwapsWithMealContextForDate(goals, fromDate);
            } else {
                // Date range generation with meal context
                result = SwapGenerationService.instance().generateSwapsWithMealContextForDateRange(goals, fromDate, toDate);
            }
            
            if (result.hasErrors()) {
                // Show error dialog
                String errorMessage = String.join("\n", result.getErrors());
                showErrorDialog("Swap Generation Error", errorMessage);
                return;
            }
            
            if (result.isSuccessful()) {
                // Update the select swap view with generated swaps
                selectSwapPresenter.updateSwaps(result.getSwaps());
            } else {
                showErrorDialog("No Swaps Found", "No suitable food swaps could be generated for your logged meals in the selected date range.");
            }
            
        } catch (Exception e) {
            showErrorDialog("Unexpected Error", "An error occurred while generating swaps: " + e.getMessage());
        }
    }

    /**
     * Extracts goals from the form presenters.
     */
    private List<Goal> extractGoalsFromForm() {
        List<Goal> goals = new ArrayList<>();
        
        // Extract Goal 1 (always present)
        Goal goal1 = extractGoalFromPresenter(goal1Presenter);
        if (goal1 != null) {
            goals.add(goal1);
        }
        
        // Extract Goal 2 (if visible/enabled)
        if (createGoalsPresenter.isSecondGoalEnabled()) {
            Goal goal2 = extractGoalFromPresenter(goal2Presenter);
            if (goal2 != null) {
                goals.add(goal2);
            }
        }
        
        return goals;
    }

    /**
     * Extracts a goal from a single goal form presenter.
     */
    private Goal extractGoalFromPresenter(GoalsFormPresenter presenter) {
        try {
            // Get the selected nutrient from the actual form
            var selectedNutrientOption = presenter.getSelectedNutrient();
            if (selectedNutrientOption == null) {
                return null;
            }
            
            var selectedNutrient = selectedNutrientOption.nutrient();
            
            DropdownOptionGoalDirection direction = presenter.getDirection();
            DropdownOptionGoalType type = presenter.getType();
            
            if (type == DropdownOptionGoalType.PRECISE) {
                Float amount = presenter.getPreciseAmount();
                if (amount != null && amount > 0) {
                    return new Goal(selectedNutrient, direction, amount);
                } else {
                    return null;
                }
            } else {
                DropdownOptionGoalIntensity intensity = presenter.getIntensity();
                if (intensity != null) {
                    return new Goal(selectedNutrient, direction, intensity);
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Shows an error dialog to the user.
     */
    private void showErrorDialog(String title, String message) {
        javax.swing.JOptionPane.showMessageDialog(view, message, title, javax.swing.JOptionPane.ERROR_MESSAGE);
    }
}
