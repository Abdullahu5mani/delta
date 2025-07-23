package data;

import java.util.Collection;
import java.util.Map;

/**
 * Represents a conversion factor that allows converting between different measurement units for foods.
 * Links a specific food with a measurement unit and provides the conversion multiplier.
 * Follows the same pattern as Food.java domain class.
 */
public class ConversionFactor implements IRecord {
    private Integer foodId;
    private Integer measureId;
    private Double conversionFactorValue;
    private String convFactorDateOfEntry;

    // Default constructor
    public ConversionFactor() {}

    // Constructor from IRecord (for CSV import and database queries)
    public ConversionFactor(IRecord record) {
        this.foodId = (Integer) record.getValue("FoodID");
        this.measureId = (Integer) record.getValue("MeasureID");
        this.conversionFactorValue = (Double) record.getValue("ConversionFactorValue");
        this.convFactorDateOfEntry = (String) record.getValue("ConvFactorDateOfEntry");
    }

    // Constructor with parameters
    public ConversionFactor(Integer foodId, Integer measureId, Double conversionFactorValue, String convFactorDateOfEntry) {
        this.foodId = foodId;
        this.measureId = measureId;
        this.conversionFactorValue = conversionFactorValue;
        this.convFactorDateOfEntry = convFactorDateOfEntry;
    }

    @Override
    public Object getValue(String field) {
        return switch (field) {
            case "FoodID" -> foodId;
            case "MeasureID" -> measureId;
            case "ConversionFactorValue" -> conversionFactorValue;
            case "ConvFactorDateOfEntry" -> convFactorDateOfEntry;
            default -> null;
        };
    }

    @Override
    public Collection<String> fieldNames() {
        return Map.of("FoodID", foodId, "MeasureID", measureId,
                     "ConversionFactorValue", conversionFactorValue, 
                     "ConvFactorDateOfEntry", convFactorDateOfEntry).keySet();
    }

    // Getters and setters
    public Integer getFoodId() { return foodId; }
    public void setFoodId(Integer foodId) { this.foodId = foodId; }

    public Integer getMeasureId() { return measureId; }
    public void setMeasureId(Integer measureId) { this.measureId = measureId; }

    public Double getConversionFactorValue() { return conversionFactorValue; }
    public void setConversionFactorValue(Double conversionFactorValue) { this.conversionFactorValue = conversionFactorValue; }

    public String getConvFactorDateOfEntry() { return convFactorDateOfEntry; }
    public void setConvFactorDateOfEntry(String convFactorDateOfEntry) { this.convFactorDateOfEntry = convFactorDateOfEntry; }

    @Override
    public String toString() {
        return "ConversionFactor{" +
                "foodId=" + foodId +
                ", measureId=" + measureId +
                ", conversionFactor=" + conversionFactorValue +
                ", dateOfEntry='" + convFactorDateOfEntry + '\'' +
                '}';
    }
}
