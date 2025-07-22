package data;

import java.util.Collection;
import java.util.Map;

/**
 * Represents a food group entity (e.g., "Dairy and Egg Products", "Fruits and fruit juices").
 * This class implements IRecord to integrate with the database layer.
 */
public class FoodGroup implements IRecord {
    private Integer foodGroupId;
    private Integer foodGroupCode;
    private String foodGroupName;
    private String foodGroupNameF; // French name

    /**
     * Default constructor.
     */
    public FoodGroup() {
    }

    /**
     * Constructor for creating a food group with all details.
     */
    public FoodGroup(Integer foodGroupId, Integer foodGroupCode, String foodGroupName, String foodGroupNameF) {
        this.foodGroupId = foodGroupId;
        this.foodGroupCode = foodGroupCode;
        this.foodGroupName = foodGroupName;
        this.foodGroupNameF = foodGroupNameF;
    }

    /**
     * Constructor for creating a FoodGroup from database record data.
     */
    public FoodGroup(IRecord record) {
        this.foodGroupId = (Integer) record.getValue("FoodGroupID");
        this.foodGroupCode = (Integer) record.getValue("FoodGroupCode");
        this.foodGroupName = (String) record.getValue("FoodGroupName");
        this.foodGroupNameF = null; // No French field in database
    }

    // Getters and setters
    public Integer getFoodGroupId() {
        return foodGroupId;
    }

    public void setFoodGroupId(Integer foodGroupId) {
        this.foodGroupId = foodGroupId;
    }

    public Integer getFoodGroupCode() {
        return foodGroupCode;
    }

    public void setFoodGroupCode(Integer foodGroupCode) {
        this.foodGroupCode = foodGroupCode;
    }

    public String getFoodGroupName() {
        return foodGroupName;
    }

    public void setFoodGroupName(String foodGroupName) {
        this.foodGroupName = foodGroupName;
    }

    public String getFoodGroupNameF() {
        return foodGroupNameF;
    }

    public void setFoodGroupNameF(String foodGroupNameF) {
        this.foodGroupNameF = foodGroupNameF;
    }

    // Domain logic methods
    public String getDisplayName() {
        return foodGroupName != null ? foodGroupName : "Unknown Food Group";
    }

    public boolean isProteinGroup() {
        if (foodGroupName == null) return false;
        String name = foodGroupName.toLowerCase();
        return name.contains("meat") || name.contains("poultry") || name.contains("fish") || 
               name.contains("dairy") || name.contains("egg") || name.contains("legume");
    }

    public boolean isPlantBased() {
        if (foodGroupName == null) return false;
        String name = foodGroupName.toLowerCase();
        return name.contains("fruit") || name.contains("vegetable") || name.contains("grain") || 
               name.contains("cereal") || name.contains("nut") || name.contains("seed") ||
               name.contains("legume") || name.contains("spice") || name.contains("herb");
    }

    // IRecord implementation
    @Override
    public Object getValue(String field) {
        return switch (field) {
            case "FoodGroupID" -> foodGroupId;
            case "FoodGroupCode" -> foodGroupCode;
            case "FoodGroupName" -> foodGroupName;
            // No FoodGroupNameF - French field removed from database
            default -> null;
        };
    }

    @Override
    public Collection<String> fieldNames() {
        // Only include essential fields for CSV import
        return Map.of("FoodGroupID", foodGroupId, "FoodGroupCode", foodGroupCode, 
                     "FoodGroupName", foodGroupName).keySet();
    }

    @Override
    public String toString() {
        return "FoodGroup(id: %d, name: %s)".formatted(foodGroupId, foodGroupName);
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