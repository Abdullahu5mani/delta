package data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Access Object for Nutrient operations.
 */
public class NutrientDAO {
    private final IDatabaseDriver driver;
    private final String tableName;

    public NutrientDAO(IDatabaseDriver driver) {
        this.driver = driver;
        this.tableName = "nutrients";
    }

    public List<Nutrient> findAll() throws DatabaseException {
        List<IRecord> records = driver.execute(new SelectQuery(tableName));
        return records.stream()
                .map(Nutrient::new)
                .collect(Collectors.toList());
    }

    public Nutrient findById(Integer nutrientId) throws DatabaseException {
        List<IRecord> records = driver.execute(
                new SelectQuery(tableName)
                        .filter("NutrientID", Comparison.EQUAL, nutrientId)
        );
        return records.isEmpty() ? null : new Nutrient(records.get(0));
    }

    public Nutrient findByCode(Integer nutrientCode) throws DatabaseException {
        List<IRecord> records = driver.execute(
                new SelectQuery(tableName)
                        .filter("NutrientCode", Comparison.EQUAL, nutrientCode)
        );
        return records.isEmpty() ? null : new Nutrient(records.get(0));
    }

    public Nutrient findBySymbol(String nutrientSymbol) throws DatabaseException {
        List<IRecord> records = driver.execute(
                new SelectQuery(tableName)
                        .filter("NutrientSymbol", Comparison.EQUAL, nutrientSymbol)
        );
        return records.isEmpty() ? null : new Nutrient(records.get(0));
    }

    public List<Nutrient> findByUnit(String unit) throws DatabaseException {
        List<IRecord> records = driver.execute(
                new SelectQuery(tableName)
                        .filter("NutrientUnit", Comparison.EQUAL, unit)
        );
        return records.stream()
                .map(Nutrient::new)
                .collect(Collectors.toList());
    }

    public void save(Nutrient nutrient) throws DatabaseException {
        driver.execute(new InsertQuery(tableName, nutrient));
    }

    public void update(Nutrient nutrient) throws DatabaseException {
        driver.execute(new UpdateQuery(tableName, nutrient));
    }

    public void deleteById(Integer nutrientId) throws DatabaseException {
        driver.execute(
                new DeleteQuery(tableName)
                        .filter("NutrientID", Comparison.EQUAL, nutrientId)
        );
    }

    public void delete(Nutrient nutrient) throws DatabaseException {
        if (nutrient.getNutrientId() != null) {
            deleteById(nutrient.getNutrientId());
        }
    }

    public int count() throws DatabaseException {
        return driver.execute(new SelectQuery(tableName)).size();
    }
} 