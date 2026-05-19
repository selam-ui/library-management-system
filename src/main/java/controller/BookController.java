package controller;

import dto.BookDTO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import service.BookService;
import service.impl.BookServiceImpl;
import util.SceneManager;

public class BookController {
    @FXML private TableView<BookDTO> table;
    @FXML private TableColumn<BookDTO, Number> idCol;
    @FXML private TableColumn<BookDTO, String> isbnCol;
    @FXML private TableColumn<BookDTO, String> titleCol;
    @FXML private TableColumn<BookDTO, String> authorCol;
    @FXML private TableColumn<BookDTO, String> categoryCol;
    @FXML private TableColumn<BookDTO, Number> totalCol;
    @FXML private TableColumn<BookDTO, Number> availableCol;

    @FXML private TextField isbnField, titleField, authorField, categoryField, totalField, availableField, searchField;
    @FXML private Label messageLabel;

    private final BookService service = new BookServiceImpl();
    private final ObservableList<BookDTO> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()));
        isbnCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIsbn()));
        titleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle()));
        authorCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAuthor()));
        categoryCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategory()));
        totalCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getTotalCopies()));
        availableCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getAvailableCopies()));
        table.setItems(data);
        table.getSelectionModel().selectedItemProperty().addListener((obs, o, sel) -> {
            if (sel != null) {
                isbnField.setText(sel.getIsbn());
                titleField.setText(sel.getTitle());
                authorField.setText(sel.getAuthor());
                categoryField.setText(sel.getCategory());
                totalField.setText(String.valueOf(sel.getTotalCopies()));
                availableField.setText(String.valueOf(sel.getAvailableCopies()));
            }
        });
        refresh();
    }

    private void refresh() {
        data.setAll(service.listAll());
    }

    private BookDTO fromFields() {
        BookDTO d = new BookDTO();
        d.setIsbn(isbnField.getText());
        d.setTitle(titleField.getText());
        d.setAuthor(authorField.getText());
        d.setCategory(categoryField.getText());
        try { d.setTotalCopies(Integer.parseInt(totalField.getText())); } catch (Exception e) { d.setTotalCopies(0); }
        try { d.setAvailableCopies(Integer.parseInt(availableField.getText())); } catch (Exception e) { d.setAvailableCopies(0); }
        return d;
    }

    @FXML
    public void onAdd() {
        try {
            service.add(fromFields());
            messageLabel.setText("Book added");
            refresh();
        } catch (Exception e) { messageLabel.setText(e.getMessage()); }
    }

    @FXML
    public void onUpdate() {
        BookDTO sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { messageLabel.setText("Select a book"); return; }
        try {
            BookDTO d = fromFields();
            d.setId(sel.getId());
            service.update(d);
            messageLabel.setText("Book updated");
            refresh();
        } catch (Exception e) { messageLabel.setText(e.getMessage()); }
    }

    @FXML
    public void onDelete() {
        BookDTO sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { messageLabel.setText("Select a book"); return; }
        service.delete(sel.getId());
        messageLabel.setText("Book deleted");
        refresh();
    }

    @FXML
    public void onSearch() {
        data.setAll(service.search(searchField.getText()));
    }

    @FXML
    public void onBack() { SceneManager.switchTo("/ui/Dashboard.fxml"); }
}
