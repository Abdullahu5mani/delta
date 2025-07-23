package profile.service;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import profile.model.Profile;
import profile.model.Sex;
import profile.model.UnitSystem;
import profile.repository.TestUserRepository;

public class ProfileServiceImplementorTest {

    private ProfileServiceImplementor service;
    private TestUserRepository repository;

    @BeforeEach
    public void setUp() {
        repository = new TestUserRepository();
        service = new ProfileServiceImplementor(repository);
    }

    @Test
    public void testAddProfile_Success() {
        Profile profile = new Profile.Builder()
                .id(1)
                .name("John Doe")
                .age(25)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.of(1998, 1, 1))
                .height(180.0)
                .weight(70.0)
                .unitSystem(UnitSystem.METRIC)
                .build();

        service.add(profile);

        Optional<Profile> found = repository.findById(profile.getId());
        assertTrue(found.isPresent());
        assertEquals(profile, found.get());
    }

    @Test
    public void testAddProfile_NullProfile() {
        assertThrows(NullPointerException.class, () -> {
            service.add(null);
        });
    }

    @Test
    public void testAddProfile_DuplicateProfile() {
        Profile profile = new Profile.Builder()
                .id(2)
                .name("John Doe")
                .age(25)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.of(1998, 1, 1))
                .height(180.0)
                .weight(70.0)
                .unitSystem(UnitSystem.METRIC)
                .build();

        service.add(profile);
        
        // Try to add the same profile again - should handle gracefully
        assertDoesNotThrow(() -> {
            service.add(profile);
        });
    }

    @Test
    public void testAddProfile_WithDefaults() {
        Profile profile = new Profile.Builder()
                .id(3)
                .name("Jane Doe")
                .age(30)
                .sex(Sex.FEMALE)
                .dateOfBirth(LocalDate.of(1993, 5, 15))
                .height(165.0)
                .weight(60.0)
                .build(); // No unitSystem specified - should get default

        service.add(profile);

        Optional<Profile> found = repository.findById(profile.getId());
        assertTrue(found.isPresent());
        // The profile should have the default unit system
        assertEquals(UnitSystem.METRIC, found.get().getUnitSystem());
    }

    @Test
    public void testGetById() {
        Profile profile = new Profile.Builder()
                .id(4)
                .name("Test User")
                .age(28)
                .sex(Sex.FEMALE)
                .dateOfBirth(LocalDate.of(1995, 3, 10))
                .height(170.0)
                .weight(65.0)
                .build();

        service.add(profile);
        
        Optional<Profile> found = service.getById(profile.getId());
        assertTrue(found.isPresent());
        assertEquals(profile, found.get());
        
        Optional<Profile> notFound = service.getById(999);
        assertFalse(notFound.isPresent());
    }

    @Test
    public void testUpdate() {
        Profile original = new Profile.Builder()
                .id(5)
                .name("Original Name")
                .age(25)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.of(1998, 1, 1))
                .height(175.0)
                .weight(70.0)
                .build();

        service.add(original);
        
        Profile updated = new Profile.Builder()
                .id(original.getId()) // Same ID
                .name("Updated Name")
                .age(26)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.of(1998, 1, 1))
                .height(175.0)
                .weight(72.0)
                .build();

        service.update(updated);
        
        Optional<Profile> found = service.getById(original.getId());
        assertTrue(found.isPresent());
        assertEquals("Updated Name", found.get().getName());
        assertEquals(72.0, found.get().getWeight());
    }

    @Test
    public void testListAll() {
        assertTrue(service.listAll().isEmpty());
        
        Profile profile1 = new Profile.Builder()
                .id(6)
                .name("User 1")
                .age(25)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.of(1998, 1, 1))
                .height(180.0)
                .weight(70.0)
                .build();
                
        Profile profile2 = new Profile.Builder()
                .id(7)
                .name("User 2")
                .age(30)
                .sex(Sex.FEMALE)
                .dateOfBirth(LocalDate.of(1993, 5, 15))
                .height(165.0)
                .weight(60.0)
                .build();

        service.add(profile1);
        service.add(profile2);
        
        assertEquals(2, service.listAll().size());
    }

    @Test
    public void testSessionManagement() {
        Profile profile = new Profile.Builder()
                .id(8)
                .name("Session User")
                .age(28)
                .sex(Sex.FEMALE)
                .dateOfBirth(LocalDate.of(1995, 3, 10))
                .height(170.0)
                .weight(65.0)
                .build();

        service.add(profile);
        
        // Initially no session
        assertFalse(service.getCurrentSession().isPresent());
        
        // Open session
        Optional<Profile> opened = service.openSession(profile.getId());
        assertTrue(opened.isPresent());
        assertEquals(profile, opened.get());
        
        // Current session should be set
        Optional<Profile> current = service.getCurrentSession();
        assertTrue(current.isPresent());
        assertEquals(profile, current.get());
        
        // Close session
        service.closeSession();
        assertFalse(service.getCurrentSession().isPresent());
    }

    @Test
    public void testUpdateUser_WithValidData_ShouldUpdateProfile() {
        // create and add an existing profile
        Profile existingProfile = new Profile.Builder()
                .id(1)
                .name("John Doe")
                .age(25)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.of(1999, 7, 22))
                .height(175.0)
                .weight(70.0)
                .unitSystem(UnitSystem.METRIC)
                .build();
        service.add(existingProfile);

        // Prepare updated data
        var rawInput = new profile.view.ISignUpView.RawInput(
            "John Smith", 
            "1999-07-22",
            "180.0", 
            "75.0",  
            "MALE",
            "METRIC"
        );

        // When
        assertDoesNotThrow(() -> {
            Profile updatedProfile = service.updateUser(1, rawInput);
            
            assertEquals(1, updatedProfile.getId());
            assertEquals("John Smith", updatedProfile.getName());
            assertEquals(26, updatedProfile.getAge()); // Corrected: 2025 - 1999 = 26 years old
            assertEquals(Sex.MALE, updatedProfile.getSex());
            assertEquals(180.0, updatedProfile.getHeight());
            assertEquals(75.0, updatedProfile.getWeight());
            assertEquals(UnitSystem.METRIC, updatedProfile.getUnitSystem());
        });
    }

    @Test
    public void testUpdateUser_WithNonExistentProfile_ShouldThrowException() {
        var rawInput = new profile.view.ISignUpView.RawInput(
            "John Smith",
            "1999-07-22",
            "180.0",
            "75.0",
            "MALE",
            "METRIC"
        );

        // When/Then
        assertThrows(IProfileService.ProfileNotFoundException.class, () -> {
            service.updateUser(999, rawInput);
        });
    }

    @Test
    public void testUpdateUser_WithInvalidData_ShouldThrowValidationException() {
        // create and add an existing profile
        Profile existingProfile = new Profile.Builder()
                .id(1)
                .name("John Doe")
                .age(25)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.of(1999, 7, 22))
                .height(175.0)
                .weight(70.0)
                .unitSystem(UnitSystem.METRIC)
                .build();
        service.add(existingProfile);

        // Prepare invalid data (empty name)
        var rawInput = new profile.view.ISignUpView.RawInput(
            "", // Invalid empty name
            "1999-07-22",
            "180.0",
            "75.0",
            "MALE",
            "METRIC"
        );

        // then
        assertThrows(IProfileService.ValidationException.class, () -> {
            service.updateUser(1, rawInput);
        });
    }

    

    
}
