package swaps.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import meals.models.food.Food;
import meals.models.meal.Meal;
import meals.models.meal.MealItem;
import meals.models.nutrient.Nutrient;
import meals.services.QueryFoodsService;
import meals.services.QueryMealsService;
import swaps.models.Goal;
import swaps.models.Swap;
import swaps.models.SwapWithMealContext;
import swaps.ui.goals.create_goal_form.form_fields.DropdownOptionGoalDirection;

/**
 * Service for generating potential food swaps based on nutritional goals.
 */
public class SwapGenerationService {
    private static SwapGenerationService instance;
    private final GoalValidationService goalValidationService;
    
    private SwapGenerationService() {
        this.goalValidationService = GoalValidationService.instance();
    }
    
    public static SwapGenerationService instance() {
        if (instance == null) {
            instance = new SwapGenerationService();
        }
        return instance;
    }

    /**
     * Generates potential swaps based on the provided goals.
     * 
     * @param goals List of goals to optimize for
     * @return SwapGenerationResult containing potential swaps or validation errors
     */
    public SwapGenerationResult generateSwaps(List<Goal> goals) {
        // Use current date for backward compatibility
        Date today = new Date();
        return generateSwapsForDate(goals, today);
    }

    /**
     * Generates potential swaps based on the provided goals for foods logged on a specific date.
     * Only suggests swaps for foods that the user has actually logged in their meals.
     * 
     * @param goals List of goals to optimize for
     * @param date The date to get logged meals for
     * @return SwapGenerationResult containing potential swaps or validation errors
     */
    public SwapGenerationResult generateSwapsForDate(List<Goal> goals, Date date) {
        // Validate goals first
        GoalValidationService.GoalValidationResult validationResult = goalValidationService.validateGoals(goals);
        if (validationResult.hasErrors()) {
            return new SwapGenerationResult(Collections.emptyList(), validationResult.getErrors());
        }

        try {
            // Get user's logged meals for the specified date
            List<Food> loggedFoods = getUserLoggedFoods(date);
            
            if (loggedFoods.isEmpty()) {
                List<String> errors = List.of("No meals logged for the selected date. Please log some meals first before generating swaps.");
                return new SwapGenerationResult(Collections.emptyList(), errors);
            }
            
            // Get all available foods for finding alternatives
            List<Food> allFoods = QueryFoodsService.instance().fetchAll();
            
            // Generate potential swaps only for logged foods
            List<Swap> potentialSwaps = new ArrayList<>();
            
            // For each logged food, find better alternatives based on goals
            for (Food loggedFood : loggedFoods) {
                List<Swap> swapsForFood = findBetterAlternatives(loggedFood, allFoods, goals);
                potentialSwaps.addAll(swapsForFood);
            }
            
            // Sort swaps by effectiveness and limit results
            List<Swap> topSwaps = potentialSwaps.stream()
                    .distinct()
                    .sorted((s1, s2) -> compareSwapEffectiveness(s1, s2, goals))
                    .limit(10) // Limit to top 10 swaps
                    .collect(Collectors.toList());
            
            // Ensure we have at least one swap option
            if (topSwaps.isEmpty()) {
                List<String> errors = List.of("No suitable food swaps found for your logged meals with the specified goals");
                return new SwapGenerationResult(Collections.emptyList(), errors);
            }
            
            return new SwapGenerationResult(topSwaps, Collections.emptyList());
            
        } catch (QueryFoodsService.QueryFoodsServiceException e) {
            List<String> errors = List.of("Error accessing food database: " + e.getMessage());
            return new SwapGenerationResult(Collections.emptyList(), errors);
        } catch (Exception e) {
            List<String> errors = List.of("Error generating swaps: " + e.getMessage());
            return new SwapGenerationResult(Collections.emptyList(), errors);
        }
    }

