package shared.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Utility class for creating date ranges for database queries.
 * Provides helper methods to get start and end of day for a given date.
 */
public class DateRangeUtils {
    
    /**
     * Creates a Date object representing the start of the day (00:00:00.000) for the given date.
     * 
     * @param date The date to get the start of day for
     * @return A Date object representing 00:00:00.000 on the given date
     */
    public static Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    
    /**
     * Creates a Date object representing the end of the day (23:59:59.999) for the given date.
     * 
     * @param date The date to get the end of day for
     * @return A Date object representing 23:59:59.999 on the given date
     */
    public static Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }
    
    /**
     * Creates a date range for a given date, returning both start and end of day.
     * 
     * @param date The date to create a range for
     * @return An array with [startOfDay, endOfDay]
     */
    public static Date[] getDayRange(Date date) {
        return new Date[]{getStartOfDay(date), getEndOfDay(date)};
    }
} 