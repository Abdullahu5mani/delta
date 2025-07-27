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
     * Prepares the meal details view with the selected swap information.
     */
    private void prepareMealDetailsView() {
        SwapWithMealContext selectedSwap = selectSwapPresenter.getSelectedSwap();
        
        // Initialize the meal details view with the selected swap
        swapMealDetailsPresenter.setSwap(selectedSwap);
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
