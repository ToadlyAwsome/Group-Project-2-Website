package edu.okcu.tablefx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.ArrayList;

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
                // Student branch: record credentials and pass them to StudentController
                FXMLLoader loader = new FXMLLoader(getClass().getResource("student.fxml"));
                Parent root = loader.load();

                // append/update credentials to actuallStudents.txt (unique by username)
                Path credsFile = Paths.get("actuallStudents.txt");
                try {
                    List<String> existing = Files.exists(credsFile)
                            ? Files.readAllLines(credsFile, StandardCharsets.UTF_8)
                            : new ArrayList<>();

                    String newEntry = username + "|" + password;
                    boolean updated = false;
                    List<String> out = new ArrayList<>();

                    for (String line : existing) {
                        if (line == null || line.trim().isEmpty()) continue;
                        String[] parts = line.split("\\|", -1);
                        String existingUser = parts.length > 0 ? parts[0].trim() : "";
                        if (existingUser.equals(username)) {
                            // replace with new entry (update password if different)
                            out.add(newEntry);
                            updated = true;
                        } else {
                            out.add(line);
                        }
                    }
                    if (!updated) {
                        out.add(newEntry);
                    }

                    Files.write(credsFile, out, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                Scene scene = new Scene(root);
                // pass credentials to StudentController
                StudentController controller = loader.getController();
                if (controller != null) {
                    controller.setCredentials(username, password);
                }

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