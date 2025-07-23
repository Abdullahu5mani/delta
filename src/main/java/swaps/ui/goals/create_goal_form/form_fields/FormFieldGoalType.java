package swaps.ui.goals.create_goal_form.form_fields;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Form field representing a type of goal.
 */
public class FormFieldGoalType extends JPanel  {
    private JComboBox goalTypeDropDown;

    public FormFieldGoalType() {
        // vertically stack components
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        DropdownOptionGoalType[] choices = {
                DropdownOptionGoalType.PRECISE,
                DropdownOptionGoalType.IMPRECISE
        };
        goalTypeDropDown = new JComboBox<>(choices);
        // wrap in JPanel with FlowLayout to avoid combo box filling entire window
        JPanel comboWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        comboWrapper.add(new JLabel("Select Goal Type:"));
        comboWrapper.add(goalTypeDropDown);
        this.add(comboWrapper);
    }

    /**
     * Sets the selected goal type.
     * @param goalType The selected goal type (precise/imprecise).
     */
    public void setSelectedGoalType(DropdownOptionGoalType goalType) {
        goalTypeDropDown.setSelectedItem(goalType);
    }

    /**
     * Specifies which action to take when the user interacts with the dropdown.
     * @param listener The action to take when the user interacts with the dropdown.
     */
    public void addListener(Consumer<DropdownOptionGoalType> listener) {
        goalTypeDropDown.addActionListener(e -> {
            DropdownOptionGoalType selected = (DropdownOptionGoalType) goalTypeDropDown.getSelectedItem();
            listener.accept(selected);
        });
    }
}
