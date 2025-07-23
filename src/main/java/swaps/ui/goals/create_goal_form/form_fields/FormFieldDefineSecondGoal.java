package swaps.ui.goals.create_goal_form.form_fields;

import javax.swing.*;
import java.util.function.Consumer;

/**
 * Represents a checkbox where the user can select whether they want to specify a second goal.
 */
public class FormFieldDefineSecondGoal extends JPanel {
    private JCheckBox oneOrTwoGoalsDropdown;

    public FormFieldDefineSecondGoal() {
        this.add(new JLabel("Specify a second goal?"));
        oneOrTwoGoalsDropdown = new JCheckBox();
        this.add(oneOrTwoGoalsDropdown);
    }

    /**
     * Sets whether the checkbox is selected/de-selected.
     * @param isSelected Whether checkbox is selected/de-selected.
     */
    public void setOneOrTwoGoalsCheckbox(boolean isSelected) {
        oneOrTwoGoalsDropdown.setSelected(isSelected);
    }

    /**
     * Adds an action to take when the user interacts with the select one or two goals checkbox.
     * @param listener An action to perform when the user clicks the checkbox.
     */
    public void addOneOrTwoGoalsCheckboxListener(Consumer<Boolean> listener) {
        oneOrTwoGoalsDropdown.addActionListener(e -> {
            listener.accept(oneOrTwoGoalsDropdown.isSelected());
        });
    }
}
