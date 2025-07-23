package profile.view;

import profile.model.Profile;

public interface IEditProfileView {

    /** Register a handler fired when the user hits "Update profile". */
    void setOnSubmit(Runnable callback);

    /** Register a handler fired when the user hits "Cancel". */
    void setOnCancel(Runnable callback);

    /** @return freshly-typed but *unvalidated* user data. */
    EditInput getFormInput();

    /** Inform the user something went wrong. */
    void showError(String msg);

    /** Inform the user of successful profile update. */
    void showSuccess(String msg);

    /** Close & dispose the window after success. */
    void close();

    /** Load existing profile data into the form fields. */
    void loadProfileData(Profile profile);

    /** Simple holder for raw text from the form. */
    record EditInput(
            Integer id,
            String fullName,
            String dob,
            String height,
            String weight,
            String sex,
            String unitSystem) {}
}
