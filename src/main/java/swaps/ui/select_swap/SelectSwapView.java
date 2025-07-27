package swaps.ui.select_swap;


import java.util.List;
import java.util.function.Consumer;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import swaps.models.SwapWithMealContext;

/**
 * View for selecting a swap from the list of options.
 */
public class SelectSwapView extends JPanel {
    private static final String TITLE = "Select Swap From List";

    private final DefaultListModel<SwapListItem> potentialSwapListModel;
    private final JList<SwapListItem> potentialSwapList;

    public SelectSwapView() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(new JLabel(TITLE));
        JPanel swapsContainer = new JPanel();
        potentialSwapListModel = new DefaultListModel<>();
        potentialSwapList = new JList<>(potentialSwapListModel);
        swapsContainer.add(potentialSwapList);
        this.add(swapsContainer);
    }

    /**
     * Sets the list of potential swaps to render.
     * @param potentialSwaps The list of potential swaps with meal context to render.
     */
    protected void setPotentialSwaps(List<SwapWithMealContext> potentialSwaps) {
        for (SwapWithMealContext swap: potentialSwaps) {
            potentialSwapListModel.addElement(new SwapListItem(swap));
        }
    }

    /**
     * Clears all swaps from the list.
     */
    protected void clearSwaps() {
        potentialSwapListModel.clear();
    }

    /**
     * Adds a listener to be called when a swap is selected form the list.
     * @param listener The function to be called when a swap is selected form the list.
     */
    protected void addSelectionListener(Consumer<SwapListItem> listener) {
        potentialSwapList.addListSelectionListener((e) -> {
            listener.accept(potentialSwapList.getSelectedValue());
        });
    }
}
