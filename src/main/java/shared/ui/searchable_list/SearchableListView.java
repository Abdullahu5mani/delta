package shared.ui.searchable_list;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Class that renders a list that can be searched and filtered.
 * @param <T> The type of object to include in the searchable list.
 */
public class SearchableListView<T> extends JPanel {
    public interface SelectionChangeListener<T> {
        void selectionChanged(T selectedItem);
    }

    private static final String SEARCH_BOX_LABEL = "Search";
    private static final String SELECTED_ITEM_LABEL_TEMPLATE = "Selected: %s";

    private static final int DEFAULT_MAX_ITEMS_SHOW = 10;
    private static final int DEFAULT_PREFERRED_WIDTH = 200;
    private static final int DEFAULT_PREFERRED_HEIGHT = 100;
    private static final int DEFAULT_SEARCH_BOX_WIDTH = 100;
    private static final int DEFAULT_SEARCH_BOX_HEIGHT = 20;

    private final List<T> allItems;
    private T selectedItem;
    private JLabel selectedItemLabel;
    private JList<T> list;
    private DefaultListModel<T> listModel;
    private JTextField searchField;
    private final List<SelectionChangeListener<T>> selectionListeners = new ArrayList<>();

    /**
     * @param allItems The items to list and search through.
     */
    public SearchableListView(List<T> allItems) {
        this.allItems = allItems;
        initLayout();
        initSearchBox();
        initSearchResults();
    }

    /**
     * Helper method initializing the layout to be called in the constructor.
     */
    private void initLayout() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    /**
     * Helper method initializing the search box to be called in the constructor.
     */
    private void initSearchBox() {
        JPanel searchTextPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchTextPanel.add(new JLabel(SEARCH_BOX_LABEL));
        searchTextPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectedItemLabel = new JLabel();
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(DEFAULT_SEARCH_BOX_WIDTH, DEFAULT_SEARCH_BOX_HEIGHT));
        searchField.getDocument().addDocumentListener(new DocumentUpdateListener(this::filter));
        searchTextPanel.add(searchField);
        searchTextPanel.add(selectedItemLabel);
        this.add(searchTextPanel);
    }

    /**
     * Helper method initializing the list of search results to be called in the constructor.
     */
    private void initSearchResults() {
        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        list.addListSelectionListener(e -> this.listSelectionListener());
        allItems.stream().limit(DEFAULT_MAX_ITEMS_SHOW).forEach(listModel::addElement);
        selectFirstItem();
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(DEFAULT_PREFERRED_WIDTH, DEFAULT_PREFERRED_HEIGHT));
        this.add(scrollPane);
    }

    /**
     * Function to run when an item is selected from the list.
     */
    private void listSelectionListener() {
        T selectedItemFromList = list.getSelectedValue();
        if (selectedItemFromList == null) return;
        selectedItem = list.getSelectedValue();
        selectedItemLabel.setText(SELECTED_ITEM_LABEL_TEMPLATE.formatted(selectedItem));
        for (SelectionChangeListener<T> listener : selectionListeners) {
            listener.selectionChanged(selectedItem);
        }
    }

    /**
     * Selects the first item from the search results box.
     * Useful when initializing the search box.
     */
    private void selectFirstItem() {
        if (listModel.getSize() > 0) {
            list.setSelectedIndex(0);
            selectedItem = list.getSelectedValue();
            selectedItemLabel.setText(SELECTED_ITEM_LABEL_TEMPLATE.formatted(selectedItem));
            for (SelectionChangeListener<T> listener : selectionListeners) {
                listener.selectionChanged(selectedItem);
            }
        }
    }

    /**
     * Helper method to filter the items based on the search text.
     */
    private void filter() {
        String searchTerm = searchField.getText().toLowerCase();
        listModel.clear();
        List<T> filteredItems = searchTerm.isEmpty()
                ? allItems
                : allItems.stream().filter(item -> item.toString().toLowerCase().startsWith(searchTerm)).toList();
        filteredItems.stream()
                .limit(DEFAULT_MAX_ITEMS_SHOW)
                .forEach(listModel::addElement);
    }

    /**
     * @return The selected item.
     */
    public T getSelectedItem() {
        return selectedItem;
    }

    /**
     * @param listener The function to run the selected element changes.
     */
    public void addSelectionChangedListener(SelectionChangeListener<T> listener) {
        selectionListeners.add(listener);
    }
}
