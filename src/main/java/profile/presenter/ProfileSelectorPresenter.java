package profile.presenter;

import java.util.List;

import app.AppMainPresenter;
import app.LeftNavItem;
import profile.model.Profile;
import profile.service.IProfileService;
import profile.view.ISplashView;

/**
 * Presenter for profile selection (splash screen).
 * Handles loading profiles from service and user selection.
 */
public class ProfileSelectorPresenter {
    
    private final ISplashView view;
    private final IProfileService profileService;
    
    public ProfileSelectorPresenter(ISplashView view, IProfileService profileService) {
        this.view = view;
        this.profileService = profileService;
    }
    
    /**
     * Initialize the presenter and set up view callbacks.
     */
    public void initialize() {
        view.setOnProfileSelected(this::handleProfileSelection);
        view.setOnCreateNewProfile(this::handleCreateNewProfile);
        loadProfiles();
    }
    
    /**
     * Load all available profiles from the service and display them.
     */
    private void loadProfiles() {
        try {
            List<Profile> profiles = profileService.listAll();
            view.displayUserProfiles(profiles);
            
            if (profiles.isEmpty()) {
                view.showErrorMessage("No profiles found. Please create a new profile to get started.");
            }
        } catch (Exception e) {
            view.showErrorMessage("Failed to load profiles: " + e.getMessage());
        }
    }
    
    /**
     * Handle when user selects a profile.
     * Opens a session for the selected profile.
     * 
     * @param selectedProfile The profile selected by the user
     */
    private void handleProfileSelection(Profile selectedProfile) {
        try {
            // Open session for selected profile
            profileService.openSession(selectedProfile.getId());
        
            
            // Navigate to main functionality 
            AppMainPresenter.instance().navigateTo(LeftNavItem.LOG_MEAL);
            
        } catch (Exception e) {
            view.showErrorMessage("Failed to select profile: " + e.getMessage());
        }
    }
    
    /**
     * Handle when user wants to create a new profile.
     * Navigates to the create profile view.
     */
    private void handleCreateNewProfile() {
        try {
            AppMainPresenter.instance().navigateTo(LeftNavItem.CREATE_PROFILE);
        } catch (Exception e) {
            view.showErrorMessage("Failed to navigate to profile creation: " + e.getMessage());
        }
    }
    

}
