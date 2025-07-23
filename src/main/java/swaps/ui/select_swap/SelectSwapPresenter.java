package swaps.ui.select_swap;

import swaps.models.Swap;

import java.util.List;

public class SelectSwapPresenter {
    private Swap selectedSwap;

    /**
     * @param view The select swap view to control.
     * @param potentialSwaps The list of potential swaps to select from.
     */
    public SelectSwapPresenter(SelectSwapView view, List<Swap> potentialSwaps) {
        view.addSelectionListener(swapListItem -> {
            if (swapListItem == null) return;
            selectedSwap = swapListItem.swap();
        });
        view.setPotentialSwaps(potentialSwaps);
    }

    /**
     * @return The swap selected by the user.
     */
    public Swap getSelectedSwap() {
        return selectedSwap;
    }
}
