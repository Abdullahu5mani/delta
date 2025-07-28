package swaps.ui;

import java.awt.CardLayout;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import swaps.ui.goals.CreateGoalsView;
import swaps.ui.select_swap.SelectSwapView;
import swaps.ui.swap_meal_details.SwapMealDetailsView;
import swaps.ui.swap_statistics.SwapStatisticsView;

/**
 * View that orchestrates the entire workflow for creating, selecting, and applying a swap.
 * A series of cards are presented to the user one by one for each stage of the process.
 */
public class SwapsView extends JPanel {
    private static final String NEXT_BUTTON_LABEL = "Next";
    private static final String PREVIOUS_BUTTON_LABEL = "Previous";

    private final CreateGoalsView createGoalsView;
    private SelectSwapView selectSwapView;
    private SwapMealDetailsView swapMealDetailsView;
    private SwapStatisticsView swapStatisticsView;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JButton nextButton;
    private JButton previousButton;

    /**
     * @param createGoalsView A view allowing the user to create one or two goals.
     */
    public SwapsView(CreateGoalsView createGoalsView) {
        this.createGoalsView = createGoalsView;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        initCardPanel();
        initNavigationButtons();
    }

    /**
     * Initializes the card panels representing each step in the swap workflow.
     * Helper method to be called in the constructor.
     */
    private void initCardPanel() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.add(createGoalsView, SwapsPresenter.DEFINE_GOALS_CARD_ID);
        selectSwapView = new SelectSwapView();
        cardPanel.add(selectSwapView, SwapsPresenter.SELECT_SWAPS_CARD_ID);
        swapMealDetailsView = new SwapMealDetailsView();
        cardPanel.add(swapMealDetailsView, SwapsPresenter.SWAP_MEAL_DETAILS_CARD_ID);
        swapStatisticsView = new SwapStatisticsView();
        cardPanel.add(swapStatisticsView, SwapsPresenter.SWAP_STATISTICS_CARD_ID);
        cardLayout.show(cardPanel, SwapsPresenter.DEFINE_GOALS_CARD_ID);
        this.add(cardPanel);
    }

    /**
     * Initializes the next/previous buttons.
     * Helper method to be called in the constructor.
     */
    private void initNavigationButtons() {
        JPanel buttonsSection = new JPanel();
        buttonsSection.setLayout(new FlowLayout());
        previousButton = new JButton(PREVIOUS_BUTTON_LABEL);
        buttonsSection.add(previousButton);
        nextButton = new JButton(NEXT_BUTTON_LABEL);
        buttonsSection.add(nextButton);
        this.add(buttonsSection);
    }

    /**
     * Renders the specified step in the swap workflow.
     * @param cardId The ID of the card to render.
     */
    public void showCard(String cardId) {
        cardLayout.show(cardPanel, cardId);
    }

    /**
     * @param isEnabled Whether the previous button is enabled.
     */
    public void setPreviousButtonEnabled(boolean isEnabled) {
        previousButton.setEnabled(isEnabled);
    }

    /**
     * @param isEnabled Whether the next button is enabled.
     */
    public void setNextButtonEnabled(boolean isEnabled) {
        nextButton.setEnabled(isEnabled);
    }

    /**
     * Registers a listener to be run when the previous button is clicked.
     * @param listener The function to run when the previous button is clicked.
     */
    public void addPreviousButtonActionListener(Runnable listener) {
        previousButton.addActionListener(e -> listener.run());
    }

    /**
     * Registers a listener to be run when the next button is clicked.
     * @param listener The function to run when the next button is clicked.
     */
    public void addNextButtonActionListener(Runnable listener) {
        nextButton.addActionListener(e -> listener.run());
    }

    /**
     * @return The view where the user can specify 1 or 2 goals.
     */
    public CreateGoalsView getDefineGoalsView() {
        return createGoalsView;
    }

    /**
     * @return The view where the user can select a swap.
     */
    public SelectSwapView getSelectSwapView() {
        return selectSwapView;
    }

    /**
     * @return The view where the user can see meal details and nutrition comparison.
     */
    public SwapMealDetailsView getSwapMealDetailsView() {
        return swapMealDetailsView;
    }

    /**
     * @return The view where the user can see swap statistics.
     */
    public SwapStatisticsView getSwapStatisticsView() {
        return swapStatisticsView;
    }
}
