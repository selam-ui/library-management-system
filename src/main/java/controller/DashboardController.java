package controller;

import dao.impl.BookDAOImpl;
import dao.impl.BorrowRecordDAOImpl;
import dao.impl.UserDAOImpl;
import dao.interfaces.BookDAO;
import dao.interfaces.BorrowRecordDAO;
import dao.interfaces.UserDAO;
import enumtypes.BorrowStatus;
import enumtypes.Role;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.Book;
import model.BorrowRecord;
import model.User;
import service.AuthService;
import service.impl.AuthServiceImpl;
import session.SessionManager;
import util.SceneManager;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;
    @FXML private Button booksButton;
    @FXML private Button borrowButton;
    @FXML private Button usersButton;
    @FXML private VBox statsPane;
    @FXML private Label totalBooksLabel;
    @FXML private Label borrowedBooksLabel;
    @FXML private Label overdueBooksLabel;
    @FXML private Label registeredUsersLabel;
    @FXML private Label mostBorrowedLabel;

    private final AuthService authService = new AuthServiceImpl();
    private final BookDAO bookDao = new BookDAOImpl();
    private final BorrowRecordDAO borrowDao = new BorrowRecordDAOImpl();
    private final UserDAO userDao = new UserDAOImpl();

    @FXML
    public void initialize() {
        User u = SessionManager.getCurrentUser();
        if (u != null) {
            welcomeLabel.setText("Welcome, " + (u.getFullName() != null ? u.getFullName() : u.getUsername()));
            roleLabel.setText("Role: " + u.getRole());
            boolean isAdmin = u.getRole() == Role.ADMIN;
            boolean isLibrarian = u.getRole() == Role.LIBRARIAN;
            booksButton.setVisible(isAdmin || isLibrarian);
            usersButton.setVisible(isAdmin);
            borrowButton.setVisible(true);
            statsPane.setVisible(isAdmin);
            if (isAdmin) loadStats();
        }
    }

    private void loadStats() {
        borrowDao.markOverdueRecords();
        List<Book> books = bookDao.findAll();
        List<BorrowRecord> records = borrowDao.findAll();

        totalBooksLabel.setText(String.valueOf(books.size()));
        borrowedBooksLabel.setText(String.valueOf(records.stream()
                .filter(r -> r.getStatus() == BorrowStatus.BORROWED)
                .count()));
        overdueBooksLabel.setText(String.valueOf(records.stream()
                .filter(r -> r.getStatus() == BorrowStatus.OVERDUE)
                .count()));
        registeredUsersLabel.setText(String.valueOf(userDao.findAll().size()));

        Map<Integer, Long> countByBook = records.stream()
                .collect(Collectors.groupingBy(BorrowRecord::getBookId, Collectors.counting()));
        mostBorrowedLabel.setText(countByBook.entrySet().stream()
                .max(Comparator.comparingLong(Map.Entry::getValue))
                .flatMap(entry -> bookDao.findById(entry.getKey()))
                .map(Book::getTitle)
                .orElse("N/A"));
    }

    @FXML
    public void onOpenBooks() {
        User u = SessionManager.getCurrentUser();
        if (u == null) return;
        if (u.getRole() != Role.ADMIN && u.getRole() != Role.LIBRARIAN) {
            return;
        }
        SceneManager.switchTo("/ui/Books.fxml");
    }

    @FXML
    public void onOpenBorrow() { SceneManager.switchTo("/ui/Borrow.fxml"); }

    @FXML
    public void onOpenUsers() {
        User u = SessionManager.getCurrentUser();
        if (u == null || u.getRole() != Role.ADMIN) return;
        SceneManager.switchTo("/ui/Users.fxml");
    }

    @FXML
    public void onLogout() {
        authService.logout();
        SceneManager.switchTo("/ui/Login.fxml");
    }
}
