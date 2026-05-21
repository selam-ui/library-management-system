package controller;

import enumtypes.Role;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.User;
import service.UserService;
import service.impl.UserServiceImpl;
import util.SceneManager;

public class RegistrationController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private ComboBox<Role> roleComboBox;
    @FXML private Label messageLabel;

    private final UserService userService = new UserServiceImpl();

    @FXML
    public void onRegister() {
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showError("Passwords do not match.");
            return;
        }

        try {
            User user = new User();
            user.setUsername(usernameField.getText());
            user.setPassword(passwordField.getText());
            user.setFullName(fullNameField.getText());
            user.setEmail(emailField.getText());
            user.setRole(roleComboBox.getValue() != null ? roleComboBox.getValue() : Role.STUDENT);
            User registered = userService.register(user);
            if (registered.getRole() == Role.STUDENT) {
                messageLabel.setText("Student account created successfully. You may log in now.");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Registration Successful");
                alert.setHeaderText(null);
                alert.setContentText("Your student account is active. Please log in.");
                alert.showAndWait();
            } else {
                messageLabel.setText("Registration submitted. Wait for admin approval.");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Registration Submitted");
                alert.setHeaderText(null);
                alert.setContentText("Your librarian account request has been sent to the administrator for approval.");
                alert.showAndWait();
            }
            clearFields();
            SceneManager.switchTo("/ui/Login.fxml");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void onBack() {
        SceneManager.switchTo("/ui/Login.fxml");
    }

    private void showError(String message) {
        messageLabel.setText(message);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Registration Error");
        alert.setHeaderText("Unable to register");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        fullNameField.clear();
        emailField.clear();
        if (roleComboBox != null) {
            roleComboBox.setValue(Role.STUDENT);
        }
    }

    @FXML
    public void initialize() {
        if (roleComboBox != null) {
            roleComboBox.getItems().setAll(Role.STUDENT, Role.LIBRARIAN);
            roleComboBox.setValue(Role.STUDENT);
        }
    }
}
