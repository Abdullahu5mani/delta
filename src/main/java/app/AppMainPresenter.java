package app;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import meals.ui.LogMealPresenter;
import meals.ui.LogMealView;
import meals.ui.MealListPresenter;
import meals.ui.MealListView;

import profile.presenter.EditProfilePresenter;
import profile.presenter.ProfileSelectorPresenter;
import profile.presenter.UserSignUpPresenter;
import profile.view.EditProfileView;
import profile.view.SignUpView;
import profile.view.SplashView;

import statistics.presenter.NutrientBreakdownPresenter;
import statistics.presenter.SwapComparisonPresenter;

import shared.navigation.INavElement;
import shared.navigation.NavItem;
import shared.navigation.NavSubMenu;
import shared.navigation.NavigationPresenter;
import shared.navigation.NavigationView;

import swaps.ui.SwapsPresenter;
import swaps.ui.SwapsView;
import swaps.ui.goals.CreateGoalsView;

/**
 * Presenter for the main UI of the app.
 */
public class AppMainPresenter {
    private static AppMainPresenter instance;

    private final AppMainView appMainView;
    private final NavigationPresenter<LeftNavItem> leftNavPresenter;

    private AppMainPresenter() {
        // navigation tree (menu structure)
        INavElement<LeftNavItem> leftNavTree = buildLeftNavTree();

        // Wire view - presenter
        NavigationView<LeftNavItem> leftNav = new NavigationView<>(leftNavTree);
        leftNavPresenter = new NavigationPresenter<>(leftNav);
        appMainView = new AppMainView(leftNav);

        // listen for navigation events
        leftNavPresenter.addNavigationListener(leftNavItem -> {
            boolean sessionActive = shared.ServiceFactory.getProfileService()
                    .getCurrentSession()
                    .isPresent();

            // block navigation to meal-related tabs when no profile is selected
            if (!sessionActive && isMealTab(leftNavItem)) {
                JOptionPane.showMessageDialog(appMainView,
                        "Please select a profile to access this feature.",
                        "No Active Profile",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // else build and render the requested view
            JComponent newView = buildView(leftNavItem);
            if (newView != null) {
                appMainView.renderCard(leftNavItem, newView);
            }
        });
    }

    /** @return {@code true} if the nav item needs an active profile/session. */
    private boolean isMealTab(LeftNavItem item) {
        return switch (item) {
            case LOG_MEAL,
                    VIEW_MULTIPLE_MEALS,
                    VIEW_SINGLE_MEAL,
                    VIEW_NUTRIENT_BREAKDOWN,
                    VIEW_SWAP_COMPARISON,
                    EXPLORE_INGREDIENT_SWAPS -> true;
            default -> false;
        };
    }

    /**
     * Constructs a new view/presenter corresponding to the given navigation item.
     * We construct a new view each time navigation happens and only have one active
     * at a time.
     */
    private JComponent buildView(LeftNavItem navItem) {
        return switch (navItem) {

            case SELECT_PROFILE -> {
                try {
                    SplashView view = new SplashView();
                    new ProfileSelectorPresenter(view, shared.ServiceFactory.getProfileService())
                            .initialize();
                    yield view;
                } catch (Exception e) {
                    System.err.println("Failed to initialize profile selector: " + e.getMessage());
                    yield new PlaceholderView("Error loading Profile Selection");
                }
            }

            case EDIT_PROFILE -> {
                try {
                    EditProfileView view = new EditProfileView();
                    new EditProfilePresenter(view, shared.ServiceFactory.getProfileService())
                            .initialize();
                    yield view;
                } catch (Exception e) {
                    System.err.println("Failed to initialize edit profile: " + e.getMessage());
                    yield new PlaceholderView("Error loading Edit Profile form");
                }
            }

            case CREATE_PROFILE -> {
                try {
                    SignUpView view = new SignUpView();
                    new UserSignUpPresenter(view, shared.ServiceFactory.getProfileService())
                            .initialize();
                    yield view;
                } catch (Exception e) {
                    System.err.println("Failed to initialize sign up panel: " + e.getMessage());
                    yield new PlaceholderView("Error loading Create Profile form");
                }
            }

            case LOG_MEAL -> initializeLogMealView();
            case VIEW_MULTIPLE_MEALS -> initializeMealListView();
            case VIEW_SINGLE_MEAL -> new PlaceholderView("Single Meal View");
            case VIEW_NUTRIENT_BREAKDOWN -> initializeNutrientBreakdownView();
            case VIEW_SWAP_COMPARISON -> initializeSwapComparisonView();
            case EXPLORE_INGREDIENT_SWAPS -> initializeSwapsView();

            default -> null;
        };
    }

    /**
     * Creates the view to log meals initialized with the corresponding presenter.
     * @return The view enabling the user to log meals through the UI.
     */
    private LogMealView initializeLogMealView() {
        LogMealView logMealView = new LogMealView();
        new LogMealPresenter(logMealView); // register event listeners
        return logMealView;
    }

    private SwapsView initializeSwapsView() {
        SwapsView swapsView = new SwapsView(new CreateGoalsView());
        new SwapsPresenter(swapsView); // register action listeners
        return swapsView;
    }

    private MealListView initializeMealListView() {
        MealListView mealListView = new MealListView();
        new MealListPresenter(mealListView);
        return mealListView;
    }

    /**
     * Creates the view for nutrient breakdown statistics with date selection UI.
     * @return The panel containing the nutrient breakdown visualization with date controls.
     */
    private JComponent initializeNutrientBreakdownView() {
        try {
            NutrientBreakdownPresenter presenter = new NutrientBreakdownPresenter();
            return presenter.createNutrientBreakdownUI();
        } catch (Exception e) {
            System.err.println("Failed to initialize nutrient breakdown view: " + e.getMessage());
            return new PlaceholderView("Error loading Nutrient Breakdown");
        }
    }

    /**
     * Creates the view for swap comparison statistics with date selection UI.
     * @return The panel containing the swap comparison visualization with date controls.
     */
    private JComponent initializeSwapComparisonView() {
        try {
            SwapComparisonPresenter presenter = new SwapComparisonPresenter();
            return presenter.createSwapComparisonUI();
        } catch (Exception e) {
            System.err.println("Failed to initialize swap comparison view: " + e.getMessage());
            return new PlaceholderView("Error loading Swap Comparison");
        }
    }

    /** @return The tree representing the menu for the left navigation bar. */
    private static INavElement<LeftNavItem> buildLeftNavTree() {
        NavSubMenu<LeftNavItem> leftNavRoot = new NavSubMenu<>(LeftNavItem.MENU_ROOT);
        leftNavRoot.addNavElement(buildLeftNavProfileSubMenu());
        leftNavRoot.addNavElement(buildLeftNavMealsSubmenu());
        leftNavRoot.addNavElement(buildLeftNavMealStatisticsSubmenu());
        leftNavRoot.addNavElement(new NavItem<>(LeftNavItem.EXPLORE_INGREDIENT_SWAPS));
        return leftNavRoot;
    }

    /** @return The profiles submenu for the left navigation bar. */
    private static INavElement<LeftNavItem> buildLeftNavProfileSubMenu() {
        NavSubMenu<LeftNavItem> profileSubMenu = new NavSubMenu<>(LeftNavItem.PROFILE_SUBMENU);
        profileSubMenu.addNavElement(new NavItem<>(LeftNavItem.SELECT_PROFILE));
        profileSubMenu.addNavElement(new NavItem<>(LeftNavItem.EDIT_PROFILE));
        profileSubMenu.addNavElement(new NavItem<>(LeftNavItem.CREATE_PROFILE));
        return profileSubMenu;
    }

    /** @return The meals submenu for the left navigation bar. */
    private static INavElement<LeftNavItem> buildLeftNavMealsSubmenu() {
        NavSubMenu<LeftNavItem> mealsSubMenu = new NavSubMenu<>(LeftNavItem.MEALS_SUBMENU);
        mealsSubMenu.addNavElement(new NavItem<>(LeftNavItem.LOG_MEAL));
        mealsSubMenu.addNavElement(new NavItem<>(LeftNavItem.VIEW_MULTIPLE_MEALS));
        mealsSubMenu.addNavElement(new NavItem<>(LeftNavItem.VIEW_SINGLE_MEAL));
        return mealsSubMenu;
    }

    /**
     * @return The meal statistics submenu for the left navigation bar.
     */
    private static INavElement<LeftNavItem> buildLeftNavMealStatisticsSubmenu() {
        NavSubMenu<LeftNavItem> statisticsSubMenu = new NavSubMenu<>(LeftNavItem.MEAL_STATISTICS_SUBMENU);
        statisticsSubMenu.addNavElement(new NavItem<>(LeftNavItem.VIEW_NUTRIENT_BREAKDOWN));
        statisticsSubMenu.addNavElement(new NavItem<>(LeftNavItem.VIEW_SWAP_COMPARISON));
        return statisticsSubMenu;
    }

    /**
     * Navigates to the specified view.
     * This method is useful if you want to force navigation without the user clicking on the navigation menu directly.
     * For example, after creating a new profile and hitting "Submit", the CreateProfilePresenter could call this method
     * to navigate to the LogMealView for example.
     *
     * @param leftNavItem The nav item for the view to navigate to.
     */
    public void navigateTo(LeftNavItem leftNavItem) {
        leftNavPresenter.navigateTo(leftNavItem);
    }

    /** @return The singleton instance controlling the main UI for the app. */
    public static AppMainPresenter instance() {
        if (instance == null) {
            instance = new AppMainPresenter();
        }
        return instance;
    }

    /**
     * Runs the application and renders the UI.
     * @param args Command line arguments (unused).
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            AppMainPresenter presenter = AppMainPresenter.instance();
            // Start with profile selection
            presenter.navigateTo(LeftNavItem.SELECT_PROFILE);
        });
    }
}
