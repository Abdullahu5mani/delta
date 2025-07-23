package data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a query to select records from the database.
 */
public class SelectQuery extends Query {
    /**
     * The filters applied to the query controlling which records are selected.
     */
    private final List<QueryFilter> filters;
    
    /**
     * The maximum number of records to return.
     */
    private Integer limit;
    
    /**
     * The column to sort by.
     */
    private String sortColumn;
    
    /**
     * The order to sort by.
     */
    private SortOrder sortOrder;

    /**
     * @param collectionName The name of the database collection to select records from.
     */
    public SelectQuery(String collectionName) {
        super(collectionName);
        filters = new ArrayList<>();
    }

    /**
     * Adds a filter to the query specifying which records to select from the database.
     *
     * @param field      The field to filter on.
     * @param comparison The comparison to perform using the field and value as operands.
     * @param value      The value to compare the field to.
     * @return The same query instance after adding a filter to it.
     */
    public SelectQuery filter(String field, Comparison comparison, Object value) {
        filters.add(new QueryFilter(field, comparison, value));
        return this;
    }
    
    /**
     * Sets the maximum number of records to return.
     *
     * @param limit The maximum number of records to return.
     * @return The same query instance after setting the limit.
     */
    public SelectQuery limit(Integer limit) {
        this.limit = limit;
        return this;
    }
    
    /**
     * Sets the column to sort by.
     *
     * @param sortColumn The column to sort by.
     * @return The same query instance after setting the sort column.
     */
    public SelectQuery sortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
        return this;
    }
    
    /**
     * Sets the order to sort by.
     *
     * @param sortOrder The order to sort by.
     * @return The same query instance after setting the sort order.
     */
    public SelectQuery sortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

    /**
     * @return The list of filters applied to the query.
     */
    protected List<QueryFilter> getFilters() {
        return filters;
    }
    
    /**
     * @return The maximum number of records to return.
     */
    public Integer getLimit() {
        return limit;
    }
    
    /**
     * @return The column to sort by.
     */
    public String getSortColumn() {
        return sortColumn;
    }
    
    /**
     * @return The order to sort by.
     */
    public SortOrder getSortOrder() {
        return sortOrder;
    }

    @Override
    public String toString() {
        return "SelectQuery(collectionName: %s, filters: %s, limit: %s, sortColumn: %s, sortOrder: %s)"
                .formatted(getCollectionName(), getFilters(), limit, sortColumn, sortOrder);
    }
}
