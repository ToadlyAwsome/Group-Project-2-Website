package edu.okcu.tablefx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Random;

public class Person {
    private static final Random RAND = new Random();

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private int age;

    public Person() {
        this.id = generateId();
        this.firstName = "New Course";
        this.lastName = "Professor";
        this.email = "Room";
        this.age = 0;
    }

    public Person(String firstName, String lastName, String email, int age) {
        this(generateId(), firstName, lastName, email, age);
    }

    public Person(int id, String firstName, String lastName, String email, int age) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    private static int generateId() {
        return RAND.nextInt(10000) + 1;
    }

    public static ObservableList<Person> getDummyData() {
        ObservableList<Person> people = FXCollections.observableArrayList();

        Person class1 = new Person(101, "Java II", "Dr. Smith", "CS201", 24);
        Person class2 = new Person(205, "Calculus", "Prof. Johnson", "MH110", 31);
        Person class3 = new Person(330, "Database", "Dr. Brown", "CS105", 18);
        Person class4 = new Person(410, "Physics", "Dr. Miller", "SCI220", 27);

        people.add(class1);
        people.add(class2);
        people.add(class3);
        people.add(class4);

        return people;
    }
}
