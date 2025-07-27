package meals.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import shared.service_output.ServiceError;
import shared.service_output.ServiceOutput;

/**
 * Service for querying date ranges with available meal logs.
 * Used to determine which dates the user can select for swap generation.
 */
public class MealDateRangeService {
    private static MealDateRangeService instance;
    
    private MealDateRangeService() {}
    
    public static MealDateRangeService instance() {
        if (instance == null) {
            instance = new MealDateRangeService();
        }
        return instance;
    }

    /**
     * Output containing available date ranges for meal logs.
     */
    public static class DateRangeOutput extends ServiceOutput {
        private final List<Date> availableDates;
        private final Date earliestDate;
        private final Date latestDate;

        public DateRangeOutput(List<Date> availableDates, Date earliestDate, Date latestDate, List<ServiceError> errors) {
            super(errors);
            this.availableDates = availableDates;
            this.earliestDate = earliestDate;
            this.latestDate = latestDate;
        }

        public List<Date> getAvailableDates() {
            return availableDates;
        }

        public Date getEarliestDate() {
            return earliestDate;
        }

        public Date getLatestDate() {
            return latestDate;
        }

        public boolean hasAnyMealLogs() {
            return !availableDates.isEmpty();
        }
    }

    /**
     * Gets the available date range for meal logs.
     * 
     * @return DateRangeOutput containing available dates and range info
     */
    public DateRangeOutput getAvailableDateRange() {
        try {
            // Get a wide date range to find all meals (last 30 days to future)
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -30); // 30 days ago
            Date startDate = cal.getTime();
            
            cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 1); // Tomorrow
            Date endDate = cal.getTime();
            
            QueryMealsService.QueryMealsServiceOutput mealsOutput = 
                    QueryMealsService.instance().getMealsByDate(startDate, endDate);
            
            if (!mealsOutput.ok()) {
                List<ServiceError> errors = List.of(new ServiceError("Error getting meal logs: " + mealsOutput.errors()));
                return new DateRangeOutput(Collections.emptyList(), null, null, errors);
            }
            
            // Extract unique dates from meals
            Set<Date> uniqueDates = new HashSet<>();
            for (var meal : mealsOutput.getMeals()) {
                Date mealDate = truncateToDay(meal.getCreatedAt());
                uniqueDates.add(mealDate);
            }
            
            List<Date> availableDates = new ArrayList<>(uniqueDates);
            Collections.sort(availableDates);
            
            Date earliestDate = availableDates.isEmpty() ? null : availableDates.get(0);
            Date latestDate = availableDates.isEmpty() ? null : availableDates.get(availableDates.size() - 1);
            
            return new DateRangeOutput(availableDates, earliestDate, latestDate, Collections.emptyList());
            
        } catch (Exception e) {
            List<ServiceError> errors = List.of(new ServiceError("Error determining available date range: " + e.getMessage()));
            return new DateRangeOutput(Collections.emptyList(), null, null, errors);
        }
    }

    /**
     * Checks if there are meal logs within the specified date range.
     * 
     * @param fromDate Start date (inclusive)
     * @param toDate End date (inclusive)
     * @return true if there are meal logs in the range, false otherwise
     */
    public boolean hasMealLogsInRange(Date fromDate, Date toDate) {
        try {
            QueryMealsService.QueryMealsServiceOutput mealsOutput = 
                    QueryMealsService.instance().getMealsByDate(fromDate, toDate);
            
            return mealsOutput.ok() && !mealsOutput.getMeals().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Truncates a date to just the day (removes time component).
     */
    private Date truncateToDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}
