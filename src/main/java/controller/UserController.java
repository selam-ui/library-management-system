package controller;

import enumtypes.Role;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.User;
import service.UserService;
import service.impl.UserServiceImpl;
import util.SceneManager;

public class UserController {
    @FXML private TableView<User> table;
    @FXML private TableColumn<User, Number> idCol;
    @FXML private TableColumn<User, String> usernameCol;
    @FXML private TableColumn<User, String> fullNameCol;
    @FXML private TableColumn<User, String> emailCol;
    @FXML private TableColumn<User, Role> roleCol;
    @FXML private TableColumn<User, Boolean> approvedCol;
    @FXML private TableColumn<User, Boolean> activeCol;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private ComboBox<Role> roleComboBox;
    @FXML private Label messageLabel;

    private final UserService service = new UserServiceImpl();
    private final ObservableList<User> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        fullNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        approvedCol.setCellValueFactory(new PropertyValueFactory<>("approved"));
        activeCol.setCellValueFactory(new PropertyValueFactory<>("active"));

        table.setItems(data);
        roleComboBox.setItems(FXCollections.observableArrayList(Role.values()));

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            if (sel != null) {
                usernameField.setText(sel.getUsername());
                passwordField.clear();
                fullNameField.setText(sel.getFullName());
                emailField.setText(sel.getEmail());
                roleComboBox.setValue(sel.getRole());
            }
        });
        refresh();
    }

    private void refresh() {
        data.setAll(service.listAll());
    }

    @FXML
    public void onAdd() {
        try {
            User user = fromFields();
            service.create(user);
            messageLabel.setText("User created successfully.");
            refresh();
            clearFields();
        } catch (Exception e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    public void onUpdate() {
        User selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Select a user to update.");
            return;
        }
        try {
            User user = fromFields();
            user.setId(selected.getId());
            service.update(user);
            messageLabel.setText("User updated successfully.");
            refresh();
        } catch (Exception e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    public void onDelete() {
        User selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Select a user to delete.");
            return;
        }
        try {
            service.delete(selected.getId());
            messageLabel.setText("User deleted successfully.");
            refresh();
            clearFields();
        } catch (Exception e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    public void onApprove() {
        User selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Select a user to approve.");
            return;
        }
        try {
            service.approve(selected.getId());
            messageLabel.setText("User approved and activated.");
            refresh();
        } catch (Exception e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    public void onActivate() {
        User selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Select a user to activate.");
            return;
        }
        try {
            service.activate(selected.getId());
            messageLabel.setText("User activated.");
            refresh();
        } catch (Exception e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    public void onDeactivate() {
        User selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Select a user to deactivate.");
            return;
        }
        try {
            service.deactivate(selected.getId());
            messageLabel.setText("User deactivated.");
            refresh();
        } catch (Exception e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    public void onClear() {
        clearFields();
        table.getSelectionModel().clearSelection();
        messageLabel.setText("");
    }

    @FXML
    public void onBack() {
        SceneManager.switchTo("/ui/Dashboard.fxml");
    }

    private User fromFields() {
        User user = new User();
        user.setUsername(usernameField.getText());
        user.setPassword(passwordField.getText());
        user.setFullName(fullNameField.getText());
        user.setEmail(emailField.getText());
        user.setRole(roleComboBox.getValue());
        return user;
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        fullNameField.clear();
        emailField.clear();
        roleComboBox.setValue(null);
    }
}
