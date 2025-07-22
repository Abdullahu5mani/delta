package profile.view;

import java.util.List;

import profile.model.Profile;

/**
 * Interface for the profile selection/splash view.
 */
public interface ISplashView {
    
    /**
     * Display the list of available user profiles.
     * 
     * @param profiles List of profiles to display
     */
    void displayUserProfiles(List<Profile> profiles);
    
    /**
     * Register a callback for when a user profile is selected.
     * 
     * @param callback Callback that receives the selected profile
     */
    void setOnProfileSelected(java.util.function.Consumer<Profile> callback);
    
    /**
     * Show an error message to the user.
     * 
     * @param message The error message to display
     */
    void showErrorMessage(String message);
    
    /**
     * Show the sign up form (navigate to profile creation).
     */
    void showSignUpForm();
    
    /**
     * Register a callback for when the user wants to create a new profile.
     * 
     * @param callback Callback to execute when user clicks "Create New Profile"
     */
    void setOnCreateNewProfile(Runnable callback);
}
