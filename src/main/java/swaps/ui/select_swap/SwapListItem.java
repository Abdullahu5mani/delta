package swaps.ui.select_swap;

import swaps.models.Swap;

/**
 * Represents a swap to be rendered in the list. Implements the toString method so the swap is rendered properly.
 * @param swap The swap to select form the list.
 */
public record SwapListItem(Swap swap) {

    @Override
    public String toString() {
        String oldFoodName = swap.oldFood().getFoodDescription();
        String newFoodName = swap.newFood().getFoodDescription();
        return "%s → %s".formatted(oldFoodName, newFoodName);
    }
}
