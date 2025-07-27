package swaps.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import meals.models.nutrient.Nutrient;
import swaps.models.Goal;
import swaps.ui.goals.create_goal_form.form_fields.DropdownOptionGoalDirection;
import swaps.ui.goals.create_goal_form.form_fields.DropdownOptionGoalType;

/**
 * Service for validating goals according to business rules.
 * This service handles all validation logic for goals including input validation and conflict detection.
 */
public class GoalValidationService {
    private static GoalValidationService instance;
    
    private GoalValidationService() {}
    
    public static GoalValidationService instance() {
        if (instance == null) {
            instance = new GoalValidationService();
        }
        return instance;
    }

    /**
     * Validates a list of goals for conflicts and business rule violations.
     * 
     * @param goals List of goals to validate
     * @return GoalValidationResult containing any validation errors
     */
    public GoalValidationResult validateGoals(List<Goal> goals) {
        List<String> errors = new ArrayList<>();
        
        if (goals == null || goals.isEmpty()) {
            errors.add("At least one goal must be specified");
            return new GoalValidationResult(errors);
        }
        
        // Check for invalid goals
        for (Goal goal : goals) {
            if (!goal.isValid()) {
                errors.add("Invalid goal configuration");
            }
        }
        
        // Check for conflicting goals (same nutrient with opposite directions)
        errors.addAll(validateGoalConflicts(goals));
        
        return new GoalValidationResult(errors);
    }

    /**
     * Validates individual goal input parameters before creating Goal objects.
     * 
     * @param nutrientSelected Whether a nutrient has been selected
     * @param goalType The type of goal (precise/imprecise)
     * @param preciseAmount The precise amount (if applicable)
     * @param intensitySelected Whether intensity has been selected for imprecise goals
     * @param goalName Name of the goal for error messages (e.g., "Goal 1")
     * @return GoalValidationResult containing any validation errors
     */
    public GoalValidationResult validateGoalInputs(
            boolean nutrientSelected,
            DropdownOptionGoalType goalType,
            Float preciseAmount,
            boolean intensitySelected,
            String goalName) {
        
        List<String> errors = new ArrayList<>();
        
        // Check if nutrient is selected
        if (!nutrientSelected) {
            errors.add(goalName + ": Please select a nutrient");
            return new GoalValidationResult(errors); // Can't validate further without nutrient
        }
        
        // Check goal type specific validations
        if (goalType == DropdownOptionGoalType.PRECISE) {
            if (preciseAmount == null) {
                errors.add(goalName + ": Please enter a precise amount");
            } else if (preciseAmount <= 0) {
                errors.add(goalName + ": Amount must be greater than 0");
            } else if (preciseAmount > 100) {
                errors.add(goalName + ": Amount cannot exceed 100%");
            }
        } else {
            // Imprecise goal
            if (!intensitySelected) {
                errors.add(goalName + ": Please select an intensity level");
            }
        }
        
        return new GoalValidationResult(errors);
    }

    /**
     * Validates for conflicting goals (same nutrient with opposite directions).
     */
    private List<String> validateGoalConflicts(List<Goal> goals) {
        List<String> errors = new ArrayList<>();
        
        // Group goals by nutrient
        Map<Nutrient, List<Goal>> goalsByNutrient = goals.stream()
                .collect(Collectors.groupingBy(Goal::getNutrient));
        
        for (Map.Entry<Nutrient, List<Goal>> entry : goalsByNutrient.entrySet()) {
            List<Goal> nutrientGoals = entry.getValue();
            if (nutrientGoals.size() > 1) {
                boolean hasIncrease = nutrientGoals.stream()
                        .anyMatch(g -> g.getDirection() == DropdownOptionGoalDirection.INCREASE);
                boolean hasDecrease = nutrientGoals.stream()
                        .anyMatch(g -> g.getDirection() == DropdownOptionGoalDirection.DECREASE);
                
                if (hasIncrease && hasDecrease) {
                    errors.add("Conflicting goals for " + entry.getKey().getNutrientName() + 
                              ": cannot both increase and decrease the same nutrient");
                }
            }
        }
        
        return errors;
    }

    /**
     * Result class for goal validation operations.
     */
    public static class GoalValidationResult {
        private final List<String> errors;
        
        public GoalValidationResult(List<String> errors) {
            this.errors = errors;
        }
        
        public List<String> getErrors() {
            return errors;
        }
        
        public boolean hasErrors() {
            return !errors.isEmpty();
        }
        
        public boolean isValid() {
            return errors.isEmpty();
        }
        
        public String getErrorMessage() {
            return String.join("\n", errors);
        }
    }
}
