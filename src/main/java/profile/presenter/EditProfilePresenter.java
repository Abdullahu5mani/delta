package profile.presenter;

import app.AppMainPresenter;
import app.LeftNavItem;
import profile.model.Profile;
import profile.service.IProfileService;
import profile.view.IEditProfileView;

public class EditProfilePresenter {
    private final IEditProfileView view;
    private final IProfileService profileService;

    public EditProfilePresenter(IEditProfileView view, IProfileService profileService) {
        this.view = view;
        this.profileService = profileService;
    }

    /**
     * Initialize the presenter and set up view callbacks
     */
    public void initialize() {
        view.setOnSubmit(this::handleFormSubmission);
        view.setOnCancel(this::handleCancel);
        
        loadCurrentUserProfile();
    }

    /**
     * Load the current user's profile data into the form
     */
    private void loadCurrentUserProfile() {
        try {
            Profile currentUser = profileService.getCurrentSession()
                .orElseThrow(() -> new RuntimeException("No active user session found. Please select a profile first."));
            
            view.loadProfileData(currentUser);
        } catch (Exception e) {
            view.showError("Failed to load profile data: " + e.getMessage());
        }
    }

    private void handleFormSubmission() {
        try {
            // Extract form data from view
            IEditProfileView.EditInput editInput = view.getFormInput();
            
            if (editInput.id() == null) {
                view.showError("No profile selected for editing");
                return;
            }
            
            // Update the user profile 
            Profile updatedProfile = updateUserProfile(editInput);
            
            // show success message through the view
            view.showSuccess("Profile updated successfully for " + updatedProfile.getName() + "!");
            
            // Close the edit form
            view.close();
            
            try {
                AppMainPresenter.instance().navigateTo(LeftNavItem.SELECT_PROFILE);
            } catch (Exception navException) {
                System.out.println("Navigation after profile update failed");
            }
            
        } catch (IProfileService.ValidationException e) {
            // Show validation error to user
            view.showError(e.getMessage());
        } catch (IProfileService.ProfileNotFoundException e) {
            // Show profile not found error to user
            view.showError(e.getMessage());
        } catch (Exception e) {
            // generic error for unexpected issues
            view.showError("An unexpected error occurred: " + e.getMessage());
        }
    }

    private void handleCancel() {
        try {
            AppMainPresenter.instance().navigateTo(LeftNavItem.SELECT_PROFILE);
        } catch (Exception e) {
            view.showError("Failed to cancel: " + e.getMessage());
        }
    }

    /**
     * Update user profile using the service layer
     */
    private Profile updateUserProfile(IEditProfileView.EditInput editInput) throws IProfileService.ValidationException, IProfileService.ProfileNotFoundException {
        // Convert EditInput to RawInput for validation
        var rawInput = new profile.view.ISignUpView.RawInput(
            editInput.fullName(),
            editInput.dob(),
            editInput.height(),
            editInput.weight(),
            editInput.sex(),
            editInput.unitSystem()
        );
        
        return profileService.updateUser(editInput.id(), rawInput);
    }
}
