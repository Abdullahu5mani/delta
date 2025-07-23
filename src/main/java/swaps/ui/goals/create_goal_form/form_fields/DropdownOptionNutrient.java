package swaps.ui.goals.create_goal_form.form_fields;

import meals.models.nutrient.Nutrient;

/**
 * Represents a dropdown option to select a nutrient.
 */
public record DropdownOptionNutrient(Nutrient nutrient) {
    @Override
    public String toString() {
        return nutrient.getNutrientName();
    }
}
