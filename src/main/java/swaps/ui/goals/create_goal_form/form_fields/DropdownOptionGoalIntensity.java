package swaps.ui.goals.create_goal_form.form_fields;

/**
 * An enum representing dropdown options for the possible intensities of a goal.
 */
public enum DropdownOptionGoalIntensity {
    HIGH("A lot"),
    MEDIUM("A moderate amount"),
    LOW("A little");

    private final String label;

    /**
     * @param label The String used to represent the goal type in the dropdown.
     */
    DropdownOptionGoalIntensity(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