    /**
     * Generates potential food swaps for a specific date with meal context information.
     * 
     * @param goals The list of nutritional goals to optimize for
     * @param date The date to get logged meals for
     * @return SwapWithMealContextResult containing potential swaps with meal dates or validation errors
     */
    public SwapWithMealContextResult generateSwapsWithMealContextForDate(List<Goal> goals, Date date) {
        // Validate goals first
        GoalValidationService.GoalValidationResult validationResult = goalValidationService.validateGoals(goals);
        if (validationResult.hasErrors()) {
            return new SwapWithMealContextResult(Collections.emptyList(), validationResult.getErrors());
        }

        try {
            // Get user's logged meals for the specified date
            List<Meal> meals = getUserLoggedMeals(date);
            
            if (meals.isEmpty()) {
                List<String> errors = List.of("No meals logged for the selected date. Please log some meals first before generating swaps.");
                return new SwapWithMealContextResult(Collections.emptyList(), errors);
            }
            
            // Get all available foods for finding alternatives
            List<Food> allFoods = QueryFoodsService.instance().fetchAll();
            
            // Generate potential swaps with meal context
            List<SwapWithMealContext> potentialSwaps = new ArrayList<>();
            
            // For each meal, find better alternatives for its foods
            for (Meal meal : meals) {
                Date mealDate = meal.getCreatedAt();
                for (MealItem item : meal.getMealItems()) {
                    Food loggedFood = item.getFood();
                    List<SwapWithMealContext> swapsForFood = findBetterAlternativesWithContext(loggedFood, allFoods, goals, mealDate);
                    potentialSwaps.addAll(swapsForFood);
                }
            }
            
            // Sort swaps by effectiveness and limit results
            List<SwapWithMealContext> topSwaps = potentialSwaps.stream()
                    .distinct()
                    .sorted((s1, s2) -> compareSwapEffectiveness(s1.toSwap(), s2.toSwap(), goals))
                    .limit(10) // Limit to top 10 swaps
                    .collect(Collectors.toList());
            
            // Ensure we have at least one swap option
            if (topSwaps.isEmpty()) {
                List<String> errors = List.of("No suitable food swaps found for your logged meals with the specified goals");
                return new SwapWithMealContextResult(Collections.emptyList(), errors);
            }
            
            return new SwapWithMealContextResult(topSwaps, Collections.emptyList());
            
        } catch (QueryFoodsService.QueryFoodsServiceException e) {
            List<String> errors = List.of("Error accessing food database: " + e.getMessage());
            return new SwapWithMealContextResult(Collections.emptyList(), errors);
        } catch (Exception e) {
            List<String> errors = List.of("Error generating swaps: " + e.getMessage());
            return new SwapWithMealContextResult(Collections.emptyList(), errors);
        }
    }

    /**
     * Generates potential food swaps for a date range with meal context information.
     * 
     * @param goals The list of nutritional goals to optimize for
     * @param fromDate The start date of the range (inclusive)
     * @param toDate The end date of the range (inclusive)
     * @return SwapWithMealContextResult containing potential swaps with meal dates or validation errors
     */
    public SwapWithMealContextResult generateSwapsWithMealContextForDateRange(List<Goal> goals, Date fromDate, Date toDate) {
        // Validate goals first
        GoalValidationService.GoalValidationResult validationResult = goalValidationService.validateGoals(goals);
        if (validationResult.hasErrors()) {
            return new SwapWithMealContextResult(Collections.emptyList(), validationResult.getErrors());
        }

        try {
            // Get user's logged meals for the specified date range
            List<Meal> meals = getUserLoggedMealsForDateRange(fromDate, toDate);
            
            if (meals.isEmpty()) {
                List<String> errors = List.of("No meals logged for the selected date range. Please log some meals first before generating swaps.");
                return new SwapWithMealContextResult(Collections.emptyList(), errors);
            }
            
            // Get all available foods for finding alternatives
            List<Food> allFoods = QueryFoodsService.instance().fetchAll();
            
            // Generate potential swaps with meal context
            List<SwapWithMealContext> potentialSwaps = new ArrayList<>();
            
            // For each meal, find better alternatives for its foods
            for (Meal meal : meals) {
                Date mealDate = meal.getCreatedAt();
                for (MealItem mealItem : meal.getMealItems()) {
                    Food oldFood = mealItem.getFood();
                    List<SwapWithMealContext> alternatives = findBetterAlternativesWithContext(oldFood, allFoods, goals, mealDate);
                    
                    potentialSwaps.addAll(alternatives);
                }
            }
            
            // Remove duplicates and limit results
            List<SwapWithMealContext> uniqueSwaps = potentialSwaps.stream()
                    .distinct()
                    .limit(50) // Limit to top 50 swaps 
                    .collect(Collectors.toList());
            
            if (uniqueSwaps.isEmpty()) {
                List<String> errors = List.of("No suitable food swaps found for your logged meals in the selected date range.");
                return new SwapWithMealContextResult(Collections.emptyList(), errors);
            }
            
            return new SwapWithMealContextResult(uniqueSwaps, Collections.emptyList());
            
        } catch (QueryFoodsService.QueryFoodsServiceException e) {
            List<String> errors = List.of("Error accessing food database: " + e.getMessage());
            return new SwapWithMealContextResult(Collections.emptyList(), errors);
        } catch (Exception e) {
            List<String> errors = List.of("Error generating swaps: " + e.getMessage());
            return new SwapWithMealContextResult(Collections.emptyList(), errors);
        }
    }

