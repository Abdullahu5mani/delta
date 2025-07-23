package swaps.ui.goals;

import swaps.ui.goals.create_goal_form.form_fields.FormFieldDefineSecondGoal;
import swaps.ui.goals.create_goal_form.GoalsFormView;

import javax.swing.*;

/**
 * View for specifying 1 or 2 goals to generate swaps for.
 */
public class CreateGoalsView extends JPanel {
    private static final String GOAL_1_HEADER = "Goal 1";
    private static final String GOAL_2_HEADER = "Goal 2";

    private final FormFieldDefineSecondGoal defineSecondGoalCheckbox;
    private final GoalsFormView goal1View;
    private final GoalsFormView goal2View;

    public CreateGoalsView() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        // goal 1
        goal1View = new GoalsFormView(GOAL_1_HEADER);
        this.add(goal1View);
        // define second goal checkbox
        defineSecondGoalCheckbox = new FormFieldDefineSecondGoal();
        this.add(defineSecondGoalCheckbox);
        // goal 2
        goal2View = new GoalsFormView(GOAL_2_HEADER);
        this.add(goal2View);
    }

    /**
     * Controls whether the form for the second goal is visible.
     * @param isVisible Whether the second goal form should be rendered.
     */
    public void setGoal2FormVisibility(boolean isVisible) {
        goal2View.setVisible(isVisible);
    }

    /**
     * @return The checkbox that allows the user to specify whether they want to define a second goal.
     */
    public FormFieldDefineSecondGoal getDefineSecondGoalCheckbox() {
        return defineSecondGoalCheckbox;
    }

    /**
     * @return The form representing the first goal.
     */
    public GoalsFormView getGoal1View() {
        return goal1View;
    }

    /**
     * @return The form representing the optional second goal.
     */
    public GoalsFormView getGoal2View() {
        return goal2View;
    }
}
