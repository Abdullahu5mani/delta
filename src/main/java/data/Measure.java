package data;

import java.util.Collection;
import java.util.Map;

/**
 * Represents a measurement unit (e.g., "100ml", "1 cup", "1 tablespoon") that can be used for food quantities.
 * Follows the same pattern as Food.java domain class.
 */
public class Measure implements IRecord {
    private Integer measureId;
    private String measureDescription;
    private String measureDescriptionF;  // French - nullable
    private String commonName;

    // Default constructor
    public Measure() {}

    // Constructor from IRecord (for CSV import and database queries)
    public Measure(IRecord record) {
        this.measureId = (Integer) record.getValue("MeasureID");
        this.measureDescription = (String) record.getValue("MeasureDescription");
        this.measureDescriptionF = (String) record.getValue("MeasureDescriptionF");
        this.commonName = (String) record.getValue("CommonName");
    }

    // Constructor with parameters
    public Measure(Integer measureId, String measureDescription, String measureDescriptionF, String commonName) {
        this.measureId = measureId;
        this.measureDescription = measureDescription;
        this.measureDescriptionF = measureDescriptionF;
        this.commonName = commonName;
    }

    @Override
    public Object getValue(String field) {
        return switch (field) {
            case "MeasureID" -> measureId;
            case "MeasureDescription" -> measureDescription;
            case "MeasureDescriptionF" -> measureDescriptionF;
            case "CommonName" -> commonName;
            default -> null;
        };
    }

    @Override
    public Collection<String> fieldNames() {
        return Map.of("MeasureID", measureId, "MeasureDescription", measureDescription,
                     "MeasureDescriptionF", measureDescriptionF, "CommonName", commonName).keySet();
    }

    // Getters and setters
    public Integer getMeasureId() { return measureId; }
    public void setMeasureId(Integer measureId) { this.measureId = measureId; }

    public String getMeasureDescription() { return measureDescription; }
    public void setMeasureDescription(String measureDescription) { this.measureDescription = measureDescription; }

    public String getMeasureDescriptionF() { return measureDescriptionF; }
    public void setMeasureDescriptionF(String measureDescriptionF) { this.measureDescriptionF = measureDescriptionF; }

    public String getCommonName() { return commonName; }
    public void setCommonName(String commonName) { this.commonName = commonName; }

    @Override
    public String toString() {
        return "Measure{" +
                "measureId=" + measureId +
                ", description='" + measureDescription + '\'' +
                ", commonName='" + commonName + '\'' +
                '}';
    }
}
