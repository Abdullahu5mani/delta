package swaps.ui.goals.create_goal_form.form_fields;

import meals.models.MockDataFactory;

import javax.swing.*;
import java.util.List;

public class FormFieldNutrient extends JPanel {
    public FormFieldNutrient() {
        // TO DO: query backend for nutrient list
        List<DropdownOptionNutrient> dropdownOptions = MockDataFactory
                .createMockNutrients()
                .stream()
                .map(DropdownOptionNutrient::new)
                .toList();
        // JComboBox works with arrays not lists
        // new DropdownOptionNutrient[0] is a workaround to specify the type
        // otherwise the result is an Object[]
        DropdownOptionNutrient[] dropdownOptionsArray = dropdownOptions.toArray(new DropdownOptionNutrient[0]);
        JComboBox<DropdownOptionNutrient> dropdown = new JComboBox<>(dropdownOptionsArray);
        dropdown.setEditable(true);
        this.add(dropdown);
    }
}
