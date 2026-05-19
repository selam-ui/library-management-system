package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
            messageLabel.setText("Login failed: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText("Unable to sign in");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void onRegister() {
        SceneManager.switchTo("/ui/Registration.fxml");
    }
}
