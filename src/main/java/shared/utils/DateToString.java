package shared.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * utility class converting d Date object to a "yyyy-MM-dd" format.
 */
public class DateToString {
    /**
     * Calls the helper method.
     * @param date Teh date to format.
     * @return The date in "yyyy-MM-dd" format.
     */
    public static String call(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}
