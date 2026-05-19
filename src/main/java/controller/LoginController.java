package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.User;
import service.AuthService;
import service.impl.AuthServiceImpl;
import util.SceneManager;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private final AuthService authService = new AuthServiceImpl();

    @FXML
    public void onLogin() {
        try {
            User u = authService.login(usernameField.getText(), passwordField.getText());
            messageLabel.setText("Welcome, " + (u.getFullName() != null ? u.getFullName() : u.getUsername()));
            SceneManager.switchTo("/ui/Dashboard.fxml");
        } catch (Exception e) {
            messageLabel.setText(e.getMessage());
        }
    }
}
