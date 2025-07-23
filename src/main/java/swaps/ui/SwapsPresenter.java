package swaps.ui;


import meals.models.MockDataFactory;
import swaps.models.Swap;
import swaps.ui.goals.CreateGoalsPresenter;
import swaps.ui.goals.CreateGoalsView;
import swaps.ui.goals.create_goal_form.GoalsFormPresenter;
import swaps.ui.select_swap.SelectSwapPresenter;
import swaps.ui.select_swap.SelectSwapView;

import javax.swing.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * The presenter that handles the logic and manages state for navigating between different steps in the swap workflow.
 */
public class SwapsPresenter {
    protected static final String DEFINE_GOALS_CARD_ID = "DEFINE_GOALS";
    protected static final String SELECT_SWAPS_CARD_ID = "SELECT_SWAPS";
    protected static final String SWAP_STATISTICS_CARD_ID = "SWAP_STATISTICS";
    protected static final String SWAP_MEAL_DETAILS_CARD_ID = "SWAP_MEAL_DETAILS";
    protected static final String[] CARD_IDS = {
            DEFINE_GOALS_CARD_ID,
            SELECT_SWAPS_CARD_ID,
            SWAP_STATISTICS_CARD_ID,
            SWAP_MEAL_DETAILS_CARD_ID
    };

    private SelectSwapPresenter selectSwapPresenter;

    private final SwapsView view;
    private int currentCardIndex;
    private final int lastCardIndex;

    /**
     * @param view The view to manage through the presenter.
     */
    public SwapsPresenter(SwapsView view) {
        this.view = view;
        currentCardIndex = 0;
        lastCardIndex = Array.getLength(CARD_IDS) - 1;
        initDefineGoalsView();
        initSelectSwapView();
        addPreviousButtonActionListener();
        view.setPreviousButtonEnabled(false);
        addNextButtonActionListener();
        view.setNextButtonEnabled(true);
    }

    /**
     * Initializes the views/presenters for defining goals, which is the first step in the swaps process.
     * Helper method to be called in the constructor.
     */
    private void initDefineGoalsView() {
        CreateGoalsView createGoalsView = view.getDefineGoalsView();
        GoalsFormPresenter goal1Presenter = new GoalsFormPresenter(createGoalsView.getGoal1View());
        GoalsFormPresenter goal2Presenter = new GoalsFormPresenter(createGoalsView.getGoal2View());
        new CreateGoalsPresenter(createGoalsView, goal1Presenter, goal2Presenter);
    }

    /**
     * Initializes the select swap view. Helper method to be called in the constructor.
     */
    private void initSelectSwapView() {
        SelectSwapView selectSwapView = view.getSelectSwapView();
        // TO DO: create list of swaps when swaps generated instead of hard-coding
        List<Swap> mockSwapList = new ArrayList<>();
        mockSwapList.add(new Swap(
                MockDataFactory.generateMockFoods().getFirst(),
                MockDataFactory.generateMockFoods().getLast())
        );
        mockSwapList.add(new Swap(
                MockDataFactory.generateMockFoods().getLast(),
                MockDataFactory.generateMockFoods().getFirst()
        ));
        selectSwapPresenter = new SelectSwapPresenter(selectSwapView, mockSwapList);
    }

    /**
     * Defines what action is taken when the previous button is clicked.
     * Helper method to be called in the constructor.
     */
    private void addPreviousButtonActionListener() {
        view.addPreviousButtonActionListener(() -> {
            if (currentCardIndex == 0) return;
            currentCardIndex--;
            updateVisibleCardAndNavigationControls();
        });
    }

    /**
     * Defines what action is taken when the next button is clicked.
     * Helper method to be called in the constructor.
     */
    private void addNextButtonActionListener() {
        view.addNextButtonActionListener(() -> {
            if (currentCardIndex == lastCardIndex) return;
            currentCardIndex++;
            updateVisibleCardAndNavigationControls();
        });
    }

    /**
     * Handles logic for updating the UI when navigating to a new card.
     */
    private void updateVisibleCardAndNavigationControls() {
        view.setPreviousButtonEnabled(currentCardIndex > 0);
        view.setNextButtonEnabled(currentCardIndex < lastCardIndex);
        view.showCard(CARD_IDS[currentCardIndex]);
    }
}
