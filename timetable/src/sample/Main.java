package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.*;
;
import java.net.Socket;
import java.time.LocalDate;
import java.util.*;

public class Main extends Application {
    public static volatile HashMap<String, Socket> sockets = new HashMap<>();
    public static volatile HashMap<ListView<String>, String> userLists = new HashMap<>();

    public static HashMap<String, ArrayList<Subject>> subjects = new HashMap<>();
    public static HashMap<String, HashMap<String, ArrayList<Subject>>> filteredSubjects = new HashMap<>();
    public static HashMap<String, Student> studentNames = new HashMap<>();
    public static HashMap<Integer, ArrayList<Student>> groups = new HashMap<>();
    public static HashMap<Integer, HashMap<String, ArrayList<Subject>>> groupsSubjects = new HashMap<>();

    public static String currentChatStudent;
    public static LocalDate startDate = null;
    public static LocalDate endDate = null;

    public static volatile boolean shouldRunServer = true;

    void setDateConstraints(SubjectTime time) {
        LocalDate candidate = LocalDate.of(time.date.year, time.date.month, time.date.dayOfMonth);
        if (startDate == null)
            startDate = LocalDate.of(time.date.year, time.date.month, time.date.dayOfMonth);
        else {
            if (startDate.compareTo(candidate) > 0)
                startDate = candidate;
        }

        if (endDate == null)
            endDate = LocalDate.of(time.date.year, time.date.month, time.date.dayOfMonth);
        else {
            if (endDate.compareTo(candidate) < 0)
                endDate = candidate;
        }
    }

    void readSubjects() throws FileNotFoundException {
        File f = new File("StudentInfo\\subjectData.txt");
        Scanner s = new Scanner(f);

        while (s.hasNextLine()) {
            int group = Integer.parseInt(s.nextLine());
            int subjectCount = Integer.parseInt(s.nextLine());
            HashMap<String, ArrayList<Subject>> groupSubjects = new HashMap<>();
            for (int sub = 0; sub < subjectCount; ++sub) {
                String subjectName = s.nextLine();
                int dayCount = Integer.parseInt(s.nextLine());
                for (int i = 0; i < dayCount; ++i) {
                    String date = s.nextLine();

                    String hour = s.nextLine();
                    SubjectTime time = new SubjectTime(date, hour);
                    setDateConstraints(time);
                    Subject subject = new Subject(subjectName, time);

                    filteredSubjects.computeIfAbsent(subjectName, k -> new HashMap<>());
                    filteredSubjects.get(subjectName).computeIfAbsent(time.date.toString(), k -> new ArrayList<>());
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
        File f = new File("StudentInfo\\groupData.txt");
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
    public void stop() throws Exception {
        shouldRunServer = false;
        new Socket("localhost", 8050).close();
        System.exit(0);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        primaryStage.setTitle("Prisijungimas");
        primaryStage.setScene(new Scene(root, 800, 500));
        readSubjects();
        getStudents();
        primaryStage.show();
    }

    public static void main(String[] args) {
        Server s = new Server();
        new Thread(s).start();
        launch(args);
    }
}