package profile.presenter;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import profile.model.Profile;
import profile.model.Sex;
import profile.model.UnitSystem;
import profile.service.TestProfileService;
import profile.view.IEditProfileView;

class EditProfilePresenterTest {

    private TestEditProfileView testView;
    private TestProfileService testService;
    private EditProfilePresenter presenter;

    @BeforeEach
    void setUp() {
        testView = new TestEditProfileView();
        testService = new TestProfileService();
        presenter = new EditProfilePresenter(testView, testService);
        testView.reset();
        testService.clear();
    }

    @Test
    void constructor_ShouldStoreViewAndService() {
        // Given/When
        EditProfilePresenter testPresenter = new EditProfilePresenter(testView, testService);
        
        // Then
        assertNotNull(testPresenter);
    }

    @Test
    void initialize_WithActiveSession_ShouldLoadProfileData() {
        // Given - Set up an active session
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
        testService.addProfile(existingProfile);
        testService.openSession(1); // Set active session

        // When
        presenter.initialize();

        // Then
        assertNotNull(testView.getOnSubmitCallback());
        assertNotNull(testView.getOnCancelCallback());
        assertNotNull(testView.getLoadedProfile());
        assertEquals("John Doe", testView.getLoadedProfile().getName());
    }

    @Test
    void initialize_WithNoActiveSession_ShouldShowError() {
        
        // When
        presenter.initialize();

        // Then
        assertNotNull(testView.getLastError());
        assertTrue(testView.getLastError().contains("No active user session found"));
    }

    @Test
    void handleFormSubmission_WithValidData_ShouldUpdateProfile() {
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
        testService.addProfile(existingProfile);
        testService.openSession(1);
        
        presenter.initialize();
        
        // Set updated form data
        testView.setFormInput(new IEditProfileView.EditInput(
            1,
            "John Smith", // Updated name
            "1999-07-22",
            "180.0", // Updated height
            "75.0",  // Updated weight
            "MALE",
            "METRIC"
        ));

        // When
        testView.triggerSubmit();

        // Then
        assertNotNull(testView.getLastSuccess());
        assertTrue(testView.getLastSuccess().contains("Profile updated successfully"));
        assertTrue(testView.getLastSuccess().contains("John Smith"));
        assertTrue(testView.isClosed());
        
        // Verify profile was actually updated in service
        Profile updatedProfile = testService.getById(1).orElse(null);
        assertNotNull(updatedProfile);
        assertEquals("John Smith", updatedProfile.getName());
        assertEquals(180.0, updatedProfile.getHeight());
        assertEquals(75.0, updatedProfile.getWeight());
    }

    @Test
    void handleFormSubmission_WithInvalidData_ShouldShowError() {
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
        testService.addProfile(existingProfile);
        testService.openSession(1);
        
        presenter.initialize();
        
        testView.setFormInput(new IEditProfileView.EditInput(
            1,
            "", // Invalid empty name
            "1999-07-22",
            "175.0",
            "70.0",
            "MALE",
            "METRIC"
        ));

        // When
        testView.triggerSubmit();

        // Then
        assertNotNull(testView.getLastError());
        assertTrue(testView.getLastError().contains("Full name is required"));
        assertEquals(false, testView.isClosed());
        
        // Verify profile was not changed
        Profile unchangedProfile = testService.getById(1).orElse(null);
        assertNotNull(unchangedProfile);
        assertEquals("John Doe", unchangedProfile.getName()); 
    }

   
    @Test
    void handleFormSubmission_WithNonExistentProfile_ShouldShowError() {
        // Given
        presenter.initialize();
        
        // Set form data with non-existent profile ID
        testView.setFormInput(new IEditProfileView.EditInput(
            999, // Non-existent profile ID
            "John Doe",
            "1999-07-22",
            "175.0",
            "70.0",
            "MALE",
            "METRIC"
        ));

        // When
        testView.triggerSubmit();

        // Then
        assertNotNull(testView.getLastError());
        assertTrue(testView.getLastError().contains("Profile with ID 999 not found"));
        assertEquals(false, testView.isClosed());
    }


    static class TestEditProfileView implements IEditProfileView {
        private Runnable onSubmitCallback;
        private Runnable onCancelCallback;
        private EditInput formInput;
        private String lastError;
        private String lastSuccess;
        private boolean closed = false;
        private Profile loadedProfile;

        @Override
        public void setOnSubmit(Runnable callback) {
            this.onSubmitCallback = callback;
        }

        @Override
        public void setOnCancel(Runnable callback) {
            this.onCancelCallback = callback;
        }

        @Override
        public EditInput getFormInput() {
            return formInput;
        }

        @Override
        public void showError(String msg) {
            this.lastError = msg;
        }

        @Override
        public void showSuccess(String msg) {
            this.lastSuccess = msg;
        }

        @Override
        public void close() {
            this.closed = true;
        }

        @Override
        public void loadProfileData(Profile profile) {
            this.loadedProfile = profile;
        }

        // Test helper methods
        public void setFormInput(EditInput input) {
            this.formInput = input;
        }

        public void triggerSubmit() {
            if (onSubmitCallback != null) {
                onSubmitCallback.run();
            }
        }

        public void triggerCancel() {
            if (onCancelCallback != null) {
                onCancelCallback.run();
            }
        }

        public String getLastError() {
            return lastError;
        }

        public String getLastSuccess() {
            return lastSuccess;
        }

        public boolean isClosed() {
            return closed;
        }

        public Profile getLoadedProfile() {
            return loadedProfile;
        }

        public Runnable getOnSubmitCallback() {
            return onSubmitCallback;
        }

        public Runnable getOnCancelCallback() {
            return onCancelCallback;
        }

        public void reset() {
            this.lastError = null;
            this.lastSuccess = null;
            this.closed = false;
            this.loadedProfile = null;
            this.formInput = null;
        }
    }
}
