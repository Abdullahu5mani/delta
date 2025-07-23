package data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Access Object for Measure operations.
 * Follows the same pattern as FoodDAO.java.
 */
public class MeasureDAO {
    private final IDatabaseDriver driver;
    private final String tableName;

    public MeasureDAO(IDatabaseDriver driver) {
        this.driver = driver;
        this.tableName = "measures";
    }

    // Basic operations following FoodDAO pattern
    public List<Measure> findAll() throws DatabaseException {
        List<IRecord> records = driver.execute(new SelectQuery(tableName));
        return records.stream()
                .map(Measure::new)
                .collect(Collectors.toList());
    }

    public Measure findById(Integer measureId) throws DatabaseException {
        List<IRecord> records = driver.execute(
                new SelectQuery(tableName)
                        .filter("MeasureID", Comparison.EQUAL, measureId)
        );
        return records.isEmpty() ? null : new Measure(records.get(0));
    }

    public void save(Measure measure) throws DatabaseException {
        driver.execute(new InsertQuery(tableName, measure));
    }

    public void update(Measure measure) throws DatabaseException {
        driver.execute(new UpdateQuery(tableName, measure));
    }

    public void deleteById(Integer measureId) throws DatabaseException {
        driver.execute(
                new DeleteQuery(tableName)
                        .filter("MeasureID", Comparison.EQUAL, measureId)
        );
    }

    public void delete(Measure measure) throws DatabaseException {
        if (measure.getMeasureId() != null) {
            deleteById(measure.getMeasureId());
        }
    }

    public int count() throws DatabaseException {
        return driver.execute(new SelectQuery(tableName)).size();
    }
}
