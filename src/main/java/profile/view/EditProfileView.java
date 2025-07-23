package profile.view;

import java.time.format.DateTimeFormatter;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import profile.model.Profile;
import profile.model.Sex;
import profile.model.UnitSystem;

/**
 * Panel-based implementation of the edit profile view 
 */
public class EditProfileView extends JPanel implements IEditProfileView {

    private JTextField fullNameField;
    private JTextField dobField;
    private JTextField heightField;
    private JTextField weightField;

    private JComboBox<Sex> sexCombo;
    private JComboBox<UnitSystem> unitSystemCombo;

    private JButton updateProfileButton;
    private JButton cancelButton;

    //presenter hooks
    private Runnable onSubmit;
    private Runnable onCancel;
    
    // Store the profile being edited
    private Profile currentProfile;

    //constructor
    public EditProfileView() {
        initializeComponents();
        hookButtons();
    }

    @Override
    public void setOnSubmit(Runnable callback) {
        this.onSubmit = callback;
    }

    @Override
    public void setOnCancel(Runnable callback) {
        this.onCancel = callback;
    }

    @Override
    public EditInput getFormInput() {
        return new EditInput(
                currentProfile != null ? currentProfile.getId() : null, // id
                fullNameField.getText().trim(), // fullName
                dobField.getText().trim(), // dob
                heightField.getText().trim(), // height
                weightField.getText().trim(), // weight
                ((Sex) sexCombo.getSelectedItem()).name(), // sex
                ((UnitSystem) unitSystemCombo.getSelectedItem()).name()); // unitSystem
    }

    @Override
    public void showError(String msg) {
        JOptionPane.showMessageDialog(
                this,
                msg,
                "Validation error",
                JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void showSuccess(String msg) {
        JOptionPane.showMessageDialog(
                this,
                msg,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void close() {
        clearForm();
    }

    @Override
    public void loadProfileData(Profile profile) {
        this.currentProfile = profile;
        if (profile != null) {
            fullNameField.setText(profile.getName());
            dobField.setText(profile.getDateOfBirth().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            heightField.setText(String.valueOf(profile.getHeight()));
            weightField.setText(String.valueOf(profile.getWeight()));
            sexCombo.setSelectedItem(profile.getSex());
            unitSystemCombo.setSelectedItem(profile.getUnitSystem());
        }
    }

    /**
     * Clears all form fields to their default state.
     */
    private void clearForm() {
        fullNameField.setText("");
        dobField.setText("");
        heightField.setText("");
        weightField.setText("");
        sexCombo.setSelectedIndex(0);
        unitSystemCombo.setSelectedIndex(0);
        currentProfile = null;
    }

    //wiring helper
    private void hookButtons() {
        updateProfileButton.addActionListener(e -> {
            if (onSubmit != null)
                onSubmit.run();
        });
        cancelButton.addActionListener(e -> {
            if (onCancel != null)
                onCancel.run();
        });
    }

    // GUI initialization
    private void initializeComponents() {
        // overall layout
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);

        // Create components
        fullNameField = new JTextField(20);
        fullNameField.setToolTipText("Enter your full name");

        heightField = new JTextField(20);
        heightField.setToolTipText("Enter your height");

        weightField = new JTextField(20);
        weightField.setToolTipText("Enter your weight");

        // Create combo boxes
        sexCombo = new JComboBox<>();
        for (Sex sex : Sex.values()) {
            sexCombo.addItem(sex);
        }

        unitSystemCombo = new JComboBox<>();
        for (UnitSystem u : UnitSystem.values()) {
            unitSystemCombo.addItem(u);
        }

        // Create date field
        dobField = new JTextField(20);
        dobField.setToolTipText("Enter date in format: YYYY-MM-DD (e.g., 1990-12-25)");

        // Create buttons
        updateProfileButton = new JButton("Update Profile");
        cancelButton = new JButton("Cancel");

        // Add title label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = java.awt.GridBagConstraints.CENTER;
        add(new JLabel("<html><h2>Edit Profile</h2></html>"), gbc);
        gbc.gridwidth = 1; 

        // Add components to panel
        // FULL-NAME
        gbc.gridx = 0; //column position
        gbc.gridy = 1; //row position
        gbc.anchor = java.awt.GridBagConstraints.WEST; //alignment inside the cell
        add(new JLabel("Full Name:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL; //stretch horizontally
        add(fullNameField, gbc);

        //SEX
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        add(new JLabel("Sex:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(sexCombo, gbc);

        //DOB
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        add(new JLabel("Date of Birth:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(dobField, gbc);

        //UNIT SYSTEM
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        add(new JLabel("Unit System:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(unitSystemCombo, gbc);

        //HEIGHT
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        add(new JLabel("Height:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(heightField, gbc);

        //WEIGHT
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        add(new JLabel("Weight:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(weightField, gbc);

        // BUTTONS
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(updateProfileButton);
        buttonPanel.add(cancelButton);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = java.awt.GridBagConstraints.CENTER;
        add(buttonPanel, gbc);
    }
}
