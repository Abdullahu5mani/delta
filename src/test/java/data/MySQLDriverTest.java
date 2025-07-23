package data;

import org.junit.jupiter.api.Test;
import shared.AppBackend;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MySQLDriverTest {
    @Test
    public void testAlwaysTrue() {
        // test to verify Docker is working
        assertEquals(1 + 1, 2, "Always true.");
    }

    @Test
    public void testSelectAll() throws DatabaseException {
        List<IRecord> records = AppBackend.db().execute(new SelectQuery("students"));
        assertTrue(records.size() >= 4, "Should have at least 4 student records from seed data");
    }
    
    @Test
    public void testSelectLimit() throws DatabaseException {
        List<IRecord> records = AppBackend.db().execute(
            new SelectQuery("students").limit(2)
        );
        assertEquals(2, records.size(), "Limit should return exactly 2 records");
    }
    
    @Test
    public void testFuzzySearch() throws DatabaseException {
        List<IRecord> records = AppBackend.db().execute(
            new SelectQuery("students").filter("name", Comparison.FUZZY_SEARCH, "a")
        );
        assertTrue(records.size() > 0, "Fuzzy search should find records containing 'a'");
        
        // Verify that all returned records contain 'a' in the name field
        for (IRecord record : records) {
            String name = (String) record.getValue("name");
            assertTrue(name.toLowerCase().contains("a"), 
                "Record name should contain 'a': " + name);
        }
    }
    
    @Test
    public void testOrderBy() throws DatabaseException {
        // Test ascending order
        List<IRecord> ascendingRecords = AppBackend.db().execute(
            new SelectQuery("students")
                .sortColumn("id")
                .sortOrder(SortOrder.ASCENDING)
        );
        assertTrue(ascendingRecords.size() > 1, "Should have multiple records to test ordering");
        
        // Verify ascending order
        for (int i = 1; i < ascendingRecords.size(); i++) {
            Integer prevId = (Integer) ascendingRecords.get(i - 1).getValue("id");
            Integer currentId = (Integer) ascendingRecords.get(i).getValue("id");
            assertTrue(prevId <= currentId, "Records should be in ascending order by id");
        }
        
        // Test descending order
        List<IRecord> descendingRecords = AppBackend.db().execute(
            new SelectQuery("students")
                .sortColumn("id")
                .sortOrder(SortOrder.DESCENDING)
        );
        
        // Verify descending order
        for (int i = 1; i < descendingRecords.size(); i++) {
            Integer prevId = (Integer) descendingRecords.get(i - 1).getValue("id");
            Integer currentId = (Integer) descendingRecords.get(i).getValue("id");
            assertTrue(prevId >= currentId, "Records should be in descending order by id");
        }
    }
}
