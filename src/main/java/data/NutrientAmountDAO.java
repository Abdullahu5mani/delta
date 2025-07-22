package data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Access Object for NutrientAmount operations.
 */
public class NutrientAmountDAO {
    private final IDatabaseDriver driver;
    private final String tableName;

    public NutrientAmountDAO(IDatabaseDriver driver) {
        this.driver = driver;
        this.tableName = "nutrient_amounts";
    }

    public List<NutrientAmount> findAll() throws DatabaseException {
        List<IRecord> records = driver.execute(new SelectQuery(tableName));
        return records.stream()
                .map(NutrientAmount::new)
                .collect(Collectors.toList());
    }

    public NutrientAmount findByFoodAndNutrient(Integer foodId, Integer nutrientId) throws DatabaseException {
        List<IRecord> records = driver.execute(
                new SelectQuery(tableName)
                        .filter("FoodID", Comparison.EQUAL, foodId)
                        .filter("NutrientID", Comparison.EQUAL, nutrientId)
        );
        return records.isEmpty() ? null : new NutrientAmount(records.get(0));
    }

    public List<NutrientAmount> findByFood(Integer foodId) throws DatabaseException {
        List<IRecord> records = driver.execute(
                new SelectQuery(tableName)
                        .filter("FoodID", Comparison.EQUAL, foodId)
        );
        return records.stream()
                .map(NutrientAmount::new)
                .collect(Collectors.toList());
    }

    public List<NutrientAmount> findByNutrient(Integer nutrientId) throws DatabaseException {
        List<IRecord> records = driver.execute(
                new SelectQuery(tableName)
                        .filter("NutrientID", Comparison.EQUAL, nutrientId)
        );
        return records.stream()
                .map(NutrientAmount::new)
                .collect(Collectors.toList());
    }

    public void save(NutrientAmount nutrientAmount) throws DatabaseException {
        driver.execute(new InsertQuery(tableName, nutrientAmount));
    }

    public void update(NutrientAmount nutrientAmount) throws DatabaseException {
        driver.execute(new UpdateQuery(tableName, nutrientAmount));
    }

    public void deleteByFoodAndNutrient(Integer foodId, Integer nutrientId) throws DatabaseException {
        driver.execute(
                new DeleteQuery(tableName)
                        .filter("FoodID", Comparison.EQUAL, foodId)
                        .filter("NutrientID", Comparison.EQUAL, nutrientId)
        );
    }

    public void deleteByFood(Integer foodId) throws DatabaseException {
        driver.execute(
                new DeleteQuery(tableName)
                        .filter("FoodID", Comparison.EQUAL, foodId)
        );
    }

    public void delete(NutrientAmount nutrientAmount) throws DatabaseException {
        deleteByFoodAndNutrient(nutrientAmount.getFoodId(), nutrientAmount.getNutrientId());
    }

    public int count() throws DatabaseException {
        return driver.execute(new SelectQuery(tableName)).size();
    }
} 