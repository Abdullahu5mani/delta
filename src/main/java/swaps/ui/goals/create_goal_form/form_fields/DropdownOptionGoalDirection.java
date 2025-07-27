package swaps.ui.goals.create_goal_form.form_fields;

/**
 * An enum representing dropdown options for goal direction (increase or decrease).
 */
public enum DropdownOptionGoalDirection {
    INCREASE("Increase"),
    DECREASE("Decrease");

    private final String label;

    /**
     * @param label The String used to represent the goal direction in the dropdown.
     */
    DropdownOptionGoalDirection(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
