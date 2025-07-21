package meals.models.food;

import meals.models.nutrient.Nutrient;

import java.util.List;
import java.util.Map;

/**
 * Represents a food entity with aggregation to FoodGroup.
 * Demonstrates the "has-a" relationship where Food can have a FoodGroup.
 */
public class Food {
    private Integer foodId;
    private String foodDescription;
    private FoodGroup foodGroup;
    private Map<Nutrient, Float> nutrientAmounts;
    private List<Measure> possibleMeasures;

    /**
     * Constructor with food group (aggregation).
     */
    public Food(
            Integer foodId,
            String foodDescription,
            FoodGroup foodGroup,
            Map<Nutrient, Float> nutrientAmounts,
            List<Measure> possibleMeasures
    ) {
        this.foodId = foodId;
        this.foodDescription = foodDescription;
        this.foodGroup = foodGroup;
        this.nutrientAmounts = nutrientAmounts;
        this.possibleMeasures = possibleMeasures;
    }

    // Basic getters and setters
    public Integer getFoodId() {
        return foodId;
    }

    public String getFoodDescription() {
        return foodDescription;
    }

    // Aggregation methods - Managing FoodGroup relationship
    public FoodGroup getFoodGroup() {
        return foodGroup;
    }

    // Domain logic methods
    public Float getNutrientAmount(Nutrient nutrient) {
        return nutrientAmounts.get(nutrient);
    }

    public Map<Nutrient, Float> getNutrientAmounts() {
        return nutrientAmounts;
    }

    public List<Measure> getPossibleMeasures() {
        return possibleMeasures;
    }

    @Override
    public String toString() {
        return "Food(id: %d, description: %s, group: %s)"
                .formatted(foodId, foodDescription, getFoodGroup());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Food that = (Food) obj;
        return foodId != null ? foodId.equals(that.foodId) : that.foodId == null;
    }

    @Override
    public int hashCode() {
        return foodId != null ? foodId.hashCode() : 0;
    }
}
