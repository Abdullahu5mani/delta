package profile.presenter;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import profile.model.Profile;
import profile.model.Sex;
import profile.model.UnitSystem;
import profile.service.TestProfileService;
import profile.view.ISignUpView;
import profile.view.TestSignUpView;

class UserSignUpPresenterTest {

    private TestSignUpView testView;
    private TestProfileService testService;
    private UserSignUpPresenter presenter;

    @BeforeEach
    void setUp() {
        testView = new TestSignUpView();
        testService = new TestProfileService();
        presenter = new UserSignUpPresenter(testView, testService);
        testView.reset();
        testService.clear();
    }

    @Test
    void constructor_ShouldStoreViewAndService() {
        // Given/When
        UserSignUpPresenter testPresenter = new UserSignUpPresenter(testView, testService);
        
        // Then
        assertNotNull(testPresenter);
    }

    @Test
    void initialize_ShouldSetViewCallback() {
        // When
        presenter.initialize();


        testView.triggerSubmit();
        
        // Should either succeed or show validation errors (depending on form data)
        assertTrue(testView.isClosed() || testView.getLastError() != null || testService.size() > 0);
    }

    @Test
    void handleFormSubmission_WithValidInput_ShouldCreateProfile() {
        // Given
        presenter.initialize();
        testView.setFormInput(new ISignUpView.RawInput(
            "John Doe",
            "1999-07-01",  
            "175.0",
            "70.0",
            "MALE",
            "METRIC"
        ));

        // When
        testView.triggerSubmit();
        assertEquals(1, testService.size());
        assertTrue(testView.isClosed());
        assertNull(testView.getLastError());
    }

    @Test
    void testSuccessfulSubmission() {
        // Arrange
        presenter.initialize();
        
        // Act
        testView.triggerSubmit();
        
        // Assert
        assertEquals("Profile created successfully for John Doe!", testView.getLastSuccess());
        assertTrue(testView.isClosed());
        assertEquals(1, testService.size());
        assertNull(testView.getLastError());
    }

    @Test
    void handleFormSubmission_WithEmptyName_ShouldShowError() {
        // Given
        presenter.initialize();
        testView.setFormInput(new ISignUpView.RawInput(
            "",  // Empty name
            "1999-07-01",  // Fixed: consistent age/DOB
            "175.0",
            "70.0",
            "MALE",
            "METRIC"
        ));

        // When
        testView.triggerSubmit();

        // Then
        assertEquals(0, testService.size());
        assertFalse(testView.isClosed());
        assertNotNull(testView.getLastError());
        assertTrue(testView.getLastError().contains("Full name is required"));
    }

    @Test
    void handleFormSubmission_WithInvalidDateFormat_ShouldShowError() {
        // Given
        presenter.initialize();
        testView.setFormInput(new ISignUpView.RawInput(
            "John Doe",
            "invalid-date",  
            "175.0", 
            "70.0",
            "MALE",
            "METRIC"
        ));

        // When
        testView.triggerSubmit();

        // Then
        assertEquals(0, testService.size());
        assertFalse(testView.isClosed());
        assertNotNull(testView.getLastError());
        assertTrue(testView.getLastError().contains("Date of birth must be in YYYY-MM-DD format") || 
                   testView.getLastError().contains("Invalid date format"));
    }

    @Test
    void handleFormSubmission_WithFutureDateOfBirth_ShouldShowError() {
        // Given
        presenter.initialize();
        testView.setFormInput(new ISignUpView.RawInput(
            "John Doe",
            "2030-01-15",  // Future date
            "175.0",
            "70.0",
            "MALE",
            "METRIC"
        ));       
        testView.triggerSubmit();

        // Then
        assertEquals(0, testService.size());
        assertFalse(testView.isClosed());
        assertNotNull(testView.getLastError());
        assertTrue(testView.getLastError().contains("Date of birth cannot be in the future"));
    }

    @Test
    void handleFormSubmission_WithDuplicateName_ShouldShowError() {
        // Given
        Profile existingProfile = new Profile.Builder()
            .id(1)
            .name("John Doe")
            .age(30)
            .sex(Sex.MALE)
            .dateOfBirth(LocalDate.of(1994, 7, 1))  // Fixed: age 30 with consistent DOB
            .height(180.0)
            .weight(75.0)
            .unitSystem(UnitSystem.METRIC)
            .build();
        testService.addProfile(existingProfile);

        presenter.initialize();
        testView.setFormInput(new ISignUpView.RawInput(
            "john doe",  
            "1999-07-01",  
            "175.0",
            "70.0", 
            "MALE",
            "METRIC"
        ));

        // When
        testView.triggerSubmit();

        // Then
        assertEquals(1, testService.size()); // Only the original profile
        assertFalse(testView.isClosed());
        assertNotNull(testView.getLastError());
        assertTrue(testView.getLastError().contains("already exists"));
    }

    @Test
    void handleFormSubmission_WithServiceException_ShouldShowError() {
        // Given
        presenter.initialize();
        // Set exception to happen when listAll is called (for duplicate check)
        testService.setShouldThrowException(true);
        testService.setExceptionMessage("Database connection failed");
        
        testView.setFormInput(new ISignUpView.RawInput(
            "John Doe",
            "1999-07-01",  
            "175.0",
            "70.0",
            "MALE",
            "METRIC"
        ));

        // When
        testView.triggerSubmit();

        // Then
        assertEquals(0, testService.size());
        assertFalse(testView.isClosed());
        assertNotNull(testView.getLastError());
        assertTrue(testView.getLastError().contains("error") || testView.getLastError().contains("Database connection failed"));
    }

    @Test
    void handleFormSubmission_WithInvalidHeightForMetric_ShouldShowError() {
        // Given
        presenter.initialize();
        testView.setFormInput(new ISignUpView.RawInput(
            "John Doe",
            "1999-07-01", 
            "500.0",  
            "70.0",
            "MALE",
            "METRIC"
        ));

        // When
        testView.triggerSubmit();

        // Then
        assertEquals(0, testService.size());
        assertFalse(testView.isClosed());
        assertNotNull(testView.getLastError());
        assertTrue(testView.getLastError().contains("Height must be between 30 and 300 cm"));
    }

    @Test
    void handleFormSubmission_WithInvalidHeightForImperial_ShouldShowError() {
        // Given
        presenter.initialize();
        testView.setFormInput(new ISignUpView.RawInput(
            "John Doe",
            "1999-07-01",  
            "20.0",  
            "150.0",
            "MALE",
            "IMPERIAL"
        ));

        // When
        testView.triggerSubmit();

        // Then
        assertEquals(0, testService.size());
        assertFalse(testView.isClosed());
        assertNotNull(testView.getLastError());
        assertTrue(testView.getLastError().contains("Height must be between 1 and 10 feet"));
    }
}
