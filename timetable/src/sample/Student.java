package sample;

import java.util.HashSet;

public class Student {
            //SubjectTime
    HashSet<String> visitedSubjects;
    String name;

    Student (String name, HashSet<String> visitedSubjects) {
        this.name = name;
        this.visitedSubjects = visitedSubjects;
    }
}
