package meals.models.food;

/**
 * Represents a food group entity (e.g., "Dairy and Egg Products", "Fruits and fruit juices").
 * This class implements IRecord to integrate with the database layer.
 */
public class FoodGroup {
    private Integer foodGroupId;
    private String foodGroupName;

    /**
     * Constructor for creating a food group with all details.
     */
    public FoodGroup(Integer foodGroupId, String foodGroupName) {
        this.foodGroupId = foodGroupId;
        this.foodGroupName = foodGroupName;
    }

    // Getters and setters
    public Integer getFoodGroupId() {
        return foodGroupId;
    }

    public String getFoodGroupName() {
        return foodGroupName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FoodGroup that = (FoodGroup) obj;
        return foodGroupId != null ? foodGroupId.equals(that.foodGroupId) : that.foodGroupId == null;
    }

    @Override
    public int hashCode() {
        return foodGroupId != null ? foodGroupId.hashCode() : 0;
    }
}
