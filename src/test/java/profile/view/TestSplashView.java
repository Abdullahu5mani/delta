package profile.view;

import profile.model.Profile;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Test implementation of ISplashView for unit testing.
 */
public class TestSplashView implements ISplashView {
    
    private List<Profile> displayedProfiles = new ArrayList<>();
    private Consumer<Profile> onProfileSelected;
    private Runnable onCreateNewProfile;
    private String lastErrorMessage;
    private boolean signUpFormShown = false;
    
    @Override
    public void displayUserProfiles(List<Profile> profiles) {
        this.displayedProfiles = new ArrayList<>(profiles);
    }
    
    @Override
    public void setOnProfileSelected(Consumer<Profile> callback) {
        this.onProfileSelected = callback;
    }
    
    @Override
    public void showErrorMessage(String message) {
        this.lastErrorMessage = message;
    }
    
    @Override
    public void showSignUpForm() {
        this.signUpFormShown = true;
        if (onCreateNewProfile != null) {
            onCreateNewProfile.run();
        }
    }
    
    @Override
    public void setOnCreateNewProfile(Runnable callback) {
        this.onCreateNewProfile = callback;
    }
    
    // Test helper methods
    
    /**
     * Simulate user selecting a profile.
     */
    public void simulateProfileSelection(Profile profile) {
        if (onProfileSelected != null) {
            onProfileSelected.accept(profile);
        }
    }
    
    /**
     * Simulate user clicking "Create New Profile".
     */
    public void simulateCreateNewProfile() {
        if (onCreateNewProfile != null) {
            onCreateNewProfile.run();
        }
    }
    
    /**
     * Get the profiles currently displayed.
     */
    public List<Profile> getDisplayedProfiles() {
        return new ArrayList<>(displayedProfiles);
    }
    
    /**
     * Get the last error message.
     */
    public String getLastErrorMessage() {
        return lastErrorMessage;
    }
    
    /**
     * Check if sign up form was shown.
     */
    public boolean wasSignUpFormShown() {
        return signUpFormShown;
    }
    
    /**
     * Reset test state.
     */
    public void reset() {
        displayedProfiles.clear();
        onProfileSelected = null;
        onCreateNewProfile = null;
        lastErrorMessage = null;
        signUpFormShown = false;
    }
}
