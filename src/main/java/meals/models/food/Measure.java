package meals.models.food;

/**
 * Represents a quantity of food (ex. "10 Chips", "1 Head of Lettuce", "10 mL") and a numeric value showing how to
 * compare nutritional content to other quantities. For example, how to compare "1 Can" to "500 mL" of Apple Juice.
 */
public class Measure {
    private static final String CONVERSION_FACTORS_TABLE_NAME = "conversion_factors";
    private static final String MEASURES_TABLE_NAME = "measures";
    private static final String MEASURES_CONVERSION_FACTORS_VIEW_NAME = "conversion_factors_with_measure_details";

    private final int id;
    private final String name;
    private final float conversionValue;

    public static String getMeasuresTableName() {
        return MEASURES_TABLE_NAME;
    }

    public static String getConversionFactorsTableName() {
        return CONVERSION_FACTORS_TABLE_NAME;
    }

    public static String getConversionFactorsMeasuresTableName() {
        return MEASURES_CONVERSION_FACTORS_VIEW_NAME;
    }

    /**
     * @param id              The id of the conversion factor.
     * @param name            The name of the conversion factor.
     * @param conversionValue The numeric value to multiply by for the conversion.
     */
    public Measure(Integer id, String name, Float conversionValue) {
        this.id = id;
        this.name = name;
        this.conversionValue = conversionValue;
    }

    /**
     * @return The unique identifier of the conversion factor.
     */
    public int getId() {
        return id;
    }

    /**
     * @return The name of the conversion factor.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The numeric value to multiply by for the conversion.
     */
    public float getConversionValue() {
        return conversionValue;
    }

    @Override
    public String toString() {
        return "Measure(%s)".formatted(name);
    }
}
