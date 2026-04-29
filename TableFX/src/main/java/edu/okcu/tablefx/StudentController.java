package edu.okcu.tablefx;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

    // Add a path to the data file (relative to working directory)
    private static final Path DATA_FILE = Paths.get("students.txt");

    @FXML
    public void initialize() {

        idColumn.setCellValueFactory(data -> data.getValue().idProperty());
        codeColumn.setCellValueFactory(data -> data.getValue().codeProperty());
        nameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        profColumn.setCellValueFactory(data -> data.getValue().profProperty());

        classTable.setItems(classes);

        // load persisted classes from file
        loadFromFile();

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

        // persist changes
        saveToFile();
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

        // persist changes
        saveToFile();
    }

    @FXML
    private void handleDeleteClass(ActionEvent event) {
        StudentClass selected = classTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        classes.remove(selected);
        clearForm();

        // persist changes
        saveToFile();
    }

    private void clearForm() {
        codeField.clear();
        nameField.clear();
        profField.clear();
        classTable.getSelectionModel().clearSelection();
    }

    // Load classes from students.txt (format: id|CourseRoom|CourseName|Professor)
    private void loadFromFile() {
        if (!Files.exists(DATA_FILE)) {
            // nothing to load
            return;
        }
        try {
            List<String> lines = Files.readAllLines(DATA_FILE, StandardCharsets.UTF_8);
            int maxId = 0;
            classes.clear();
            for (String line : lines) {
                if (line == null || line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|", -1);
                if (parts.length < 4) continue;
                int id;
                try {
                    id = Integer.parseInt(parts[0]);
                } catch (NumberFormatException e) {
                    continue;
                }
                String code = parts[1];
                String name = parts[2];
                String prof = parts[3];
                classes.add(new StudentClass(id, code, name, prof));
                if (id > maxId) maxId = id;
            }
            idGenerator.set(maxId + 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save all classes to students.txt (overwrites file)
    private void saveToFile() {
        try {
            // prepare lines
            List<String> lines = classes.stream()
                    .map(sc -> sc.getId() + "|" + sc.getCode() + "|" + sc.getName() + "|" + sc.getProf())
                    .collect(Collectors.toList());
            // ensure parent exists (if using a nested path) then write
            Files.write(DATA_FILE, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // NEW: allow explicit Save from the UI
    @FXML
    private void handleSaveFile(ActionEvent event) {
        saveToFile();
    }

    // NEW: allow explicit Load from the UI (refresh the table)
    @FXML
    private void handleLoadFile(ActionEvent event) {
        loadFromFile();
        // ensure the TableView shows the newly loaded data
        classTable.setItems(classes);
        classTable.refresh();
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
