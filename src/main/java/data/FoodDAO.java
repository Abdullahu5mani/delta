package data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Enhanced Data Access Object for Food operations.
 * Handles aggregation relationships with FoodGroup.
 */
public class FoodDAO {
    private final IDatabaseDriver driver;
    private final FoodGroupDAO foodGroupDAO;
    private final String tableName;

    public FoodDAO(IDatabaseDriver driver, FoodGroupDAO foodGroupDAO) {
        this.driver = driver;
        this.foodGroupDAO = foodGroupDAO;
        this.tableName = "foods";
    }

    // Basic operations (without relationships)
    public List<Food> findAll() throws DatabaseException {
        List<IRecord> records = driver.execute(new SelectQuery(tableName));
        return records.stream()
                .map(Food::new)
                .collect(Collectors.toList());
    }

    public Food findById(Integer foodId) throws DatabaseException {
        List<IRecord> records = driver.execute(
                new SelectQuery(tableName)
                        .filter("FoodID", Comparison.EQUAL, foodId)
        );
        return records.isEmpty() ? null : new Food(records.get(0));
    }

    public Food findByCode(Integer foodCode) throws DatabaseException {
        List<IRecord> records = driver.execute(
                new SelectQuery(tableName)
                        .filter("FoodCode", Comparison.EQUAL, foodCode)
        );
        return records.isEmpty() ? null : new Food(records.get(0));
    }

    // Enhanced operations (with relationships)
    public Food findByIdWithFoodGroup(Integer foodId) throws DatabaseException {
        Food food = findById(foodId);
        if (food != null && food.getFoodGroupId() != null) {
            FoodGroup foodGroup = foodGroupDAO.findById(food.getFoodGroupId());
            if (foodGroup != null) {
                food.assignFoodGroup(foodGroup);
            }
        }
        return food;
    }

    public List<Food> findAllWithFoodGroups() throws DatabaseException {
        List<Food> foods = findAll();
        
        // Load food groups for all foods
        for (Food food : foods) {
            if (food.getFoodGroupId() != null) {
                FoodGroup foodGroup = foodGroupDAO.findById(food.getFoodGroupId());
                if (foodGroup != null) {
                    food.assignFoodGroup(foodGroup);
                }
            }
        }
        
        return foods;
    }

    public List<Food> findByFoodGroup(Integer foodGroupId) throws DatabaseException {
        List<IRecord> records = driver.execute(
                new SelectQuery(tableName)
                        .filter("FoodGroupID", Comparison.EQUAL, foodGroupId)
        );
        return records.stream()
                .map(Food::new)
                .collect(Collectors.toList());
    }

    public List<Food> findByFoodGroupWithDetails(Integer foodGroupId) throws DatabaseException {
        List<Food> foods = findByFoodGroup(foodGroupId);
        FoodGroup foodGroup = foodGroupDAO.findById(foodGroupId);
        
        if (foodGroup != null) {
            for (Food food : foods) {
                food.assignFoodGroup(foodGroup);
            }
        }
        
        return foods;
    }

    public void save(Food food) throws DatabaseException {
        driver.execute(new InsertQuery(tableName, food));
    }

    public void update(Food food) throws DatabaseException {
        driver.execute(new UpdateQuery(tableName, food));
    }

    public void deleteById(Integer foodId) throws DatabaseException {
        driver.execute(
                new DeleteQuery(tableName)
                        .filter("FoodID", Comparison.EQUAL, foodId)
        );
    }

    public void delete(Food food) throws DatabaseException {
        if (food.getFoodId() != null) {
            deleteById(food.getFoodId());
        }
    }

    public int count() throws DatabaseException {
        return driver.execute(new SelectQuery(tableName)).size();
    }

    // Business logic methods
    public List<Food> findFoodsWithoutGroup() throws DatabaseException {
        List<Food> allFoods = findAll();
        return allFoods.stream()
                .filter(food -> food.getFoodGroupId() == null)
                .collect(Collectors.toList());
    }

    public List<Food> findFoodsByGroup(Integer foodGroupId) throws DatabaseException {
        List<IRecord> records = driver.execute(
                new SelectQuery(tableName)
                        .filter("FoodGroupID", Comparison.EQUAL, foodGroupId)
        );
        return records.stream()
                .map(Food::new)
                .collect(Collectors.toList());
    }
} 