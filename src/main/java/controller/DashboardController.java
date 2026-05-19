package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.User;
import service.AuthService;
import service.impl.AuthServiceImpl;
import session.SessionManager;
import util.SceneManager;

public class DashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;

    private final AuthService authService = new AuthServiceImpl();

    @FXML
    public void initialize() {
        User u = SessionManager.getCurrentUser();
        if (u != null) {
            welcomeLabel.setText("Welcome, " + (u.getFullName() != null ? u.getFullName() : u.getUsername()));
            roleLabel.setText("Role: " + u.getRole());
        }
    }

    @FXML public void onOpenBooks() { SceneManager.switchTo("/ui/Books.fxml"); }

    @FXML
    public void onLogout() {
        authService.logout();
        SceneManager.switchTo("/ui/Login.fxml");
    }
}
