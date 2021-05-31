package sample;

import java.util.HashSet;

public class Student {
    HashSet<String> visitedSubjects;
    public String name;
    int group;

    Student (int group, String name, HashSet<String> visitedSubjects) {
        this.group = group;
        this.name = name;
        this.visitedSubjects = visitedSubjects;
    }
}
