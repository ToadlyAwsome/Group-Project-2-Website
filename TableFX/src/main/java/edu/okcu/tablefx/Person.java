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
        this.firstName = "NA";
        this.lastName = "NA";
        this.email = "NA";
        this.age = 100;
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

        Person person1 = new Person(1, "Jeff", "Maxwell", "jmaxwell@okcu.edu", 55);
        Person person2 = new Person(2, "Tom", "Hanks", "tom@hanks.com", 65);

        people.add(person1);
        people.add(person2);

        return people;
    }
}
