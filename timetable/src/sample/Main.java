package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main extends Application {
                        //Date
    public static HashMap<String, ArrayList<Subject>> subjects = new HashMap<>();
                        //Name            Date
    public static HashMap<String, HashMap<String, ArrayList<Subject>>> filteredSubjects = new HashMap<>(); //todo: change to set
    public static HashMap<String, Student> studentNames = new HashMap<>();
    public static HashMap<Integer, ArrayList<Student>> groups = new HashMap<>();
    public static HashMap<Integer, HashMap<String, ArrayList<Subject>>> groupsSubjects = new HashMap<>(); //todo: this about datatypes
    public static Student currentStudent;
    public static Student loggedStudent;
    //todo: hashmap with visited subjects from file

    void readSubjects() throws FileNotFoundException {
        File f = new File("C:\\timetable\\subjectData.txt");
        Scanner s = new Scanner(f);
        while (s.hasNextLine()) {
            int group = Integer.parseInt(s.nextLine());
            int subjectCount = Integer.parseInt(s.nextLine());
            HashMap<String, ArrayList<Subject>> groupSubjects = new HashMap<>();
            for (int sub = 0; sub < subjectCount; ++sub) { //todo: maybe change to while--
                String subjectName = s.nextLine();
                int dayCount = Integer.parseInt(s.nextLine());
                for (int i = 0; i < dayCount; ++i) {
                    String date = s.nextLine();
                    String hour = s.nextLine();
                    SubjectTime time = new SubjectTime(date, hour);

                    Subject subject = new Subject(subjectName, time);

                    filteredSubjects.computeIfAbsent(subjectName, k -> new HashMap<>());
                    filteredSubjects.get(subjectName).computeIfAbsent(time.date.toString(), k -> new ArrayList<>()); //todo simplify calls
                    filteredSubjects.get(subjectName).get(time.date.toString()).add(subject);


                    subjects.computeIfAbsent(time.date.toString(), k -> new ArrayList<>());
                    subjects.get(time.date.toString()).add(subject);

                    groupSubjects.computeIfAbsent(time.date.toString(), k -> new ArrayList<>());
                    groupSubjects.get(time.date.toString()).add(subject);
                }
                groupsSubjects.put(group, groupSubjects);
            }
        }
        s.close();
    }

    void getStudents() throws FileNotFoundException {
        File f = new File("C:\\timetable\\groupData.txt");
        Scanner s = new Scanner(f);
        while (s.hasNextLine()) {
            int groupNumber = Integer.parseInt(s.nextLine());
            int studentCount = Integer.parseInt(s.nextLine());
            for (int i = 0; i < studentCount; ++i) {
                String name = s.nextLine();
                HashSet<String> visitedSubjects = new HashSet<>();
                int subjectCount = Integer.parseInt(s.nextLine());
                for (int j = 0; j < subjectCount; ++j) {
                    String date = s.nextLine();
                    String hour = s.nextLine();
                    visitedSubjects.add(date + "*" + hour);
                }
                groups.computeIfAbsent(groupNumber, k -> new ArrayList<>());
                Student student = new Student(groupNumber, name, visitedSubjects);
                groups.get(groupNumber).add(student);
                studentNames.put(name, student);
            }
        }
        s.close();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        primaryStage.setTitle("Prisijungimas");
        primaryStage.setScene(new Scene(root, 800, 500));
        readSubjects();
        getStudents();
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
