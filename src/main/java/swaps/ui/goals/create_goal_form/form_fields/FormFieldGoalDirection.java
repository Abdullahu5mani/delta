package swaps.ui.goals.create_goal_form.form_fields;

import java.awt.FlowLayout;
import java.util.function.Consumer;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Form field representing the direction of a goal (increase or decrease).
 */
public class FormFieldGoalDirection extends JPanel {
    private JComboBox<DropdownOptionGoalDirection> goalDirectionDropDown;

    public FormFieldGoalDirection() {
        // vertically stack components
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        DropdownOptionGoalDirection[] choices = {
                DropdownOptionGoalDirection.INCREASE,
                DropdownOptionGoalDirection.DECREASE
        };
        goalDirectionDropDown = new JComboBox<>(choices);
        JPanel comboWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        comboWrapper.add(new JLabel("Goal Direction:"));
        comboWrapper.add(goalDirectionDropDown);
        this.add(comboWrapper);
    }

    /**
     * Sets the selected goal direction.
     * @param goalDirection The selected goal direction (increase/decrease).
     */
    public void setSelectedGoalDirection(DropdownOptionGoalDirection goalDirection) {
        goalDirectionDropDown.setSelectedItem(goalDirection);
    }

    /**
     * Gets the selected goal direction.
     * @return The selected goal direction.
     */
    public DropdownOptionGoalDirection getSelectedGoalDirection() {
        return (DropdownOptionGoalDirection) goalDirectionDropDown.getSelectedItem();
    }

    /**
     * Specifies which action to take when the user interacts with the dropdown.
     * @param listener The action to take when the user interacts with the dropdown.
     */
    public void addListener(Consumer<DropdownOptionGoalDirection> listener) {
        goalDirectionDropDown.addActionListener(e -> {
            DropdownOptionGoalDirection selected = (DropdownOptionGoalDirection) goalDirectionDropDown.getSelectedItem();
            listener.accept(selected);
        });
    }
}
