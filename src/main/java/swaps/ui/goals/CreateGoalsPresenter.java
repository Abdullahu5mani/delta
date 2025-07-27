package swaps.ui.goals;

import swaps.ui.goals.create_goal_form.GoalsFormPresenter;


/**
 * Presenter handling state and conditional rendering for the create goals view.
 */
public class CreateGoalsPresenter {
    private CreateGoalsView createGoalsView;
    private GoalsFormPresenter goal1Presenter;
    private boolean createSecondGoal;
    private GoalsFormPresenter goal2Presenter;

    /**
     * @param createGoalsView The view to control.
     * @param goal1Presenter The presenter for the first goal form.
     * @param goal2Presenter The presenter for the second goal form.
     */
    public CreateGoalsPresenter(
            CreateGoalsView createGoalsView,
            GoalsFormPresenter goal1Presenter,
            GoalsFormPresenter goal2Presenter
    ) {
        this.createGoalsView = createGoalsView;
        this.goal1Presenter = goal1Presenter;
        this.goal2Presenter = goal2Presenter;
        createSecondGoal = false;
        goal2Presenter.setFormVisibility(createSecondGoal);
        initDefineSecondGoalCheckbox();
        addActionListeners();
    }

    /**
     * Initializes the checkbox allowing the user to specify whether they want to create a second goal.
     * Helper method to be called in the constructor.
     */
    private void initDefineSecondGoalCheckbox() {
        createGoalsView.getDefineSecondGoalCheckbox().setOneOrTwoGoalsCheckbox(createSecondGoal);
    }

    /**
     * Specifies what action to take for the interactive elements on the page.
     * Helper method to be called in the constructor.
     */
    private void addActionListeners() {
        createGoalsView.getDefineSecondGoalCheckbox().addOneOrTwoGoalsCheckboxListener(createSecondGoalFromCheckbox -> {
            createSecondGoal = createSecondGoalFromCheckbox;
            createGoalsView.setGoal2FormVisibility(createSecondGoal);
        });
    }

    /**
     * @return Whether the second goal is enabled/visible.
     */
    public boolean isSecondGoalEnabled() {
        return createSecondGoal;
    }
}
