package shared.ui.searchable_list;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Class that accepts a runnable and implements DocumentListener that calls the runnable for each of the update methods.
 * The purpose of this class is to simplify creating a DocumentListener.
 */
public class DocumentUpdateListener implements DocumentListener {
    private final Runnable onUpdate;

    /**
     * @param onUpdate Function to call when an update happens.
     */
    public DocumentUpdateListener(Runnable onUpdate) {
        this.onUpdate = onUpdate;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        handleUpdate(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        handleUpdate(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        handleUpdate(e);
    }

    /**
     * Helper function wrapping the call to onUpdate.
     * @param e Update event.
     */
    private void handleUpdate(DocumentEvent e) {
        onUpdate.run();
    }
}