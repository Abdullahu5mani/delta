package data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Access Object for FoodGroup operations.
 */
public class FoodGroupDAO {
    private final IDatabaseDriver driver;
    private final String tableName;

    public FoodGroupDAO(IDatabaseDriver driver) {
        this.driver = driver;
        this.tableName = "food_groups";
    }

    public List<FoodGroup> findAll() throws DatabaseException {
        List<IRecord> records = driver.execute(new SelectQuery(tableName));
        return records.stream()
                .map(FoodGroup::new)
                .collect(Collectors.toList());
    }

    public FoodGroup findById(Integer foodGroupId) throws DatabaseException {
        List<IRecord> records = driver.execute(
                new SelectQuery(tableName)
                        .filter("FoodGroupID", Comparison.EQUAL, foodGroupId)
        );
        return records.isEmpty() ? null : new FoodGroup(records.get(0));
    }

    public FoodGroup findByCode(Integer foodGroupCode) throws DatabaseException {
        List<IRecord> records = driver.execute(
                new SelectQuery(tableName)
                        .filter("FoodGroupCode", Comparison.EQUAL, foodGroupCode)
        );
        return records.isEmpty() ? null : new FoodGroup(records.get(0));
    }

    public List<FoodGroup> findByName(String name) throws DatabaseException {
        List<IRecord> records = driver.execute(
                new SelectQuery(tableName)
                        .filter("FoodGroupName", Comparison.EQUAL, name)
        );
        return records.stream()
                .map(FoodGroup::new)
                .collect(Collectors.toList());
    }

    public void save(FoodGroup foodGroup) throws DatabaseException {
        driver.execute(new InsertQuery(tableName, foodGroup));
    }

    public void update(FoodGroup foodGroup) throws DatabaseException {
        driver.execute(new UpdateQuery(tableName, foodGroup));
    }

    public void deleteById(Integer foodGroupId) throws DatabaseException {
        driver.execute(
                new DeleteQuery(tableName)
                        .filter("FoodGroupID", Comparison.EQUAL, foodGroupId)
        );
    }

    public void delete(FoodGroup foodGroup) throws DatabaseException {
        if (foodGroup.getFoodGroupId() != null) {
            deleteById(foodGroup.getFoodGroupId());
        }
    }

    public int count() throws DatabaseException {
        return driver.execute(new SelectQuery(tableName)).size();
    }
} 