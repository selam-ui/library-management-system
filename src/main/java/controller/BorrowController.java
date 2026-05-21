package controller;

import dto.BookDTO;
import dto.BorrowRecordDTO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import service.BookService;
import service.BorrowService;
import service.ReservationService;
import service.impl.BookServiceImpl;
import service.impl.BorrowServiceImpl;
import service.impl.ReservationServiceImpl;
import util.SceneManager;

public class BorrowController {
    @FXML private TableView<BorrowRecordDTO> borrowTable;
    @FXML private TableColumn<BorrowRecordDTO, Number> idCol;
    @FXML private TableColumn<BorrowRecordDTO, String> titleCol;
    @FXML private TableColumn<BorrowRecordDTO, String> isbnCol;
    @FXML private TableColumn<BorrowRecordDTO, String> borrowDateCol;
    @FXML private TableColumn<BorrowRecordDTO, String> dueDateCol;
    @FXML private TableColumn<BorrowRecordDTO, String> returnDateCol;
    @FXML private TableColumn<BorrowRecordDTO, String> statusCol;
    @FXML private ComboBox<BookDTO> bookComboBox;
    @FXML private TableView<BookDTO> availableTable;
    @FXML private TableColumn<BookDTO, Number> availIdCol;
    @FXML private TableColumn<BookDTO, String> availTitleCol;
    @FXML private TableColumn<BookDTO, String> availIsbnCol;
    @FXML private TableColumn<BookDTO, String> availAuthorCol;
    @FXML private TableColumn<BookDTO, String> availCategoryCol;
    @FXML private TableColumn<BookDTO, Number> availAvailableCol;
    @FXML private Label messageLabel;

    private final BorrowService borrowService = new BorrowServiceImpl();
    private final BookService bookService = new BookServiceImpl();
    private final ReservationService reservationService = new ReservationServiceImpl();
    private final ObservableList<BorrowRecordDTO> records = FXCollections.observableArrayList();
    private final ObservableList<BookDTO> availableBooks = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()));
        titleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBookTitle()));
        isbnCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIsbn()));
        borrowDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBorrowDate() != null ? c.getValue().getBorrowDate().toString() : ""));
        dueDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDueDate() != null ? c.getValue().getDueDate().toString() : ""));
        returnDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReturnDate() != null ? c.getValue().getReturnDate().toString() : ""));
        statusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus().name()));

        borrowTable.setItems(records);
        availableTable.setItems(availableBooks);
        bookComboBox.setItems(availableBooks);
        bookComboBox.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(BookDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitle() + " (" + item.getAvailableCopies() + " available)");
            }
        });
        bookComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(BookDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitle() + " (" + item.getAvailableCopies() + " available)");
            }
        });
        availIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()));
        availTitleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle()));
        availIsbnCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIsbn()));
        availAuthorCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAuthor()));
        availCategoryCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategory()));
        availAvailableCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getAvailableCopies()));

        refresh();
    }

    private void refresh() {
        records.setAll(borrowService.listBorrowRecords());
        borrowTable.refresh();
        availableBooks.setAll(bookService.listAll().stream()
                .filter(book -> book.getAvailableCopies() > 0)
                .toList());
    }

    @FXML
    public void onBorrow() {
        BookDTO book = bookComboBox.getValue();
        if (book == null) {
            messageLabel.setText("Select a book to borrow.");
            return;
        }
        try {
            borrowService.borrowBook(book.getId());
            messageLabel.setText("Borrow request completed.");
            bookComboBox.getSelectionModel().clearSelection();
            refresh();
        } catch (Exception e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    public void onReserveSelected() {
        BookDTO book = availableTable.getSelectionModel().getSelectedItem();
        if (book == null) {
            messageLabel.setText("Select a book to reserve.");
            return;
        }
        try {
            reservationService.reserveBook(book.getId());
            messageLabel.setText("Book reservation created.");
            refresh();
        } catch (Exception e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    public void onReturn() {
        BorrowRecordDTO record = borrowTable.getSelectionModel().getSelectedItem();
        if (record == null) {
            messageLabel.setText("Select a borrowed record to return.");
            return;
        }
        try {
            borrowService.returnBook(record.getId());
            messageLabel.setText("Book returned successfully.");
            borrowTable.getSelectionModel().clearSelection();
            refresh();
        } catch (Exception e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    public void onBack() {
        SceneManager.switchTo("/ui/Dashboard.fxml");
    }
}
