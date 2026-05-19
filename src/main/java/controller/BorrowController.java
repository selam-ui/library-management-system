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
import service.impl.BookServiceImpl;
import service.impl.BorrowServiceImpl;
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
    @FXML private Label messageLabel;

    private final BorrowService borrowService = new BorrowServiceImpl();
    private final BookService bookService = new BookServiceImpl();
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

        refresh();
    }

    private void refresh() {
        records.setAll(borrowService.listBorrowRecords());
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
