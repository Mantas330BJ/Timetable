package sample;

import javafx.scene.layout.StackPane;

import java.util.HashSet;

public class Student {
    HashSet<StackPane> visitedSubjects;
    int group;

    Student() {
        visitedSubjects = new HashSet<>();
    }
}
