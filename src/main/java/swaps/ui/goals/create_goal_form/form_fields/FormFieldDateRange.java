package swaps.ui.goals.create_goal_form.form_fields;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Form field for selecting a date range for swap generation.
 * Only shows dates that have meal logs available.
 */
public class FormFieldDateRange extends JPanel {
    private JComboBox<DateOption> fromDateCombo;
    private JComboBox<DateOption> toDateCombo;
    private List<Date> availableDates;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy");

    /**
     * Wrapper class for date options in the combo box.
     */
    public static class DateOption {
        private final Date date;
        private final String label;

        public DateOption(Date date, String label) {
            this.date = date;
            this.label = label;
        }

        public Date getDate() {
            return date;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public FormFieldDateRange() {
        super(new GridBagLayout());
        initializeComponents();
    }

    private void initializeComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // From date label and combo
        gbc.gridx = 0; gbc.gridy = 0;
        this.add(new JLabel("From Date:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        fromDateCombo = new JComboBox<>();
        this.add(fromDateCombo, gbc);

        // To date label and combo
        gbc.gridx = 0; gbc.gridy = 1;
        this.add(new JLabel("To Date:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        toDateCombo = new JComboBox<>();
        this.add(toDateCombo, gbc);

        // Initially disabled until dates are loaded
        fromDateCombo.setEnabled(false);
        toDateCombo.setEnabled(false);
    }

    /**
     * Sets the available dates for selection.
     * 
     * @param availableDates List of dates that have meal logs
     */
    public void setAvailableDates(List<Date> availableDates) {
        this.availableDates = availableDates;
        updateDateCombos();
    }

    private void updateDateCombos() {
        fromDateCombo.removeAllItems();
        toDateCombo.removeAllItems();

        if (availableDates == null || availableDates.isEmpty()) {
            fromDateCombo.addItem(new DateOption(null, "No meal logs available"));
            toDateCombo.addItem(new DateOption(null, "No meal logs available"));
            fromDateCombo.setEnabled(false);
            toDateCombo.setEnabled(false);
            return;
        }

        // Populate combo boxes with available dates
        for (Date date : availableDates) {
            String label = DATE_FORMAT.format(date);
            
            // Add special labels for today/yesterday
            Date today = new Date();
            if (isSameDay(date, today)) {
                label = "Today (" + label + ")";
            } else {
                long daysDiff = (today.getTime() - date.getTime()) / (1000 * 60 * 60 * 24);
                if (daysDiff == 1) {
                    label = "Yesterday (" + label + ")";
                }
            }
            
            DateOption option = new DateOption(date, label);
            fromDateCombo.addItem(option);
            toDateCombo.addItem(option);
        }

        // Set default selection (latest available date)
        if (!availableDates.isEmpty()) {
            int lastIndex = availableDates.size() - 1;
            fromDateCombo.setSelectedIndex(lastIndex);
            toDateCombo.setSelectedIndex(lastIndex);
        }

        fromDateCombo.setEnabled(true);
        toDateCombo.setEnabled(true);
    }

    private boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }

    /**
     * @return The selected from date, or null if none selected
     */
    public Date getFromDate() {
        DateOption selected = (DateOption) fromDateCombo.getSelectedItem();
        return selected != null ? selected.getDate() : null;
    }

    /**
     * @return The selected to date, or null if none selected
     */
    public Date getToDate() {
        DateOption selected = (DateOption) toDateCombo.getSelectedItem();
        return selected != null ? selected.getDate() : null;
    }

    /**
     * @return True if valid dates are selected
     */
    public boolean hasValidDateRange() {
        Date fromDate = getFromDate();
        Date toDate = getToDate();
        return fromDate != null && toDate != null && !fromDate.after(toDate);
    }

    /**
     * @return Error message if date range is invalid, null if valid
     */
    public String getValidationError() {
        Date fromDate = getFromDate();
        Date toDate = getToDate();
        
        if (fromDate == null || toDate == null) {
            return "Please select both from and to dates";
        }
        
        if (fromDate.after(toDate)) {
            return "From date must be before or equal to to date";
        }
        
        return null;
    }
}