    /**
     * Gets the list of foods that the user has logged on the specified date.
     * 
     * @param date The date to get logged meals for
     * @return List of unique foods from the user's logged meals
     */
    private List<Food> getUserLoggedFoods(Date date) {
        try {
            QueryMealsService.QueryMealsServiceOutput mealsOutput = getMealsForDate(date);
            
            if (!mealsOutput.ok()) {
                return Collections.emptyList();
            }
            
            // Extract unique foods from all meals
            List<Food> loggedFoods = new ArrayList<>();
            for (Meal meal : mealsOutput.getMeals()) {
                for (MealItem mealItem : meal.getMealItems()) {
                    Food food = mealItem.getFood();
                    // Add only if not already in the list (avoid duplicates)
                    if (loggedFoods.stream().noneMatch(f -> f.getFoodId().equals(food.getFoodId()))) {
                        loggedFoods.add(food);
                    }
                }
            }
            
            return loggedFoods;
            
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * Gets the list of meals that the user has logged on the specified date.
     * 
     * @param date The date to get logged meals for
     * @return List of meals from the user's logged meals
     */
    private List<Meal> getUserLoggedMeals(Date date) {
        try {
            QueryMealsService.QueryMealsServiceOutput mealsOutput = getMealsForDate(date);
            
            if (!mealsOutput.ok()) {
                return Collections.emptyList();
            }
            
            return mealsOutput.getMeals();
            
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * Gets the list of meals that the user has logged for the specified date range.
     * 
     * @param fromDate The start date of the range (inclusive)
     * @param toDate The end date of the range (inclusive)
     * @return List of meals from the user's logged meals
     */
    private List<Meal> getUserLoggedMealsForDateRange(Date fromDate, Date toDate) {
        try {
            QueryMealsService.QueryMealsServiceOutput mealsOutput = 
                QueryMealsService.instance().getMealsByDate(fromDate, toDate);
            
            if (!mealsOutput.ok()) {
                return Collections.emptyList();
            }
            
            return mealsOutput.getMeals();
            
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * Helper method to get meals for a specific date with proper date range calculation.
     */
    private QueryMealsService.QueryMealsServiceOutput getMealsForDate(Date date) {
        // Get meals for the specified date (from start to end of day)
        Date startOfDay = new Date(date.getTime());
        Date endOfDay = new Date(date.getTime() + 24 * 60 * 60 * 1000 - 1); // End of day
        
        return QueryMealsService.instance().getMealsByDate(startOfDay, endOfDay);
    }

    /**
     * Finds better food alternatives for a given food based on goals.
     */
    private List<Swap> findBetterAlternatives(Food oldFood, List<Food> allFoods, List<Goal> goals) {
        return findBetterAlternativesWithContext(oldFood, allFoods, goals, null).stream()
                .map(SwapWithMealContext::toSwap)
                .collect(Collectors.toList());
    }
    
    /**
     * Finds better food alternatives for a given food based on goals, with meal context.
     */
    private List<SwapWithMealContext> findBetterAlternativesWithContext(Food oldFood, List<Food> allFoods, List<Goal> goals, Date mealDate) {
        List<SwapWithMealContext> swaps = new ArrayList<>();
        
        for (Food newFood : allFoods) {
            if (oldFood.getFoodId().equals(newFood.getFoodId())) {
                continue; // Skip same food
            }
            
            // Check if this swap would improve the goals
            if (isSwapBeneficial(oldFood, newFood, goals)) {
                swaps.add(new SwapWithMealContext(oldFood, newFood, mealDate));
            }
        }
        
        return swaps;
    }
    
    /**
     * Determines if swapping oldFood for newFood would be beneficial for the goals.
     */
    private boolean isSwapBeneficial(Food oldFood, Food newFood, List<Goal> goals) {
        Map<Nutrient, Float> oldNutrients = oldFood.getNutrientAmounts();
        Map<Nutrient, Float> newNutrients = newFood.getNutrientAmounts();
        
        int improvements = 0;
        int worsening = 0;
        
        for (Goal goal : goals) {
            Nutrient nutrient = goal.getNutrient();
            
            float oldAmount = oldNutrients.getOrDefault(nutrient, 0.0f);
            float newAmount = newNutrients.getOrDefault(nutrient, 0.0f);
            
            float difference = newAmount - oldAmount;
            
            if (goal.getDirection() == DropdownOptionGoalDirection.INCREASE) {
                if (difference > 0) {
                    improvements++;
                } else if (difference < 0) {
                    worsening++;
                }
            } else { // DECREASE
                if (difference < 0) {
                    improvements++;
                } else if (difference > 0) {
                    worsening++;
                }
            }
        }
        
        // Swap is beneficial if it improves more goals than it worsens
        return improvements > worsening && improvements > 0;
    }
    
    /**
     * Compares two swaps to determine which is more effective for the goals.
     * Returns negative if s1 is better, positive if s2 is better, 0 if equal.
     */
    private int compareSwapEffectiveness(Swap s1, Swap s2, List<Goal> goals) {
        double s1Score = calculateSwapScore(s1, goals);
        double s2Score = calculateSwapScore(s2, goals);
        
        return Double.compare(s2Score, s1Score); // Higher score is better
    }
    
    /**
     * Calculates a score for how well a swap achieves the goals.
     */
    private double calculateSwapScore(Swap swap, List<Goal> goals) {
        Map<Nutrient, Float> oldNutrients = swap.oldFood().getNutrientAmounts();
        Map<Nutrient, Float> newNutrients = swap.newFood().getNutrientAmounts();
        
        double totalScore = 0.0;
        
        for (Goal goal : goals) {
            Nutrient nutrient = goal.getNutrient();
            
            float oldAmount = oldNutrients.getOrDefault(nutrient, 0.0f);
            float newAmount = newNutrients.getOrDefault(nutrient, 0.0f);
            
            float difference = newAmount - oldAmount;
            
            // Score based on goal direction and magnitude
            if (goal.getDirection() == DropdownOptionGoalDirection.INCREASE) {
                totalScore += Math.max(0, difference) * goal.getTargetAmount();
            } else { // DECREASE
                totalScore += Math.max(0, -difference) * goal.getTargetAmount();
            }
        }
        
        return totalScore;
    }
    
        /**
     * Result class containing swap generation results with meal context.
     */
    public static class SwapWithMealContextResult {
        private final List<SwapWithMealContext> swaps;
        private final List<String> errors;
        
        public SwapWithMealContextResult(List<SwapWithMealContext> swaps, List<String> errors) {
            this.swaps = swaps;
            this.errors = errors;
        }
        
        public List<SwapWithMealContext> getSwaps() {
            return swaps;
        }
        
        public List<String> getErrors() {
            return errors;
        }
        
        public boolean hasErrors() {
            return !errors.isEmpty();
        }
        
        public boolean isSuccessful() {
            return errors.isEmpty() && !swaps.isEmpty();
        }
    }

    /**
     * Result class containing swap generation results.
     */
    public static class SwapGenerationResult {
        private final List<Swap> swaps;
        private final List<String> errors;
        
        public SwapGenerationResult(List<Swap> swaps, List<String> errors) {
            this.swaps = swaps;
            this.errors = errors;
        }
        
        public List<Swap> getSwaps() {
            return swaps;
        }
        
        public List<String> getErrors() {
            return errors;
        }
        
        public boolean hasErrors() {
            return !errors.isEmpty();
        }
        
        public boolean isSuccessful() {
            return errors.isEmpty() && !swaps.isEmpty();
        }
    }
}
