package edu.okcu.tablefx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PersonDialogController {
    @FXML TextField txtEmail;
    @FXML TextField txtAge;
    @FXML
    TextField txtFirstName;
    @FXML
    TextField txtLastName;
    @FXML
    Label dialogTitle;

    private Stage dialogStage;
    private Person createdPerson;
    private Person updatedPerson;


    public void onSave(ActionEvent actionEvent) {
        String firstName = txtFirstName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String email = txtEmail.getText().trim();
        int age = Integer.parseInt(txtAge.getText().trim());
        int id = 0;

        if (updatedPerson == null) {
            id = (int)Math.random() * 10000;
            createdPerson = new Person(firstName, lastName, email, age);
        } else {
            updatedPerson.setFirstName(firstName);
            updatedPerson.setLastName(lastName);
            updatedPerson.setEmail(email);
            updatedPerson.setAge(age);
            createdPerson = updatedPerson;
        }

        closeDialog();
    }

    public void onCancel(ActionEvent actionEvent) {
        this.createdPerson = null;
        closeDialog();
    }

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    public Person getCreatedPerson() {
        return this.createdPerson;
    }

    void setPerson(Person person) {
        updatedPerson = person;

        if (person == null) {
            dialogTitle.setText("Add Person");
            return;
        }

        dialogTitle.setText("Update Person");
        txtFirstName.setText(updatedPerson.getFirstName());
        txtLastName.setText(updatedPerson.getLastName());
        txtEmail.setText(updatedPerson.getEmail());
        txtAge.setText(String.valueOf(updatedPerson.getAge()));
    }

    private void closeDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
}
