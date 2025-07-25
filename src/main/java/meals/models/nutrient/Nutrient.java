package meals.models.nutrient;


import data.IRecord;

/**
 * Represents a nutrient entity (e.g., "PROTEIN", "CALCIUM", "VITAMIN C").
 * This class implements IRecord to integrate with the database layer.
 */
public class Nutrient {
    private static final String TABLE_NAME = "nutrients";
    private static final String NUTRIENT_AMOUNTS_TABLE_NAME = "nutrient_amounts";

    private Integer nutrientId;
    private String nutrientSymbol;
    private String nutrientUnit;
    private String nutrientName;


    /**
     * @return The name of the table where nutrients are persisted.
     */
    public static String getTableName() {
        return TABLE_NAME;
    }

    /**
     * @return The name of the table where nutrient amounts for each food are stored.
     */
    public static String getNutrientAmountsTableName() {
        return NUTRIENT_AMOUNTS_TABLE_NAME;
    }

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

    /**
     * @param record The record to construct a nutrient from.
     */
    public Nutrient(IRecord record) {
        this.nutrientId = (int) record.getValue("id");
        this.nutrientSymbol = (String) record.getValue("symbol");
        this.nutrientUnit = (String) record.getValue("unit");
        this.nutrientName = (String) record.getValue("unit");
    }

    // Getters and setters

    /**
     * @return The ID of the nutrient.
     */
    public Integer getNutrientId() {
        return nutrientId;
    }

    /**
     * @return The symbol for the nutrient.
     */
    public String getNutrientSymbol() {
        return nutrientSymbol;
    }

    /**
     * @return The units for the nutrient.
     */
    public String getNutrientUnit() {
        return nutrientUnit;
    }

    /**
     * @return The name of the nutrient.
     */
    public String getNutrientName() {
        return nutrientName;
    }

    // Domain logic methods

    /**
     * @return The display nae of the nutrient.
     */
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
