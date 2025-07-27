package swaps.ui.goals.create_goal_form.form_fields;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Form field representing a precise amount.
 */
public class FormFieldPreciseAmount extends JPanel {
    private static final int PREFERRED_FORM_FIELD_WIDTH = 10;
    private JTextField textField;

    public FormFieldPreciseAmount() {
        super(new FlowLayout(FlowLayout.CENTER));
        JLabel formLabel = new JLabel("Enter an amount (%)");
        this.add(formLabel);
        
        textField = new JTextField(PREFERRED_FORM_FIELD_WIDTH);
        this.add(textField);
    }

    /**
     * @return The value entered in the text field as a Float, or null if invalid/empty.
     */
    public Float getValue() {
        String text = textField.getText().trim();
        if (text.isEmpty()) {
            return null;
        }
        
        try {
            return Float.parseFloat(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Sets the value in the text field.
     * @param value The value to set
     */
    public void setValue(Float value) {
        if (value == null) {
            textField.setText("");
        } else {
            textField.setText(value.toString());
        }
    }
}
