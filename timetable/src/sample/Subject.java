package sample;

import java.util.ArrayList;

public class Subject {
    String subjectName;
    ArrayList<SubjectTime> times;

    Subject (String subjectName, ArrayList<SubjectTime> times) {
        this.subjectName = subjectName;
        this.times = times;
    }
}
