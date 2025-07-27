package swaps.models;

import java.text.SimpleDateFormat;
import java.util.Date;

import meals.models.food.Food;

/**
 * Represents an ingredient swap with meal context information.
 * Includes the date when the original food was logged.
 *
 * @param oldFood The food to be replaced.
 * @param newFood The food to substitute.
 * @param mealDate The date when the original food was logged.
 */
public record SwapWithMealContext(Food oldFood, Food newFood, Date mealDate) {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy");
    
    /**
     * @return A formatted string representation of the meal date
     */
    public String getFormattedMealDate() {
        if (mealDate == null) {
            return "Unknown date";
        }
        
        // Check if it's today
        Date today = new Date();
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (dayFormat.format(mealDate).equals(dayFormat.format(today))) {
            return "Today";
        }
        
        // Check if it's yesterday
        Date yesterday = new Date(today.getTime() - 24 * 60 * 60 * 1000);
        if (dayFormat.format(mealDate).equals(dayFormat.format(yesterday))) {
            return "Yesterday";
        }
        
        return DATE_FORMAT.format(mealDate);
    }
    
    /**
     * Converts this SwapWithMealContext to a basic Swap.
     * @return A Swap containing just the old and new foods
     */
    public Swap toSwap() {
        return new Swap(oldFood, newFood);
    }
}
