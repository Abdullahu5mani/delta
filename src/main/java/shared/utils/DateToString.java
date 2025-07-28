package shared.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * utility class converting a Date object to a "yyyy-MM-dd HH:mm:ss" format for DATETIME fields.
 */
public class DateToString {
    /**
     * Calls the helper method.
     * @param date The date to format.
     * @return The date in "yyyy-MM-dd HH:mm:ss" format for DATETIME fields.
     */
    public static String call(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
}
