package data;

/**
 * The operator to use when comparing two values in a database query.
 */
public enum Comparison {
    EQUAL,
    NOT_EQUAL,
    GREATER_THAN,
    LESS_THAN,
    GREATER_EQUAL,
    LESS_EQUAL,
    /**
     * Performs a fuzzy search using a SQL LIKE comparison.
     */
    FUZZY_SEARCH
}
