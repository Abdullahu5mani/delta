package swaps.models;

import meals.models.nutrient.Nutrient;
import swaps.ui.goals.create_goal_form.form_fields.DropdownOptionGoalDirection;
import swaps.ui.goals.create_goal_form.form_fields.DropdownOptionGoalIntensity;
import swaps.ui.goals.create_goal_form.form_fields.DropdownOptionGoalType;

/**
 * Represents a nutritional goal for generating food swaps.
 */
public class Goal {
    private final Nutrient nutrient;
    private final DropdownOptionGoalType type;
    private final DropdownOptionGoalDirection direction;
    private final DropdownOptionGoalIntensity intensity;
    private final Float preciseAmount;

    /**
     * Constructor for precise goals with a specific amount.
     */
    public Goal(Nutrient nutrient, DropdownOptionGoalDirection direction, Float preciseAmount) {
        this.nutrient = nutrient;
        this.type = DropdownOptionGoalType.PRECISE;
        this.direction = direction;
        this.preciseAmount = preciseAmount;
        this.intensity = null; // Not used for precise goals
        
        if (preciseAmount == null || preciseAmount <= 0) {
            throw new IllegalArgumentException("Precise amount must be positive");
        }
    }

    /**
     * Constructor for imprecise goals with intensity.
     */
    public Goal(Nutrient nutrient, DropdownOptionGoalDirection direction, DropdownOptionGoalIntensity intensity) {
        this.nutrient = nutrient;
        this.type = DropdownOptionGoalType.IMPRECISE;
        this.direction = direction;
        this.intensity = intensity;
        this.preciseAmount = null; // Not used for imprecise goals
    }

    // Getters
    public Nutrient getNutrient() {
        return nutrient;
    }

    public DropdownOptionGoalType getType() {
        return type;
    }

    public DropdownOptionGoalDirection getDirection() {
        return direction;
    }

    public DropdownOptionGoalIntensity getIntensity() {
        return intensity;
    }

    public Float getPreciseAmount() {
        return preciseAmount;
    }

    /**
     * Validates that the goal is well-formed.
     */
    public boolean isValid() {
        if (nutrient == null || direction == null) {
            return false;
        }
        
        if (type == DropdownOptionGoalType.PRECISE) {
            return preciseAmount != null && preciseAmount > 0;
        } else {
            return intensity != null;
        }
    }

    /**
     * Gets the target amount for this goal based on type and intensity.
     * For precise goals, returns the exact amount.
     * For imprecise goals, returns a multiplier based on intensity.
     */
    public float getTargetAmount() {
        if (type == DropdownOptionGoalType.PRECISE) {
            return preciseAmount;
        } else {
            // Return multiplier based on intensity
            return switch (intensity) {
                case HIGH -> 3.0f;
                case MEDIUM -> 2.0f;
                case LOW -> 1.5f;
            };
        }
    }

    @Override
    public String toString() {
        String directionStr = direction == DropdownOptionGoalDirection.INCREASE ? "Increase" : "Decrease";
        String nutrientName = nutrient.getNutrientName();
        
        if (type == DropdownOptionGoalType.PRECISE) {
            return String.format("%s %s by %.1f %s", directionStr, nutrientName, preciseAmount, nutrient.getNutrientUnit());
        } else {
            return String.format("%s %s (%s)", directionStr, nutrientName, intensity.toString());
        }
    }
}
