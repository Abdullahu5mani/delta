package meals.models.nutrient;


/**
 * Represents a nutrient entity (e.g., "PROTEIN", "CALCIUM", "VITAMIN C").
 * This class implements IRecord to integrate with the database layer.
 */
public class Nutrient {
    private Integer nutrientId;
    private String nutrientSymbol;
    private String nutrientUnit;
    private String nutrientName;


    /**
     * Constructor for creating a nutrient with all details.
     */
    public Nutrient(Integer nutrientId,String nutrientSymbol, String nutrientUnit,
                    String nutrientName) {
        this.nutrientId = nutrientId;
        this.nutrientSymbol = nutrientSymbol;
        this.nutrientUnit = nutrientUnit;
        this.nutrientName = nutrientName;
    }

    // Getters and setters
    public Integer getNutrientId() {
        return nutrientId;
    }

    public String getNutrientSymbol() {
        return nutrientSymbol;
    }

    public String getNutrientUnit() {
        return nutrientUnit;
    }

    public String getNutrientName() {
        return nutrientName;
    }

    // Domain logic methods
    public String getDisplayNameWithUnit() {
        String name = nutrientName != null ? nutrientName : "Unknown Nutrient";
        String unit = nutrientUnit != null ? " (" + nutrientUnit + ")" : "";
        return name + unit;
    }

    @Override
    public String toString() {
        return "Nutrient(id: %d, name: %s, unit: %s)".formatted(nutrientId, nutrientName, nutrientUnit);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Nutrient that = (Nutrient) obj;
        return nutrientId != null ? nutrientId.equals(that.nutrientId) : that.nutrientId == null;
    }

    @Override
    public int hashCode() {
        return nutrientId != null ? nutrientId.hashCode() : 0;
    }
}
