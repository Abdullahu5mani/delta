package statistics.view;

import java.util.Date;

public interface INutrientBreakdownView {
    
    /** Register a handler fired when the user requests to generate a chart. */
    void setOnGenerateChartRequested(GenerateChartCallback callback);
    
    /** Show the main panel containing the UI. */
    javax.swing.JPanel getMainPanel();
    
    /** Update the chart display area with new visualization. */
    void updateChart(javax.swing.JPanel chartPanel);
    
    /** Show an error message to the user. */
    void showError(String errorMessage);
    
    /** Show a no-data message to the user. */
    void showNoData();
    
    /** Get the selected date range from the UI. */
    DateRange getSelectedDateRange();
    
    /** Callback interface for chart generation requests. */
    @FunctionalInterface
    interface GenerateChartCallback {
        void onGenerateChartRequested(Date startDate, Date endDate);
    }
    
    /** Simple holder for date range data. */
    record DateRange(Date startDate, Date endDate) {}
} 