package app;

/**
 * Identifiers for each item in the left navigation menu.
 */
public enum LeftNavItem {

    MENU_ROOT("Menu Root - Not Visible"),

    PROFILE_SUBMENU("Profiles"),
    SELECT_PROFILE("Select Profile"),
    EDIT_PROFILE("Edit Profile"),
    CREATE_PROFILE("Create Profile"),

    MEALS_SUBMENU("Meals"),
    LOG_MEAL("Log meal"),
    VIEW_MULTIPLE_MEALS("View Multiple Meals"),
    VIEW_SINGLE_MEAL("View Single Meal"),

    MEAL_STATISTICS_SUBMENU("Meal Statistics"),
    VIEW_NUTRIENT_BREAKDOWN("View Nutrient Breakdown"),
    VIEW_FOOD_GUIDE_ALIGNMENT("Food Guide Alignment"),
    EXPLORE_INGREDIENT_SWAPS("Explore Swaps");

    private final String label;

    /**
     * @param label The visual representation of the navigation item used for rendering.
     */
    LeftNavItem(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
