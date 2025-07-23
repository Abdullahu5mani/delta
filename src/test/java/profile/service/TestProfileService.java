package profile.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import profile.model.Profile;
import profile.model.Sex;
import profile.model.UnitSystem;
import profile.repository.TestUserRepository;
import profile.view.ISignUpView;

/**
 * Simple in-memory implementation of IProfileService for unit testing
 */
public class TestProfileService implements IProfileService {
    private final ConcurrentHashMap<Integer, Profile> profiles = new ConcurrentHashMap<>();
    private Profile currentSession = null;
    private boolean shouldThrowException = false;
    private String exceptionMessage = "Test exception";
    private int nextId = 1; 

    @Override
    public void add(Profile profile) {
        if (shouldThrowException) {
            throw new RuntimeException(exceptionMessage);
        }
        
        // If profile doesn't have an ID, auto-generate one (simulating database behavior)
        Profile profileToStore = profile;
        if (profile.getId() == null) {
            profileToStore = new Profile.Builder()
                    .id(nextId++)
                    .name(profile.getName())
                    .age(profile.getAge())
                    .sex(profile.getSex())
                    .dateOfBirth(profile.getDateOfBirth())
                    .height(profile.getHeight())
                    .weight(profile.getWeight())
                    .unitSystem(profile.getUnitSystem())
                    .build();
        }
        
        profiles.put(profileToStore.getId(), profileToStore);
    }

    @Override
    public Optional<Profile> getById(Integer id) {
        if (shouldThrowException) {
            throw new RuntimeException(exceptionMessage);
        }
        return Optional.ofNullable(profiles.get(id));
    }

    @Override
    public List<Profile> listAll() {
        if (shouldThrowException) {
            throw new RuntimeException(exceptionMessage);
        }
        return new ArrayList<>(profiles.values());
    }

    @Override
    public void update(Profile profile) {
        if (shouldThrowException) {
            throw new RuntimeException(exceptionMessage);
        }
        profiles.put(profile.getId(), profile);
    }

    @Override
    public Optional<Profile> openSession(Integer profileId) {
        if (shouldThrowException) {
            throw new RuntimeException(exceptionMessage);
        }
        Profile profile = profiles.get(profileId);
        if (profile != null) {
            currentSession = profile;
            return Optional.of(profile);
        }
        return Optional.empty();
    }

    @Override
    public void closeSession() {
        if (shouldThrowException) {
            throw new RuntimeException(exceptionMessage);
        }
        currentSession = null;
    }

    @Override
    public Optional<Profile> getCurrentSession() {
        if (shouldThrowException) {
            throw new RuntimeException(exceptionMessage);
        }
        return Optional.ofNullable(currentSession);
    }

    // Test helper methods
    public void clear() {
        profiles.clear();
        currentSession = null;
        nextId = 1; // Reset 
    }

    public void setShouldThrowException(boolean shouldThrow) {
        this.shouldThrowException = shouldThrow;
    }

    public void setExceptionMessage(String message) {
        this.exceptionMessage = message;
    }

    public int size() {
        return profiles.size();
    }

    public void addProfile(Profile profile) {
        // Use the same logic as add() to handle null IDs
        Profile profileToStore = profile;
        if (profile.getId() == null) {
            profileToStore = new Profile.Builder()
                    .id(nextId++)
                    .name(profile.getName())
                    .age(profile.getAge())
                    .sex(profile.getSex())
                    .dateOfBirth(profile.getDateOfBirth())
                    .height(profile.getHeight())
                    .weight(profile.getWeight())
                    .unitSystem(profile.getUnitSystem())
                    .build();
        }
        profiles.put(profileToStore.getId(), profileToStore);
    }

    public boolean getShouldThrowException() {
        return shouldThrowException;
    }

    @Override
    public Profile updateUser(Integer profileId, ISignUpView.RawInput rawInput) throws ValidationException, ProfileNotFoundException {
        // Check if profile exists
        if (!profiles.containsKey(profileId)) {
            throw new ProfileNotFoundException("Profile with ID " + profileId + " not found");
        }
        
        // Validate the input data
        validateProfileData(rawInput);
        
        // Create updated profile
        Profile updatedProfile = createProfile(rawInput);
        
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
        
        profiles.put(profileId, profileWithId);
        
        
        return profileWithId;
    }

    @Override
    public Profile createUser(ISignUpView.RawInput rawInput) throws ValidationException, DuplicateUserException {
        validateProfileData(rawInput);
        
        Profile profile = createProfile(rawInput);
        
        // Check for duplicates in this test service's profiles
        checkForDuplicateName(profile.getName());
        
        add(profile);
        
        return profile;
    }
    
    private void checkForDuplicateName(String name) throws DuplicateUserException {
        boolean nameExists = profiles.values().stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(name));
        
        if (nameExists) {
            throw new DuplicateUserException("A profile with the name '" + name + "' already exists. Please use a different name.");
        }
    }

    @Override
    public void validateProfileData(ISignUpView.RawInput rawInput) throws ValidationException {
        ProfileServiceImplementor tempService = new ProfileServiceImplementor(new TestUserRepository());
        tempService.validateProfileData(rawInput);
    }

    @Override
    public void validateSex(String sex) throws ValidationException {
        ProfileServiceImplementor tempService = new ProfileServiceImplementor(new TestUserRepository());
        tempService.validateSex(sex);
    }

    @Override
    public void validateAge(String ageStr) throws ValidationException {
        ProfileServiceImplementor tempService = new ProfileServiceImplementor(new TestUserRepository());
        tempService.validateAge(ageStr);
    }

    @Override
    public void validateDateOfBirth(String dobStr) throws ValidationException {
        ProfileServiceImplementor tempService = new ProfileServiceImplementor(new TestUserRepository());
        tempService.validateDateOfBirth(dobStr);
    }

    @Override
    public void validateHeight(String heightStr, String unitSystem) throws ValidationException {
        ProfileServiceImplementor tempService = new ProfileServiceImplementor(new TestUserRepository());
        tempService.validateHeight(heightStr, unitSystem);
    }

    @Override
    public void validateWeight(String weightStr, String unitSystem) throws ValidationException {
        ProfileServiceImplementor tempService = new ProfileServiceImplementor(new TestUserRepository());
        tempService.validateWeight(weightStr, unitSystem);
    }

    @Override
    public void validateUnitSystem(String unitSystem) throws ValidationException {
        ProfileServiceImplementor tempService = new ProfileServiceImplementor(new TestUserRepository());
        tempService.validateUnitSystem(unitSystem);
    }

    @Override
    public Profile createProfile(ISignUpView.RawInput rawInput) throws ValidationException {
        try {
            String formattedDob = formatDateString(rawInput.dob().trim());
            LocalDate dateOfBirth = LocalDate.parse(formattedDob, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
            // Calculate age from date of birth (same logic as main service)
            int calculatedAge = java.time.Period.between(dateOfBirth, LocalDate.now()).getYears();
            
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
}
