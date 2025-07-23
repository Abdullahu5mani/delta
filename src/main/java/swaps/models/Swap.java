package swaps.models;

import meals.models.food.Food;

/**
 * Represents an ingredient swap.
 *
 * @param oldFood The food to be replaced.
 * @param newFood The food to substitute.
 */
public record Swap(Food oldFood, Food newFood) {
}
