package swaps.ui.goals.create_goal_form.form_fields;

/**
 * An enum representing dropdown options for the possible types of goals.
 */
public enum DropdownOptionGoalType {
    PRECISE("Precise"),
    IMPRECISE("Imprecise");

    private final String label;

    /**
     * A visual representation of the goal type to render in the dropdown.
     * @param label The String used to represent the goal type in the dropdown.
     */
    DropdownOptionGoalType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
