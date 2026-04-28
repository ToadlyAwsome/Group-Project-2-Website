package edu.okcu.tablefx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

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
            messageLabel.setText("Please enter username and password.");
            return;
        }

        if (!studentRadio.isSelected() && !professorRadio.isSelected()) {
            messageLabel.setText("Select Student or Professor.");
            return;
        }

        try {
            if (professorRadio.isSelected()) {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
                Scene scene = new Scene(loader.load());

                Stage stage = new Stage();
                stage.setTitle("Professor Dashboard");
                stage.setScene(scene);
                stage.show();

                messageLabel.setText("Welcome Professor " + username);

            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("student.fxml"));
                Scene scene = new Scene(loader.load());

                Stage stage = new Stage();
                stage.setTitle("Student Dashboard");
                stage.setScene(scene);
                stage.show();

                messageLabel.setText("Welcome Student " + username);
            }

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error opening screen.");
        }
    }
}