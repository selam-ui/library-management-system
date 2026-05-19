package controller;

import dto.BookDTO;
import dto.ReservationDTO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import service.BookService;
import service.ReservationService;
import service.impl.BookServiceImpl;
import service.impl.ReservationServiceImpl;
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
    @FXML private Label notificationLabel;

    private final BookService service = new BookServiceImpl();
    private final ReservationService reservationService = new ReservationServiceImpl();
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
        refreshNotifications();
    }

    private void refreshNotifications() {
        try {
            int readyCount = reservationService.countReadyNotifications();
            notificationLabel.setText(readyCount > 0 ? "You have " + readyCount + " reserved book(s) ready to borrow." : "");
        } catch (Exception e) {
            notificationLabel.setText("");
        }
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
        } catch (Exception e) {
            messageLabel.setText(e.getMessage());
        }
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
        } catch (Exception e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    public void onDelete() {
        BookDTO sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { messageLabel.setText("Select a book"); return; }
        try {
            service.delete(sel.getId());
            messageLabel.setText("Book deleted");
            refresh();
        } catch (Exception e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    public void onSearch() {
        data.setAll(service.search(searchField.getText()));
    }

    @FXML
    public void onAll() {
        data.setAll(service.listAll());
        refreshNotifications();
    }

    @FXML
    public void onAvailable() {
        data.setAll(service.listAvailable());
        refreshNotifications();
    }

    @FXML
    public void onBorrowed() {
        data.setAll(service.listBorrowed());
        refreshNotifications();
    }

    @FXML
    public void onNewest() {
        data.setAll(service.listNewest());
        refreshNotifications();
    }

    @FXML
    public void onReserve() {
        BookDTO sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            messageLabel.setText("Select a book to reserve.");
            return;
        }
        if (sel.getAvailableCopies() > 0) {
            messageLabel.setText("This book is available now. Borrow it instead of reserving it.");
            return;
        }
        try {
            reservationService.reserveBook(sel.getId());
            messageLabel.setText("Book reserved. You will be notified when it is returned.");
            refreshNotifications();
        } catch (Exception e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    public void onShowReservations() {
        try {
            var reservations = reservationService.listMyReservations();
            if (reservations.isEmpty()) {
                messageLabel.setText("You have no reservations.");
                return;
            }
            StringBuilder builder = new StringBuilder();
            for (ReservationDTO r : reservations) {
                builder.append(r.getBookTitle())
                        .append(" (ISBN: ")
                        .append(r.getIsbn())
                        .append(") - ")
                        .append(r.getStatus())
                        .append(" - reserved at ")
                        .append(r.getReservedAt())
                        .append("\n");
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("My Reservations");
            alert.setHeaderText("Your reservation status");
            alert.setContentText(builder.toString());
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        } catch (Exception e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    public void onBack() {
        SceneManager.switchTo("/ui/Dashboard.fxml");
    }
}
