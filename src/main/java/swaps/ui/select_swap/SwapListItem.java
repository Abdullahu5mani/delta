package swaps.ui.select_swap;

import swaps.models.SwapWithMealContext;

/**
 * Represents a swap with meal context to be rendered in the list. 
 * Implements the toString method so the swap is rendered properly with date information.
 * @param swapWithMealContext The swap with meal context to select from the list.
 */
public record SwapListItem(SwapWithMealContext swapWithMealContext) {

    @Override
    public String toString() {
        String oldFoodName = swapWithMealContext.oldFood().getFoodDescription();
        String newFoodName = swapWithMealContext.newFood().getFoodDescription();
        String mealDate = swapWithMealContext.getFormattedMealDate();
        return "%s → %s (%s)".formatted(oldFoodName, newFoodName, mealDate);
    }
}
