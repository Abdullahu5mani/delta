package profile.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import profile.model.Profile;
import profile.model.Sex;
import profile.model.UnitSystem;
import profile.repository.IUserRepository;
import profile.view.ISignUpView;

/**
 * Concrete implementation of {@link IProfileService}.
 */
public class ProfileServiceImplementor implements IProfileService {

    private final IUserRepository userRepo;

    /** Holds the currently signed-in user (null when no session active). */
    private final AtomicReference<Profile> currentUser = new AtomicReference<>();

    public ProfileServiceImplementor(IUserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public Optional<Profile> getById(Integer id) {
        return userRepo.findById(id);
    }

    @Override
    public void add(Profile profile) {
        userRepo.add(profile);
    }

    @Override
    public void update(Profile profile) {
        userRepo.update(profile);
    }

    @Override
    public List<Profile> listAll() {
        return new ArrayList<>(userRepo.findAll());
    }

    @Override
    public Optional<Profile> openSession(Integer id) {
        Optional<Profile> candidate = userRepo.findById(id);
        candidate.ifPresent(currentUser::set);      // only set if profile exists
        return candidate;
    }

    @Override
    public void closeSession() {
        currentUser.set(null);
    }

    @Override
    public Optional<Profile> getCurrentSession() {
        return Optional.ofNullable(currentUser.get());
    }

    /* User Creation and Validation ---------------------------------------- */

    @Override
    public Profile createUser(ISignUpView.RawInput rawInput) throws ValidationException, DuplicateUserException {
        // Validate the input
        validateProfileData(rawInput);
        
        // Create the profile
        Profile profile = createProfile(rawInput);
        
        // Check for duplicates
        checkForDuplicateName(profile.getName());
        
        // Save the profile
        add(profile);
        
        return profile;
    }

    @Override
    public Profile updateUser(Integer profileId, ISignUpView.RawInput rawInput) throws ValidationException, ProfileNotFoundException {
        // validate that the profile exists
        if (!getById(profileId).isPresent()) {
            throw new ProfileNotFoundException("Profile with ID " + profileId + " not found");
        }
        
        // Validate the input data
        validateProfileData(rawInput);
        
        // Create updated profile with calculated age from DOB
        Profile updatedProfile = createProfile(rawInput);
        
        // Create final profile with the existing ID
        Profile profileWithId = new Profile.Builder()
            .id(profileId)
            .name(updatedProfile.getName())
            .age(updatedProfile.getAge()) 
            .sex(updatedProfile.getSex())
            .dateOfBirth(updatedProfile.getDateOfBirth())
            .height(updatedProfile.getHeight())
            .weight(updatedProfile.getWeight())
            .unitSystem(updatedProfile.getUnitSystem())
            .build();
        
        update(profileWithId);
        
        
        return profileWithId;
    }

    @Override
    public void validateProfileData(ISignUpView.RawInput rawInput) throws ValidationException {
        validateRequiredFields(rawInput);
        validateDateOfBirth(rawInput.dob()); 
        validateHeight(rawInput.height(), rawInput.unitSystem());
        validateWeight(rawInput.weight(), rawInput.unitSystem());
        validateSex(rawInput.sex());
        validateUnitSystem(rawInput.unitSystem());
    }

    @Override
    public void validateSex(String sex) throws ValidationException {
        if (sex == null || sex.trim().isEmpty()) {
            throw new ValidationException("Sex selection is required");
        }
        
        try {
            Sex.valueOf(sex.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid sex selection");
        }
    }

    @Override
    public void validateAge(String ageStr) throws ValidationException {
        if (ageStr == null || ageStr.trim().isEmpty()) {
            throw new ValidationException("Age is required");
        }
        
        try {
            int age = Integer.parseInt(ageStr.trim());
            if (age < 0 || age > 150) {
                throw new ValidationException("Age must be between 0 and 150");
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("Age must be a valid number");
        }
    }

    /**
     * Validates the date of birth field.
     * 
     * @param dobStr The date of birth string to validate
     * @throws ValidationException if validation fails
     */
    @Override
    public void validateDateOfBirth(String dobStr) throws ValidationException {
        if (dobStr == null || dobStr.trim().isEmpty()) {
            throw new ValidationException("Date of birth is required (YYYY-MM-DD format)");
        }
        
        try {
            String formattedDob = formatDateString(dobStr.trim());
            LocalDate dateOfBirth = LocalDate.parse(formattedDob, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
            if (dateOfBirth.isAfter(LocalDate.now())) {
                throw new ValidationException("Date of birth cannot be in the future");
            }
            
            if (dateOfBirth.isBefore(LocalDate.of(1900, 1, 1))) {
                throw new ValidationException("Date of birth cannot be before 1900");
            }
            
            // Validate that calculated age is reasonable
            int calculatedAge = calculateAgeFromDateOfBirth(dateOfBirth);
            if (calculatedAge < 0 || calculatedAge > 150) {
                throw new ValidationException("Calculated age (" + calculatedAge + ") is not reasonable");
            }
            
        } catch (DateTimeParseException e) {
            throw new ValidationException("Invalid date format. Please use YYYY-MM-DD");
        }
    }

    @Override
    public void validateHeight(String heightStr, String unitSystem) throws ValidationException {
        if (heightStr == null || heightStr.trim().isEmpty()) {
            throw new ValidationException("Height is required");
        }
        
        try {
            double height = Double.parseDouble(heightStr.trim());
            if (height <= 0) {
                throw new ValidationException("Height must be a positive number");
            }
            
            if ("METRIC".equalsIgnoreCase(unitSystem)) {
                if (height < 30 || height > 300) {
                    throw new ValidationException("Height must be between 30 and 300 cm");
                }
            } else if ("IMPERIAL".equalsIgnoreCase(unitSystem)) {
                if (height < 1 || height > 10) {
                    throw new ValidationException("Height must be between 1 and 10 feet");
                }
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("Height must be a valid number");
        }
    }

    @Override
    public void validateWeight(String weightStr, String unitSystem) throws ValidationException {
        if (weightStr == null || weightStr.trim().isEmpty()) {
            throw new ValidationException("Weight is required");
        }
        
        try {
            double weight = Double.parseDouble(weightStr.trim());
            if (weight <= 0) {
                throw new ValidationException("Weight must be a positive number");
            }
            
            if ("METRIC".equalsIgnoreCase(unitSystem)) {
                if (weight < 0.5 || weight > 1000) {
                    throw new ValidationException("Weight must be between 0.5 and 1000 kg");
                }
            } else if ("IMPERIAL".equalsIgnoreCase(unitSystem)) {
                if (weight < 1 || weight > 2200) {
                    throw new ValidationException("Weight must be between 1 and 2200 pounds");
                }
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("Weight must be a valid number");
        }
    }

    @Override
    public void validateUnitSystem(String unitSystem) throws ValidationException {
        if (unitSystem == null || unitSystem.trim().isEmpty()) {
            throw new ValidationException("Unit system selection is required");
        }
        
        try {
            UnitSystem.valueOf(unitSystem.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid unit system selection");
        }
    }

    @Override
    public Profile createProfile(ISignUpView.RawInput rawInput) throws ValidationException {
        try {
            String formattedDob = formatDateString(rawInput.dob().trim());
            LocalDate dateOfBirth = LocalDate.parse(formattedDob, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
            // Calculate age from date of birth 
            int calculatedAge = calculateAgeFromDateOfBirth(dateOfBirth);
            
            double height = Double.parseDouble(rawInput.height().trim());
            double weight = Double.parseDouble(rawInput.weight().trim());
            Sex sex = Sex.valueOf(rawInput.sex().toUpperCase());
            UnitSystem unitSystem = UnitSystem.valueOf(rawInput.unitSystem().toUpperCase());
            
            return new Profile.Builder()
                    .name(rawInput.fullName().trim())
                    .age(calculatedAge) 
                    .sex(sex)
                    .dateOfBirth(dateOfBirth)
                    .height(height)
                    .weight(weight)
                    .unitSystem(unitSystem)
                    .build();
                    
        } catch (Exception e) {
            throw new ValidationException("Error creating profile: " + e.getMessage());
        }
    }
    
    /**
     * Calculate age from date of birth.
     * @param dateOfBirth The date of birth
     * @return The calculated age in years
     */
    private int calculateAgeFromDateOfBirth(LocalDate dateOfBirth) {
        return java.time.Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    /* Helper methods ------------------------------------------------------ */

    private void validateRequiredFields(ISignUpView.RawInput rawInput) throws ValidationException {
        if (rawInput.fullName() == null || rawInput.fullName().trim().isEmpty()) {
            throw new ValidationException("Full name is required");
        }
        
        if (rawInput.fullName().trim().length() < 2) {
            throw new ValidationException("Full name must be at least 2 characters long");
        }
    }

    private String formatDateString(String dobString) throws ValidationException {
        if (dobString.length() < 8 || dobString.length() > 10) {
            throw new ValidationException("Date of birth must be in YYYY-MM-DD format");
        }
        
        // Handle YYYYMMDD format
        if (dobString.length() == 8 && dobString.matches("\\d{8}")) {
            return dobString.substring(0, 4) + "-" +
                   dobString.substring(4, 6) + "-" +
                   dobString.substring(6, 8);
        }
        
        // Handle YYYY-MM-DD format
        if (!dobString.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new ValidationException("Date of birth must be in YYYY-MM-DD format");
        }
        
        return dobString;
    }

    private void checkForDuplicateName(String name) throws DuplicateUserException {
        List<Profile> existingProfiles = listAll();
        boolean nameExists = existingProfiles.stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(name));
        
        if (nameExists) {
            throw new DuplicateUserException("A profile with the name '" + name + "' already exists. Please use a different name.");
        }
    }
}
