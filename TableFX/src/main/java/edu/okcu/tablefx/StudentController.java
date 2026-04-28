package edu.okcu.tablefx;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.concurrent.atomic.AtomicInteger;

public class StudentController {
    @FXML private TableView<StudentClass> classTable;
    @FXML private TableColumn<StudentClass, Number> idColumn;
    @FXML private TableColumn<StudentClass, String> codeColumn;
    @FXML private TableColumn<StudentClass, String> nameColumn;
    @FXML private TableColumn<StudentClass, String> profColumn;

    @FXML private TextField codeField;
    @FXML private TextField nameField;
    @FXML private TextField profField;

    private final ObservableList<StudentClass> classes = FXCollections.observableArrayList();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    @FXML
    public void initialize() {

        idColumn.setCellValueFactory(data -> data.getValue().idProperty());
        codeColumn.setCellValueFactory(data -> data.getValue().codeProperty());
        nameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        profColumn.setCellValueFactory(data -> data.getValue().profProperty());

        classTable.setItems(classes);



        classTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            if (sel != null) {
                codeField.setText(sel.getCode());
                nameField.setText(sel.getName());
                profField.setText(sel.getProf());
            } else {
                clearForm();
            }
        });
    }

    @FXML
    private void handleAddClass(ActionEvent event) {
        String code = codeField.getText().trim();
        String name = nameField.getText().trim();
        String prof = profField.getText().trim();

        if (code.isEmpty() && name.isEmpty() && prof.isEmpty()) {
            return;
        }

        StudentClass sc = new StudentClass(idGenerator.getAndIncrement(), code, name, prof);
        classes.add(sc);
        clearForm();
        classTable.getSelectionModel().select(sc);
    }

    @FXML
    private void handleUpdateClass(ActionEvent event) {
        StudentClass selected = classTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        String code = codeField.getText().trim();
        String name = nameField.getText().trim();
        String prof = profField.getText().trim();

        selected.setCode(code);
        selected.setName(name);
        selected.setProf(prof);

        // refresh table view to reflect changes
        classTable.refresh();
    }

    @FXML
    private void handleDeleteClass(ActionEvent event) {
        StudentClass selected = classTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        classes.remove(selected);
        clearForm();
    }

    private void clearForm() {
        codeField.clear();
        nameField.clear();
        profField.clear();
        classTable.getSelectionModel().clearSelection();
    }


    public static class StudentClass {
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty code;
        private final SimpleStringProperty name;
        private final SimpleStringProperty prof;

        public StudentClass(int id, String code, String name, String prof) {
            this.id = new SimpleIntegerProperty(id);
            this.code = new SimpleStringProperty(code);
            this.name = new SimpleStringProperty(name);
            this.prof = new SimpleStringProperty(prof);
        }

        public int getId() { return id.get(); }
        public SimpleIntegerProperty idProperty() { return id; }

        public String getCode() { return code.get(); }
        public void setCode(String c) { code.set(c); }
        public SimpleStringProperty codeProperty() { return code; }

        public String getName() { return name.get(); }
        public void setName(String n) { name.set(n); }
        public SimpleStringProperty nameProperty() { return name; }

        public String getProf() { return prof.get(); }
        public void setProf(String p) { prof.set(p); }
        public SimpleStringProperty profProperty() { return prof; }
    }
}
