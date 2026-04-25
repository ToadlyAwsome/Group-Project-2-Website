package edu.okcu.tablefx;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private RadioButton studentRadio;
    @FXML private RadioButton professorRadio;
    @FXML private Label messageLabel;

    @FXML
    protected void onLoginClick() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter your username and password.");
            return;
        }

        if (!studentRadio.isSelected() && !professorRadio.isSelected()) {
            messageLabel.setText("Please select Student or Professor.");
            return;
        }

        if (studentRadio.isSelected()) {
            messageLabel.setText("Welcome, Student: " + username + "!");
        } else {
            messageLabel.setText("Welcome, Professor: " + username + "!");
        }
    }
}