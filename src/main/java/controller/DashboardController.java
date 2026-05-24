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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
    @FXML private Label accountTypeLabel;
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
<<<<<<< HEAD
            roleLabel.setText("Signed in as: " + u.getUsername());
            String accountType = switch (u.getRole()) {
                case STUDENT -> "Student";
                case LIBRARIAN -> "Librarian";
                default -> "Administrator";
            };
            accountTypeLabel.setText(accountType);
            accountTypeLabel.getStyleClass().removeAll("student-badge", "librarian-badge", "admin-badge");
            accountTypeLabel.getStyleClass().add(u.getRole() == Role.STUDENT ? "student-badge"
                    : u.getRole() == Role.LIBRARIAN ? "librarian-badge" : "admin-badge");

=======
            roleLabel.setText("Role: " + u.getRole());
            accountTypeLabel.setText("Account type: " + (u.getRole() == Role.STUDENT ? "Student" : u.getRole() == Role.LIBRARIAN ? "Librarian" : "Administrator"));
>>>>>>> 372de1882b118596593011b05e9bf254c82a1182
            boolean isAdmin = u.getRole() == Role.ADMIN;
            boolean isLibrarian = u.getRole() == Role.LIBRARIAN;
            booksButton.setVisible(isAdmin || isLibrarian);
            usersButton.setVisible(isAdmin);
            borrowButton.setVisible(true);
            statsPane.setVisible(isAdmin);
            if (isAdmin) loadStats();
        }
    }

    private boolean confirm(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, content, ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle(title);
        alert.setHeaderText(header);
        return alert.showAndWait().filter(ButtonType.OK::equals).isPresent();
    }

    @FXML
    public void onOpenBorrow() {
        if (confirm("Confirm Navigation", "Open Borrow Books page?", "Proceed to Borrow Books?")) {
            SceneManager.switchTo("/ui/Borrow.fxml");
        }
    }

    @FXML
    public void onOpenUsers() {
        User u = SessionManager.getCurrentUser();
        if (u == null || u.getRole() != Role.ADMIN) return;
        if (confirm("Confirm Navigation", "Open Manage Users page?", "Proceed to Manage Users?")) {
            SceneManager.switchTo("/ui/Users.fxml");
        }
    }

    @FXML
    public void onLogout() {
        if (confirm("Confirm Logout", "Logout from the system?", "Are you sure you want to log out?")) {
            authService.logout();
            SceneManager.switchTo("/ui/Login.fxml");
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
<<<<<<< HEAD
=======

    @FXML
    public void onOpenBorrow() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Proceed to Borrow Books?", ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle("Confirm Navigation");
        alert.setHeaderText("Open Borrow Books page?");
        if (alert.showAndWait().filter(ButtonType.OK::equals).isPresent()) {
            SceneManager.switchTo("/ui/Borrow.fxml");
        }
    }

    @FXML
    public void onOpenUsers() {
        User u = SessionManager.getCurrentUser();
        if (u == null || u.getRole() != Role.ADMIN) return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Proceed to Manage Users?", ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle("Confirm Navigation");
        alert.setHeaderText("Open Manage Users page?");
        if (alert.showAndWait().filter(ButtonType.OK::equals).isPresent()) {
            SceneManager.switchTo("/ui/Users.fxml");
        }
    }

    @FXML
    public void onLogout() {
        authService.logout();
        SceneManager.switchTo("/ui/Login.fxml");
    }
>>>>>>> 372de1882b118596593011b05e9bf254c82a1182
}
