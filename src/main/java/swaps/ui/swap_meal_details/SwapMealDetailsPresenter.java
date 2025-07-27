package swaps.ui.swap_meal_details;

import swaps.models.SwapWithMealContext;

/**
 * Presenter for the swap meal details view.
 * Handles the logic for displaying meal comparisons before and after a swap.
 */
public class SwapMealDetailsPresenter {
    private final SwapMealDetailsView view;
    private SwapWithMealContext currentSwap;

    /**
     * @param view The swap meal details view to control.
     */
    public SwapMealDetailsPresenter(SwapMealDetailsView view) {
        this.view = view;
    }

    /**
     * Sets the swap context and updates the view.
     */
    public void setSwap(SwapWithMealContext swapContext) {
        this.currentSwap = swapContext;
        view.updateSwapDetails(swapContext);
    }    /**
     * @return The currently displayed swap context
     */
    public SwapWithMealContext getCurrentSwap() {
        return currentSwap;
    }
}
