package profile.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import profile.model.Profile;

/**
 * Displays available profiles in a list and allows user to select one.
 */
public class SplashView extends JPanel implements ISplashView {
    
    private JList<Profile> profileList;
    private DefaultListModel<Profile> listModel;
    private JButton selectButton;
    private JButton createNewButton;
    
    // Callbacks
    private Consumer<Profile> onProfileSelected;
    private Runnable onCreateNewProfile;
    
    public SplashView() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    @Override
    public void displayUserProfiles(List<Profile> profiles) {
        SwingUtilities.invokeLater(() -> {
            listModel.clear();
            for (Profile profile : profiles) {
                listModel.addElement(profile);
            }
            
            // Enable/disable buttons based on whether profiles exist
            boolean hasProfiles = !profiles.isEmpty();
            selectButton.setEnabled(hasProfiles && profileList.getSelectedIndex() != -1);
            
            if (hasProfiles) {
                // Select first profile by default
                profileList.setSelectedIndex(0); 
                selectButton.setEnabled(true);
            }
        });
    }
    
    @Override
    public void setOnProfileSelected(Consumer<Profile> callback) {
        this.onProfileSelected = callback;
    }
    
    @Override
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
    
    @Override
    public void showSignUpForm() {
        if (onCreateNewProfile != null) {
            onCreateNewProfile.run();
        }
    }
    
    @Override
    public void setOnCreateNewProfile(Runnable callback) {
        this.onCreateNewProfile = callback;
    }
    
    private void initializeComponents() {
        // Create list model and list
        listModel = new DefaultListModel<>();
        profileList = new JList<>(listModel);
        profileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        profileList.setCellRenderer(new ProfileListCellRenderer());
        
        // Create buttons
        selectButton = new JButton("Select Profile");
        selectButton.setEnabled(false);
        
        createNewButton = new JButton("Create New Profile");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Select User Profile");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);
        
        // Profile list in scroll pane
        JScrollPane scrollPane = new JScrollPane(profileList);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        scrollPane.setBorder(BorderFactory.createTitledBorder("Available Profiles"));
        add(scrollPane, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(selectButton);
        buttonPanel.add(createNewButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        // enable/disable select button based on selection
        profileList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectButton.setEnabled(profileList.getSelectedValue() != null);
            }
        });
        
        // Handle profile selection
        selectButton.addActionListener(e -> {
            Profile selectedProfile = profileList.getSelectedValue();
            if (selectedProfile != null && onProfileSelected != null) {
                onProfileSelected.accept(selectedProfile);
            }
        });
        
        
        // Handle create new profile
        createNewButton.addActionListener(e -> {
            if (onCreateNewProfile != null) {
                onCreateNewProfile.run();
            }
        });
    }
    
    /**
     * show name and basic info.
     */
    private static class ProfileListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Profile profile) {
                setText(String.format("<html><b>%s</b><br><small>Age: %d, %s</small></html>", 
                       profile.getName(), 
                       profile.getAge(),
                       profile.getSex().toString().toLowerCase()));
            }
            
            return this;
        }
    }
}
