package data;

import java.util.Collection;
import java.util.Map;

/**
 * Represents a nutrient amount for a specific food.
 * This stores the actual nutritional values.
 */
public class NutrientAmount implements IRecord {
    private Integer foodId;
    private Integer nutrientId;
    private Double nutrientValue;
    private Integer nutrientSourceId;
    private String nutrientDateOfEntry; // Date field from CSV - nullable

    /**
     * Default constructor.
     */
    public NutrientAmount() {
    }

    /**
     * Constructor for basic nutrient amount.
     */
    public NutrientAmount(Integer foodId, Integer nutrientId, Double nutrientValue) {
        this.foodId = foodId;
        this.nutrientId = nutrientId;
        this.nutrientValue = nutrientValue;
    }

    /**
     * Complete constructor.
     */
    public NutrientAmount(Integer foodId, Integer nutrientId, Double nutrientValue, 
                         Integer nutrientSourceId) {
        this.foodId = foodId;
        this.nutrientId = nutrientId;
        this.nutrientValue = nutrientValue;
        this.nutrientSourceId = nutrientSourceId;
    }

    /**
     * Constructor for creating a NutrientAmount from database record data.
     */
    public NutrientAmount(IRecord record) {
        this.foodId = (Integer) record.getValue("FoodID");
        this.nutrientId = (Integer) record.getValue("NutrientID");
        this.nutrientValue = (Double) record.getValue("NutrientValue");
        this.nutrientSourceId = (Integer) record.getValue("NutrientSourceID");
        this.nutrientDateOfEntry = (String) record.getValue("NutrientDateOfEntry"); // Nullable date field
    }

    // Getters and setters
    public Integer getFoodId() {
        return foodId;
    }

    public void setFoodId(Integer foodId) {
        this.foodId = foodId;
    }

    public Integer getNutrientId() {
        return nutrientId;
    }

    public void setNutrientId(Integer nutrientId) {
        this.nutrientId = nutrientId;
    }

    public Double getNutrientValue() {
        return nutrientValue;
    }

    public void setNutrientValue(Double nutrientValue) {
        this.nutrientValue = nutrientValue;
    }

    public Integer getNutrientSourceId() {
        return nutrientSourceId;
    }

    public void setNutrientSourceId(Integer nutrientSourceId) {
        this.nutrientSourceId = nutrientSourceId;
    }

    // Domain logic methods
    public boolean hasSignificantValue() {
        return nutrientValue != null && nutrientValue > 0;
    }

    public boolean hasQualityData() {
        return true; // No longer based on numberOfObservations
    }

    public Double getValueForServing(Double conversionFactor) {
        if (nutrientValue == null || conversionFactor == null) return null;
        return nutrientValue * conversionFactor;
    }

    // IRecord implementation
    @Override
    public Object getValue(String field) {
        return switch (field) {
            case "FoodID" -> foodId;
            case "NutrientID" -> nutrientId;
            case "NutrientValue" -> nutrientValue;
            case "NutrientSourceID" -> nutrientSourceId;
            case "NutrientDateOfEntry" -> nutrientDateOfEntry; // Date field - nullable
            default -> null;
        };
    }

    @Override
    public Collection<String> fieldNames() {
        return Map.of("FoodID", foodId, "NutrientID", nutrientId, 
                     "NutrientValue", nutrientValue, "NutrientSourceID", nutrientSourceId, "NutrientDateOfEntry", nutrientDateOfEntry).keySet();
    }

    @Override
    public String toString() {
        return "NutrientAmount(foodId: %d, nutrientId: %d, value: %s)"
            .formatted(foodId, nutrientId, nutrientValue);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        NutrientAmount that = (NutrientAmount) obj;
        return foodId != null && foodId.equals(that.foodId) &&
               nutrientId != null && nutrientId.equals(that.nutrientId);
    }

    @Override
    public int hashCode() {
        int result = foodId != null ? foodId.hashCode() : 0;
        result = 31 * result + (nutrientId != null ? nutrientId.hashCode() : 0);
        return result;
    }
} 