package edu.okcu.tablefx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {

    public static void saveToFile(Path file, ObservableList<Person> people) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            writer.write("id,firstname,lastname,email,age");
            writer.newLine();
            for (Person p : people) {
                String line = String.format("%d,%s,%s,%s,%d",
                        p.getId(), p.getFirstName(),p.getLastName(),p.getEmail(), p.getAge());

                writer.write(line);
                writer.newLine();
            }
        }
    }

    public static ObservableList<Person> loadFromFile(Path file) throws IOException {
        ObservableList<Person> people = FXCollections.observableArrayList();

        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line = reader.readLine(); // Header
            while ((line = reader.readLine()) != null) {
                Person person = fromCSV(line);
                people.add(person);
            }
        }
        return people;
    }

    private static Person fromCSV(String line) {
        String[] data = line.split(",");

        Person person = new Person();
        person.setId(Integer.parseInt(data[0]));
        person.setFirstName(data[1]);
        person.setLastName(data[2]);
        person.setEmail(data[3]);
        person.setAge(Integer.parseInt(data[4]));

        return person;
    }
}
