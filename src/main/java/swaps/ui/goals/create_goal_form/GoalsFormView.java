package swaps.ui.goals.create_goal_form;

import meals.models.MockDataFactory;
import shared.ui.searchable_list.SearchableListView;
import swaps.ui.goals.create_goal_form.form_fields.*;

import javax.swing.*;
import java.awt.*;

import java.util.List;

/**
 * The view representing the form for creating a goal.
 */
public class GoalsFormView extends JPanel {
    private FormFieldGoalType typeField;
    private FormFieldNutrient nutrientField;
    private FormFieldPreciseAmount preciseAmountField;
    private FormFieldGoalIntensity intensityField;

    /**
     * @param header The header to display for the form.
     */
    public GoalsFormView(String header) {
        initLayout(header);
        initFormFields();
    }

    /**
     *  Initializes the layout of the view.
     *  Helper method to be called in the constructor.
     * @param header The header to display for the form.
     */
    private void initLayout(String header) {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        this.add(new JLabel(header));
    }

    /**
     * Initializes the form fields.
     * Helper method to be called in the constructor.
     */
    private void initFormFields() {
        typeField = new FormFieldGoalType();
        this.add(typeField);
        List<DropdownOptionNutrient> dropdownOptionNutrientList = MockDataFactory
                .createMockNutrients()
                .stream()
                .map(DropdownOptionNutrient::new)
                .toList();
        SearchableListView<DropdownOptionNutrient> listView = new SearchableListView<>(dropdownOptionNutrientList);
        this.add(listView);
        preciseAmountField = new FormFieldPreciseAmount();
        this.add(preciseAmountField);
        intensityField = new FormFieldGoalIntensity();
        this.add(intensityField);
    }

    /**
     * Controls the visibility of the form field allowing the user to specify a precise amount for the goal.
     * @param isVisible Whether the precise amount form field should be rendered.
     */
    public void setPreciseAmountFieldVisibility(boolean isVisible) {
        preciseAmountField.setVisible(isVisible);
    }

    /**
     * Controls the visibility of the form field allowing the user to specify a goal intensity.
     * @param isVisible Whether the intensity form field should be rendered.
     */
    public void setIntensityFieldVisibility(boolean isVisible) {
        intensityField.setVisible(isVisible);
    }

    /**
     * @return The field for selecting the type of goal (precise/imprecise).
     */
    public FormFieldGoalType getTypeField() {
        return typeField;
    }

    /**
     * @return The field for selecting the goal intensity.
     */
    public FormFieldGoalIntensity getIntensityField() {
        return intensityField;
    }
}
