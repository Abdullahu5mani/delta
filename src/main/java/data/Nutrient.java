package data;

import java.util.Collection;
import java.util.Map;

/**
 * Represents a nutrient entity (e.g., "PROTEIN", "CALCIUM", "VITAMIN C").
 * This class implements IRecord to integrate with the database layer.
 */
public class Nutrient implements IRecord {
    private Integer nutrientId;
    private Integer nutrientCode;
    private String nutrientSymbol;
    private String nutrientUnit;
    private String nutrientName;
    private String nutrientNameF; // French name
    private String tagname;
    private Integer nutrientDecimals;

    /**
     * Default constructor.
     */
    public Nutrient() {
    }

    /**
     * Constructor for creating a nutrient with all details.
     */
    public Nutrient(Integer nutrientId, Integer nutrientCode, String nutrientSymbol, String nutrientUnit,
                   String nutrientName, String nutrientNameF, String tagname, Integer nutrientDecimals) {
        this.nutrientId = nutrientId;
        this.nutrientCode = nutrientCode;
        this.nutrientSymbol = nutrientSymbol;
        this.nutrientUnit = nutrientUnit;
        this.nutrientName = nutrientName;
        this.nutrientNameF = nutrientNameF;
        this.tagname = tagname;
        this.nutrientDecimals = nutrientDecimals;
    }

    /**
     * Constructor for creating a Nutrient from database record data.
     */
    public Nutrient(IRecord record) {
        this.nutrientId = (Integer) record.getValue("NutrientID");
        this.nutrientCode = (Integer) record.getValue("NutrientCode");
        this.nutrientSymbol = (String) record.getValue("NutrientSymbol");
        this.nutrientUnit = (String) record.getValue("NutrientUnit");
        this.nutrientName = (String) record.getValue("NutrientName");
        this.nutrientNameF = null; // No French field in database
        this.tagname = (String) record.getValue("Tagname");
        this.nutrientDecimals = (Integer) record.getValue("NutrientDecimals");
    }

    // Getters and setters
    public Integer getNutrientId() {
        return nutrientId;
    }

    public void setNutrientId(Integer nutrientId) {
        this.nutrientId = nutrientId;
    }

    public Integer getNutrientCode() {
        return nutrientCode;
    }

    public void setNutrientCode(Integer nutrientCode) {
        this.nutrientCode = nutrientCode;
    }

    public String getNutrientSymbol() {
        return nutrientSymbol;
    }

    public void setNutrientSymbol(String nutrientSymbol) {
        this.nutrientSymbol = nutrientSymbol;
    }

    public String getNutrientUnit() {
        return nutrientUnit;
    }

    public void setNutrientUnit(String nutrientUnit) {
        this.nutrientUnit = nutrientUnit;
    }

    public String getNutrientName() {
        return nutrientName;
    }

    public void setNutrientName(String nutrientName) {
        this.nutrientName = nutrientName;
    }

    public String getNutrientNameF() {
        return nutrientNameF;
    }

    public void setNutrientNameF(String nutrientNameF) {
        this.nutrientNameF = nutrientNameF;
    }

    public String getTagname() {
        return tagname;
    }

    public void setTagname(String tagname) {
        this.tagname = tagname;
    }

    public Integer getNutrientDecimals() {
        return nutrientDecimals;
    }

    public void setNutrientDecimals(Integer nutrientDecimals) {
        this.nutrientDecimals = nutrientDecimals;
    }

    // Domain logic methods
    public String getDisplayNameWithUnit() {
        String name = nutrientName != null ? nutrientName : "Unknown Nutrient";
        String unit = nutrientUnit != null ? " (" + nutrientUnit + ")" : "";
        return name + unit;
    }

    public boolean isMacronutrient() {
        if (nutrientName == null) return false;
        String name = nutrientName.toLowerCase();
        return name.contains("protein") || name.contains("fat") || name.contains("carbohydrate") ||
               name.contains("lipid") || name.contains("energy");
    }

    public boolean isVitamin() {
        if (nutrientName == null) return false;
        String name = nutrientName.toLowerCase();
        return name.contains("vitamin") || name.contains("retinol") || name.contains("thiamin") ||
               name.contains("riboflavin") || name.contains("niacin") || name.contains("folacin") ||
               name.contains("tocopherol");
    }

    public boolean isMineral() {
        if (nutrientName == null) return false;
        String name = nutrientName.toLowerCase();
        return name.contains("calcium") || name.contains("iron") || name.contains("magnesium") ||
               name.contains("phosphorus") || name.contains("potassium") || name.contains("sodium") ||
               name.contains("zinc") || name.contains("copper") || name.contains("manganese") ||
               name.contains("selenium");
    }

    // IRecord implementation
    @Override
    public Object getValue(String field) {
        return switch (field) {
            case "NutrientID" -> nutrientId;
            case "NutrientCode" -> nutrientCode;
            case "NutrientSymbol" -> nutrientSymbol;
            case "NutrientUnit" -> nutrientUnit;
            case "NutrientName" -> nutrientName;
            // No NutrientNameF - French field removed from database
            case "Tagname" -> tagname;
            case "NutrientDecimals" -> nutrientDecimals;
            default -> null;
        };
    }

    @Override
    public Collection<String> fieldNames() {
        // Only English fields - no French fields in database
        return Map.of("NutrientID", nutrientId, "NutrientCode", nutrientCode, 
                     "NutrientSymbol", nutrientSymbol, "NutrientUnit", nutrientUnit,
                     "NutrientName", nutrientName, "Tagname", tagname, 
                     "NutrientDecimals", nutrientDecimals).keySet();
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