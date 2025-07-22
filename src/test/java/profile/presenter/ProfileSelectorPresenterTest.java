package profile.presenter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import profile.model.Profile;
import profile.model.Sex;
import profile.model.UnitSystem;
import profile.service.TestProfileService;
import profile.view.TestSplashView;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * unit test for ProfileSelectorPresenter 
 */
class ProfileSelectorPresenterTest {
    
    private TestSplashView testView;
    private TestProfileService testService;
    private ProfileSelectorPresenter presenter;
    private Profile testProfile;
    
    @BeforeEach
    void setUp() {
        testView = new TestSplashView();
        testService = new TestProfileService();
        presenter = new ProfileSelectorPresenter(testView, testService);
        
        // Create test profile
        testProfile = new Profile.Builder()
            .id(1)
            .name("Test User")
            .age(25)
            .sex(Sex.MALE)
            .dateOfBirth(LocalDate.of(1999, 1, 1))
            .height(180.0)
            .weight(75.0)
            .unitSystem(UnitSystem.METRIC)
            .build();
            
        testService.addProfile(testProfile);
        testView.reset();
        testService.closeSession(); 
    }
    
    @Test
    void initialize_ShouldLoadAndDisplayProfiles() {
        // When
        presenter.initialize();
        
        // Then
        List<Profile> displayedProfiles = testView.getDisplayedProfiles();
        assertEquals(1, displayedProfiles.size());
        assertEquals(testProfile, displayedProfiles.get(0));
    }
    
    @Test
    void userLogin_ShouldSetActiveSession() {
        presenter.initialize();
        
        // Verify no initial session
        Optional<Profile> initialSession = testService.getCurrentSession();
        assertTrue(initialSession.isEmpty(), "Should have no active session initially");
        
        // When user selects profile 
        testView.simulateProfileSelection(testProfile);
        
        // Then session should be active
        Optional<Profile> activeSession = testService.getCurrentSession();
        assertTrue(activeSession.isPresent(), "Should have active session after login");
        assertEquals(testProfile, activeSession.get(), "Active session should be the selected profile");
        assertEquals("Test User", activeSession.get().getName());
        assertEquals(Integer.valueOf(1), activeSession.get().getId());
    }
    
    @Test
    void emptyProfileList_ShouldShowError() {
        TestProfileService emptyService = new TestProfileService();
        ProfileSelectorPresenter emptyPresenter = new ProfileSelectorPresenter(testView, emptyService);
        
        emptyPresenter.initialize();
        
        assertTrue(testView.getDisplayedProfiles().isEmpty());
        assertNotNull(testView.getLastErrorMessage());
        assertTrue(testView.getLastErrorMessage().contains("No profiles found"));
    }
    
    @Test
    void multipleProfiles_ShouldDisplayAll() {
        // Given - Add another profile
        Profile profile2 = new Profile.Builder()
            .id(2)
            .name("Second User")
            .age(30)
            .sex(Sex.FEMALE)
            .dateOfBirth(LocalDate.of(1994, 6, 15))
            .height(165.0)
            .weight(60.0)
            .unitSystem(UnitSystem.METRIC)
            .build();
        testService.addProfile(profile2);
        
        presenter.initialize();
        
        List<Profile> displayedProfiles = testView.getDisplayedProfiles();
        assertEquals(2, displayedProfiles.size());
        assertTrue(displayedProfiles.contains(testProfile));
        assertTrue(displayedProfiles.contains(profile2));
    }
}
