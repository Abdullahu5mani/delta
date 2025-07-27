package swaps.ui.goals.create_goal_form;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import meals.services.QueryNutrientsService;
import shared.ui.searchable_list.SearchableListView;
import swaps.ui.goals.create_goal_form.form_fields.DropdownOptionNutrient;
import swaps.ui.goals.create_goal_form.form_fields.FormFieldDateRange;
import swaps.ui.goals.create_goal_form.form_fields.FormFieldGoalDirection;
import swaps.ui.goals.create_goal_form.form_fields.FormFieldGoalIntensity;
import swaps.ui.goals.create_goal_form.form_fields.FormFieldGoalType;
import swaps.ui.goals.create_goal_form.form_fields.FormFieldNutrient;
import swaps.ui.goals.create_goal_form.form_fields.FormFieldPreciseAmount;

/**
 * The view representing the form for creating a goal.
 */
public class GoalsFormView extends JPanel {
    private FormFieldGoalType typeField;
    private FormFieldNutrient nutrientField;
    private FormFieldPreciseAmount preciseAmountField;
    private FormFieldGoalIntensity intensityField;
    private FormFieldGoalDirection directionField;
    private FormFieldDateRange dateRangeField;
    private SearchableListView<DropdownOptionNutrient> nutrientListView;

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
        
        // Create styled header label
        JLabel headerLabel = new JLabel(header);
        Font originalFont = headerLabel.getFont();
        Font boldFont = new Font(originalFont.getName(), Font.BOLD, originalFont.getSize() + 4);
        headerLabel.setFont(boldFont);
        
        this.add(headerLabel);
    }

    /**
     * Initializes the form fields.
     * Helper method to be called in the constructor.
     */
    private void initFormFields() {
        typeField = new FormFieldGoalType();
        this.add(typeField);
        
        directionField = new FormFieldGoalDirection();
        this.add(directionField);
        
        // Load nutrients from the database instead of mock data
        List<DropdownOptionNutrient> dropdownOptionNutrientList;
        try {
            dropdownOptionNutrientList = QueryNutrientsService.instance()
                    .fetchAll()
                    .stream()
                    .map(DropdownOptionNutrient::new)
                    .toList();
        } catch (QueryNutrientsService.QueryNutrientServiceException e) {
            // Fallback to empty list if there's an error loading nutrients
            dropdownOptionNutrientList = List.of();
        }
        
        nutrientListView = new SearchableListView<>(dropdownOptionNutrientList);
        this.add(nutrientListView);
        preciseAmountField = new FormFieldPreciseAmount();
        this.add(preciseAmountField);
        intensityField = new FormFieldGoalIntensity();
        this.add(intensityField);
        
        // Add date range field for selecting meal log dates
        dateRangeField = new FormFieldDateRange();
        this.add(dateRangeField);
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

    /**
     * @return The field for selecting the goal direction (increase/decrease).
     */
    public FormFieldGoalDirection getDirectionField() {
        return directionField;
    }

    /**
     * @return The selected nutrient from the searchable list.
     */
    public DropdownOptionNutrient getSelectedNutrient() {
        return nutrientListView.getSelectedItem();
    }

    /**
     * @return The field for entering precise amounts.
     */
    public FormFieldPreciseAmount getPreciseAmountField() {
        return preciseAmountField;
    }

    /**
     * @return The field for selecting date range for meal logs.
     */
    public FormFieldDateRange getDateRangeField() {
        return dateRangeField;
    }
}
