package swaps.ui.select_swap;

import java.util.List;

import swaps.models.SwapWithMealContext;

public class SelectSwapPresenter {
    private SwapWithMealContext selectedSwap;
    private SelectSwapView view;

    /**
     * @param view The select swap view to control.
     * @param potentialSwaps The list of potential swaps with meal context to select from.
     */
    public SelectSwapPresenter(SelectSwapView view, List<SwapWithMealContext> potentialSwaps) {
        this.view = view;
        view.addSelectionListener(swapListItem -> {
            if (swapListItem == null) return;
            selectedSwap = swapListItem.swapWithMealContext();
        });
        view.setPotentialSwaps(potentialSwaps);
    }

    /**
     * Updates the list of potential swaps.
     * @param potentialSwaps The new list of potential swaps with meal context.
     */
    public void updateSwaps(List<SwapWithMealContext> potentialSwaps) {
        view.clearSwaps();
        view.setPotentialSwaps(potentialSwaps);
    }

    /**
     * @return The swap with meal context selected by the user.
     */
    public SwapWithMealContext getSelectedSwap() {
        return selectedSwap;
    }
}
