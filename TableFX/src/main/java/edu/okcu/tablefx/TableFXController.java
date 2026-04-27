package edu.okcu.tablefx;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class TableFXController {
    @FXML
    TableView<Person> personTableView;
    @FXML
    TableColumn<Person, Integer> idColumn;
    @FXML
    TableColumn<Person, String> firstNameColumn;
    @FXML
    TableColumn<Person, String> lastNameColumn;
    @FXML
    TableColumn<Person, String> emailColumn;
    @FXML
    TableColumn<Person, Integer> ageColumn;

    private ObservableList<Person> people;

    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

        people = Person.getDummyData();
        personTableView.setItems(people);
    }

    @FXML
    public void onAddPerson(ActionEvent actionEvent) throws IOException {
        Person newPerson = showPersonDialog(null);
        if (newPerson != null) {
            people.add(newPerson);
        }
    }

    public void onDeletePerson(ActionEvent actionEvent) {
        Person selectedPerson = personTableView.getSelectionModel().getSelectedItem();
        if (selectedPerson == null) {
            return;
        }

        if (confirmDelete(selectedPerson) == false) {
            return;
        }

        people.remove(selectedPerson);
    }

    private boolean confirmDelete(Person person) {
        ButtonType deleteButton = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", deleteButton, cancelButton);
        alert.setTitle("Delete Person");
        alert.setHeaderText("Delete Selected Person?");
        alert.setContentText("Remove " + person.getEmail() + " " + person.getLastName());
        Optional<ButtonType> result = alert.showAndWait();

        return result.isPresent() && result.get() == deleteButton;
    }

    public void onUpdatePerson(ActionEvent actionEvent) throws IOException {
        Person selectedPerson = personTableView.getSelectionModel().getSelectedItem();
        if (selectedPerson == null) {
            return;
        }

        Person updatedPerson = showPersonDialog(selectedPerson);
        if (updatedPerson != null) {
            personTableView.refresh();
            personTableView.getSelectionModel().select((updatedPerson));
        }
    }

    private Person showPersonDialog(Person person) throws IOException {
        try {
            String title = "Update Person";
            FXMLLoader loader = new FXMLLoader(getClass().getResource("person-dialog.fxml"));
            Parent root = loader.load();

            PersonDialogController controller = loader.getController();

            Stage stage = new Stage();
            if (person == null) {
                title = "Add Person";
            }
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);

            controller.setDialogStage(stage);
            controller.setPerson(person);

            stage.showAndWait();

            Person personRecord = controller.getCreatedPerson();
            return personRecord;
        } catch (IOException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    public void onLoadFromFile(ActionEvent actionEvent) {
        try {
            ObservableList<Person> allRecords = FileUtil.loadFromFile(Paths.get("./classes.txt"));
            people.setAll(allRecords);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Load Classes");
            alert.setHeaderText(null);
            alert.setContentText("Classes loaded successfully.");
            alert.showAndWait();

        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Load Error");
            alert.setHeaderText(null);
            alert.setContentText("Could not load classes from file.");
            alert.showAndWait();
        }
    }

    public void onSaveToFile(ActionEvent actionEvent) {
        try {
            FileUtil.saveToFile(Paths.get("./classes.txt"), people);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Save Classes");
            alert.setHeaderText(null);
            alert.setContentText("Classes saved successfully.");
            alert.showAndWait();

        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save Error");
            alert.setHeaderText(null);
            alert.setContentText("Could not save classes to file.");
            alert.showAndWait();
        }
    }
}