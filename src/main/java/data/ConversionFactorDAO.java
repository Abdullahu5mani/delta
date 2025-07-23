package data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Access Object for ConversionFactor operations.
 * Follows the same pattern as FoodDAO.java.
 * Handles aggregation relationships with Food and Measure.
 */
public class ConversionFactorDAO {
    private final IDatabaseDriver driver;
    private final String tableName;

    public ConversionFactorDAO(IDatabaseDriver driver) {
        this.driver = driver;
        this.tableName = "conversion_factors";
    }

    // Basic operations following FoodDAO pattern
    public List<ConversionFactor> findAll() throws DatabaseException {
        List<IRecord> records = driver.execute(new SelectQuery(tableName));
        return records.stream()
                .map(ConversionFactor::new)
                .collect(Collectors.toList());
    }

    public ConversionFactor findById(Integer foodId, Integer measureId) throws DatabaseException {
        List<IRecord> records = driver.execute(
                new SelectQuery(tableName)
                        .filter("FoodID", Comparison.EQUAL, foodId)
                        .filter("MeasureID", Comparison.EQUAL, measureId)
        );
        return records.isEmpty() ? null : new ConversionFactor(records.get(0));
    }

    public List<ConversionFactor> findByFoodId(Integer foodId) throws DatabaseException {
        List<IRecord> records = driver.execute(
                new SelectQuery(tableName)
                        .filter("FoodID", Comparison.EQUAL, foodId)
        );
        return records.stream()
                .map(ConversionFactor::new)
                .collect(Collectors.toList());
    }

    public List<ConversionFactor> findByMeasureId(Integer measureId) throws DatabaseException {
        List<IRecord> records = driver.execute(
                new SelectQuery(tableName)
                        .filter("MeasureID", Comparison.EQUAL, measureId)
        );
        return records.stream()
                .map(ConversionFactor::new)
                .collect(Collectors.toList());
    }

    public void save(ConversionFactor conversionFactor) throws DatabaseException {
        driver.execute(new InsertQuery(tableName, conversionFactor));
    }

    public void update(ConversionFactor conversionFactor) throws DatabaseException {
        driver.execute(new UpdateQuery(tableName, conversionFactor));
    }

    public void deleteById(Integer foodId, Integer measureId) throws DatabaseException {
        driver.execute(
                new DeleteQuery(tableName)
                        .filter("FoodID", Comparison.EQUAL, foodId)
                        .filter("MeasureID", Comparison.EQUAL, measureId)
        );
    }

    public void delete(ConversionFactor conversionFactor) throws DatabaseException {
        if (conversionFactor.getFoodId() != null && conversionFactor.getMeasureId() != null) {
            deleteById(conversionFactor.getFoodId(), conversionFactor.getMeasureId());
        }
    }

    public int count() throws DatabaseException {
        return driver.execute(new SelectQuery(tableName)).size();
    }
}
