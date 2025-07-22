package data;

import java.util.Collection;
import java.util.Map;

/**
 * Represents a food entity with aggregation to FoodGroup.
 * Demonstrates the "has-a" relationship where Food can have a FoodGroup.
 */
public class Food implements IRecord {
    private Integer foodId;
    private Integer foodCode;
    private Integer foodGroupId;
    private Integer foodSourceId;
    private String foodDescription;
    private String foodDescriptionF; // French description
    private String foodDateOfEntry;   // Date fields from CSV - nullable
    private String foodDateOfPublication; // Date fields from CSV - nullable
    private String countryCode;
    private String scientificName;
    
    // Aggregation - Food has a FoodGroup (optional, food group exists independently)
    private FoodGroup assignedFoodGroup;

    /**
     * Default constructor.
     */
    public Food() {
    }

    /**
     * Constructor for basic food without food group.
     */
    public Food(Integer foodId, Integer foodCode, String foodDescription) {
        this.foodId = foodId;
        this.foodCode = foodCode;
        this.foodDescription = foodDescription;
        this.assignedFoodGroup = null;
    }

    /**
     * Constructor with food group (aggregation).
     */
    public Food(Integer foodId, Integer foodCode, String foodDescription, FoodGroup foodGroup) {
        this(foodId, foodCode, foodDescription);
        assignFoodGroup(foodGroup);
    }

    /**
     * Constructor for creating a Food from database record data.
     */
    public Food(IRecord record) {
        this.foodId = (Integer) record.getValue("FoodID");
        this.foodCode = (Integer) record.getValue("FoodCode");
        this.foodGroupId = (Integer) record.getValue("FoodGroupID");
        this.foodSourceId = (Integer) record.getValue("FoodSourceID");
        this.foodDescription = (String) record.getValue("FoodDescription");
        this.foodDescriptionF = (String) record.getValue("FoodDescriptionF"); // Nullable French field
        
        // Convert empty date strings to null for MySQL DATE compatibility
        String entryDate = (String) record.getValue("FoodDateOfEntry");
        this.foodDateOfEntry = (entryDate == null || entryDate.trim().isEmpty()) ? null : entryDate;
        
        String pubDate = (String) record.getValue("FoodDateOfPublication");  
        this.foodDateOfPublication = (pubDate == null || pubDate.trim().isEmpty()) ? null : pubDate;
        
        this.countryCode = (String) record.getValue("CountryCode");
        this.scientificName = (String) record.getValue("ScientificName");
        this.assignedFoodGroup = null;
    }

    // Basic getters and setters
    public Integer getFoodId() {
        return foodId;
    }

    public void setFoodId(Integer foodId) {
        this.foodId = foodId;
    }

    public Integer getFoodCode() {
        return foodCode;
    }

    public void setFoodCode(Integer foodCode) {
        this.foodCode = foodCode;
    }

    public Integer getFoodGroupId() {
        return foodGroupId;
    }

    public Integer getFoodSourceId() {
        return foodSourceId;
    }

    public void setFoodSourceId(Integer foodSourceId) {
        this.foodSourceId = foodSourceId;
    }

    public String getFoodDescription() {
        return foodDescription;
    }

    public void setFoodDescription(String foodDescription) {
        this.foodDescription = foodDescription;
    }

    public String getFoodDescriptionF() {
        return foodDescriptionF;
    }

    public void setFoodDescriptionF(String foodDescriptionF) {
        this.foodDescriptionF = foodDescriptionF;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    // Aggregation methods - Managing FoodGroup relationship
    public FoodGroup getAssignedFoodGroup() {
        return assignedFoodGroup;
    }

    public boolean hasAssignedFoodGroup() {
        return assignedFoodGroup != null;
    }

    public String getFoodGroupName() {
        return hasAssignedFoodGroup() 
            ? assignedFoodGroup.getDisplayName() 
            : "Unknown Group";
    }

    public void assignFoodGroup(FoodGroup foodGroup) {
        this.assignedFoodGroup = foodGroup;
        this.foodGroupId = foodGroup != null ? foodGroup.getFoodGroupId() : null;
    }

    public void unassignFoodGroup() {
        this.assignedFoodGroup = null;
        this.foodGroupId = null;
    }

    // Domain logic methods
    public String getDisplayName() {
        return foodDescription != null ? foodDescription : "Unknown Food";
    }

    public boolean isProteinFood() {
        return hasAssignedFoodGroup() && assignedFoodGroup.isProteinGroup();
    }

    public boolean isPlantBased() {
        return hasAssignedFoodGroup() && assignedFoodGroup.isPlantBased();
    }

    public String getFullDescription() {
        return foodCode + " - " + foodDescription;
    }

    // IRecord implementation
    @Override
    public Object getValue(String field) {
        return switch (field) {
            case "FoodID" -> foodId;
            case "FoodCode" -> foodCode;
            case "FoodGroupID" -> foodGroupId;
            case "FoodSourceID" -> foodSourceId;
            case "FoodDescription" -> foodDescription;
            case "FoodDescriptionF" -> foodDescriptionF; // French field - nullable
            case "FoodDateOfEntry" -> foodDateOfEntry; // Date field - nullable
            case "FoodDateOfPublication" -> foodDateOfPublication; // Date field - nullable
            case "CountryCode" -> countryCode;
            case "ScientificName" -> scientificName;
            default -> null;
        };
    }

    @Override
    public Collection<String> fieldNames() {
        // All fields including nullable French and date fields
        return Map.of("FoodID", foodId, "FoodCode", foodCode, 
                     "FoodGroupID", foodGroupId, "FoodSourceID", foodSourceId,
                     "FoodDescription", foodDescription, "FoodDescriptionF", foodDescriptionF,
                     "FoodDateOfEntry", foodDateOfEntry, "FoodDateOfPublication", foodDateOfPublication,
                     "CountryCode", countryCode, "ScientificName", scientificName).keySet();
    }

    @Override
    public String toString() {
        return "Food(id: %d, code: %d, description: %s, group: %s)"
            .formatted(foodId, foodCode, foodDescription, getFoodGroupName());
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