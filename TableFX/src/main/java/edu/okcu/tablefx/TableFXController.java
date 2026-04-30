package edu.okcu.tablefx;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
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

    @FXML
    private TextArea studentListArea;

    private ObservableList<Person> people;

    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

        people = Person.getDummyData();
        personTableView.setItems(people);

        // LISTENER FOR SELECTED CLASS
        personTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                loadStudentsForClass(newSel);
            }
        });
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
        if (selectedPerson == null) return;

        if (!confirmDelete(selectedPerson)) return;

        people.remove(selectedPerson);
    }

    private boolean confirmDelete(Person person) {
        ButtonType deleteButton = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", deleteButton, cancelButton);
        alert.setTitle("Delete Class");
        alert.setHeaderText("Delete Selected Class?");
        alert.setContentText("Remove " + person.getFirstName());

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == deleteButton;
    }

    public void onUpdatePerson(ActionEvent actionEvent) throws IOException {
        Person selectedPerson = personTableView.getSelectionModel().getSelectedItem();
        if (selectedPerson == null) return;

        Person updatedPerson = showPersonDialog(selectedPerson);
        if (updatedPerson != null) {
            personTableView.refresh();
            personTableView.getSelectionModel().select(updatedPerson);
        }
    }

    private Person showPersonDialog(Person person) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("person-dialog.fxml"));
        Parent root = loader.load();

        PersonDialogController controller = loader.getController();

        Stage stage = new Stage();
        stage.setTitle(person == null ? "Add Class" : "Update Class");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);

        controller.setDialogStage(stage);
        controller.setPerson(person);

        stage.showAndWait();

        return controller.getCreatedPerson();
    }

    public void onLoadFromFile(ActionEvent actionEvent) {
        try {
            ObservableList<Person> allRecords = FileUtil.loadFromFile(Paths.get("./classes.txt"));
            people.setAll(allRecords);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Load Classes");
            alert.setContentText("Classes loaded successfully.");
            alert.showAndWait();

        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Load Error");
            alert.setContentText("Could not load classes.");
            alert.showAndWait();
        }
    }

    public void onSaveToFile(ActionEvent actionEvent) {
        try {
            FileUtil.saveToFile(Paths.get("./classes.txt"), people);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Save Classes");
            alert.setContentText("Classes saved successfully.");
            alert.showAndWait();

        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save Error");
            alert.setContentText("Could not save classes.");
            alert.showAndWait();
        }
    }

    // ✅ STUDENT LOADING METHOD (NOW CORRECT POSITION)
    private void loadStudentsForClass(Person selectedClass) {
        try {
            String className = selectedClass.getFirstName();
            String result = "";

            java.util.List<String> lines = java.nio.file.Files.readAllLines(java.nio.file.Paths.get("students.txt"));
            for (String line : lines) {
                if (line.startsWith(className)) {
                    String[] parts = line.split(":");
                    if (parts.length > 1) {
                        result = parts[1].trim().replace(",", "\n");
                    }
                    break;
                }
            }

            if (result.isEmpty()) {
                result = "No students found.";
            }

            studentListArea.setText(result);

        } catch (Exception e) {
            studentListArea.setText("Error loading students.");
        }
    }
}