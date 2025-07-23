package swaps.ui.goals.create_goal_form.form_fields;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Represents an intensity for imprecise goals.
 */
public class FormFieldGoalIntensity extends JPanel {
    private JComboBox<DropdownOptionGoalIntensity> goalIntensityDropdown;

    public FormFieldGoalIntensity() {
        // vertically stack components
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        DropdownOptionGoalIntensity[] dropdownChoiceIntensityOptions = {
                DropdownOptionGoalIntensity.HIGH,
                DropdownOptionGoalIntensity.MEDIUM,
                DropdownOptionGoalIntensity.LOW
        };
        goalIntensityDropdown = new JComboBox<>(dropdownChoiceIntensityOptions);
        // wrap in JPanel with FlowLayout to avoid combo box filling entire window
        JPanel comboWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        comboWrapper.add(new JLabel("Select Goal Intensity:"));
        comboWrapper.add(goalIntensityDropdown);
        this.add(comboWrapper);
    }

    /**
     * Add an action to take when the dropdown is interacted with.
     * @param listener The action to take when the user interacts with the form field.
     */
    public void addListener(Consumer<DropdownOptionGoalIntensity> listener) {
        goalIntensityDropdown.addActionListener(e -> {
            DropdownOptionGoalIntensity selectedGoalIntensity =
                    (DropdownOptionGoalIntensity) goalIntensityDropdown.getSelectedItem();
            listener.accept(selectedGoalIntensity);
        });
    }

    /**
     * Sets the selected goal intensity.
     * @param goalIntensity The selected intensity for the goal.
     */
    public void setSelectedGoalIntensity(DropdownOptionGoalIntensity goalIntensity) {
        goalIntensityDropdown.setSelectedItem(goalIntensity);
    }
}
