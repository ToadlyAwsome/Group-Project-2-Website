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
import java.util.ArrayList;
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

    // current logged in user/pw (set by LoginController)
    private String currentUser = null;
    private String currentPass = null;

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

    // Public setter called by LoginController after loading the FXML
    public void setCredentials(String username, String password) {
        this.currentUser = username;
        this.currentPass = password;
        loadFromFile();
        classTable.setItems(classes);
        classTable.refresh();
    }

    @FXML
    private void handleAddClass(ActionEvent event) {
        if (!ensureLoggedIn()) return;

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

        // persist changes for current user
        saveToFile();
    }

    @FXML
    private void handleUpdateClass(ActionEvent event) {
        if (!ensureLoggedIn()) return;

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
        if (!ensureLoggedIn()) return;

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

    // NEW: simple check that a user is logged in; show a brief alert if not.
    private boolean ensureLoggedIn() {
        if (currentUser != null && currentPass != null) return true;
        Alert a = new Alert(Alert.AlertType.WARNING, "Please log in before modifying classes.", ButtonType.OK);
        a.showAndWait();
        return false;
    }

    // Load classes from students.txt (supports both formats:
    // old: username|password|id|CourseRoom|CourseName|Professor
    // new: username|id|CourseRoom|CourseName|Professor )
    private void loadFromFile() {
        classes.clear();
        idGenerator.set(1);
        if (currentUser == null) {
            return;
        }
        if (!Files.exists(DATA_FILE)) {
            return;
        }
        try {
            List<String> lines = Files.readAllLines(DATA_FILE, StandardCharsets.UTF_8);
            int maxId = 0;
            for (String line : lines) {
                if (line == null || line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|", -1);
                if (parts.length < 3) continue;

                // owner is always at index 0
                String u = parts[0];
                if (!u.equals(currentUser)) continue;

                // determine id index: old format has id at index 2, new format at index 1
                int idIndex;
                if (parts.length >= 6) {
                    // likely old format: username|password|id|code|name|prof
                    idIndex = 2;
                } else {
                    // new format: username|id|code|name|prof
                    idIndex = 1;
                }
                if (parts.length <= idIndex) continue;

                int id;
                try {
                    id = Integer.parseInt(parts[idIndex]);
                } catch (NumberFormatException ex) {
                    continue;
                }

                // compute indices for code/name/prof relative to idIndex
                int codeIdx = idIndex + 1;
                int nameIdx = idIndex + 2;
                int profIdx = idIndex + 3;
                String code = codeIdx < parts.length ? parts[codeIdx] : "";
                String name = nameIdx < parts.length ? parts[nameIdx] : "";
                String prof = profIdx < parts.length ? parts[profIdx] : "";

                classes.add(new StudentClass(id, code, name, prof));
                if (id > maxId) maxId = id;
            }
            idGenerator.set(maxId + 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save all classes for the current user to students.txt (preserve other users' lines)
    // Normalized format written: username|id|CourseRoom|CourseName|Professor
    private void saveToFile() {
        if (currentUser == null) {
            Alert a = new Alert(Alert.AlertType.WARNING, "Please log in before saving.", ButtonType.OK);
            a.showAndWait();
            return;
        }
        try {
            List<String> existing = Files.exists(DATA_FILE)
                    ? Files.readAllLines(DATA_FILE, StandardCharsets.UTF_8)
                    : new ArrayList<>();

            // Keep lines that are NOT for the current user (compare username at index 0)
            List<String> others = existing.stream()
                    .filter(line -> {
                        if (line == null || line.trim().isEmpty()) return true;
                        String[] parts = line.split("\\|", -1);
                        if (parts.length == 0) return true;
                        String u = parts[0];
                        return !u.equals(currentUser);
                    })
                    .collect(Collectors.toList());

            // Append current user's class lines in normalized format
            List<String> myLines = classes.stream()
                    .map(sc -> currentUser + "|" + sc.getId() + "|" + escape(sc.getCode()) + "|" + escape(sc.getName()) + "|" + escape(sc.getProf()))
                    .collect(Collectors.toList());

            others.addAll(myLines);

            // write back entire file
            Files.write(DATA_FILE, others, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper to escape pipe characters in fields (simple replacement)
    private String escape(String s) {
        return s == null ? "" : s.replace("|", " ");
    }

    // NEW: allow explicit Save from the UI
    @FXML
    private void handleSaveFile(ActionEvent event) {
        saveToFile();
    }

    // NEW: allow explicit Load from the UI (reload from file for current user)
    @FXML
    private void handleLoadFile(ActionEvent event) {
        loadFromFile();
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
