package profile.service;

import java.util.List;
import java.util.Optional;

import profile.model.Profile;
import profile.view.ISignUpView;

public interface IProfileService {

    Optional<Profile> getById(Integer id);

    void add(Profile profile);

    void update(Profile profile);

    List<Profile> listAll();

    /**
     * "Sign-in": returns the profile and stores it as the current session.
     *
     * @return empty if no such profile id exists
     */
    Optional<Profile> openSession(Integer profileId);

    /** "Sign-out": forget the current session (if any). */
    void closeSession();

    /** @return the signed-in user, or empty if no session is active. */
    Optional<Profile> getCurrentSession();

    /* User Creation and Validation ---------------------------------------- */

    /**
     * Creates a new user profile from raw form input.
     * Handles all validation and business logic.
     * 
     * @param rawInput The raw form data from the view
     * @return The created profile
     * @throws ValidationException if validation fails
     * @throws DuplicateUserException if a user with the same name already exists
     */
    Profile createUser(ISignUpView.RawInput rawInput) throws ValidationException, DuplicateUserException;

    /**
     * Updates an existing user profile from raw form input.
     * Handles all validation and business logic for profile updates.
     * 
     * @param profileId The ID of the profile to update
     * @param rawInput The raw form data from the view
     * @return The updated profile
     * @throws ValidationException if validation fails
     * @throws ProfileNotFoundException if the profile doesn't exist
     */
    Profile updateUser(Integer profileId, ISignUpView.RawInput rawInput) throws ValidationException, ProfileNotFoundException;

    /**
     * Validates all profile data fields.
     * 
     * @param rawInput The raw form data to validate
     * @throws ValidationException if any validation fails
     */
    void validateProfileData(ISignUpView.RawInput rawInput) throws ValidationException;

    /**
     * Validates the sex field.
     * 
     * @param sex The sex string to validate
     * @throws ValidationException if validation fails
     */
    void validateSex(String sex) throws ValidationException;

    /**
     * Validates the age field.
     * 
     * @param ageStr The age string to validate
     * @throws ValidationException if validation fails
     */
    void validateAge(String ageStr) throws ValidationException;

    /**
     * Validates the date of birth field.
     * 
     * @param dobStr The date of birth string to validate
     * @throws ValidationException if validation fails
     */
    void validateDateOfBirth(String dobStr) throws ValidationException;

    /**
     * Validates the height field based on unit system.
     * 
     * @param heightStr The height string to validate
     * @param unitSystem The unit system for validation ranges
     * @throws ValidationException if validation fails
     */
    void validateHeight(String heightStr, String unitSystem) throws ValidationException;

    /**
     * Validates the weight field based on unit system.
     * 
     * @param weightStr The weight string to validate
     * @param unitSystem The unit system for validation ranges
     * @throws ValidationException if validation fails
     */
    void validateWeight(String weightStr, String unitSystem) throws ValidationException;

    /**
     * Validates the unit system field.
     * 
     * @param unitSystem The unit system string to validate
     * @throws ValidationException if validation fails
     */
    void validateUnitSystem(String unitSystem) throws ValidationException;

    /**
     * Creates a Profile object from validated raw input.
     * 
     * @param rawInput The validated raw form data
     * @return The created Profile object
     * @throws ValidationException if profile creation fails
     */
    Profile createProfile(ISignUpView.RawInput rawInput) throws ValidationException;

    /* Custom exceptions --------------------------------------------------- */

    /**
     * Exception thrown when validation fails.
     */
    class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when attempting to create a user with a duplicate name.
     */
    class DuplicateUserException extends Exception {
        public DuplicateUserException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when attempting to update a profile that doesn't exist.
     */
    class ProfileNotFoundException extends Exception {
        public ProfileNotFoundException(String message) {
            super(message);
        }
    }
}
