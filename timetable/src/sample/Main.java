package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeSet;

public class Main extends Application {
    public static ArrayList<Subject> subjects = new ArrayList<>();
    //public static HashMap<Date, Subject> subjects = new HashMap<>();
    void readSubjects() throws FileNotFoundException {
        File f = new File("C:\\timetable\\subjectData.txt");
        Scanner s = new Scanner(f);
        while (s.hasNextLine()) {
            String subjectName = s.nextLine();
            ArrayList<SubjectTime> times = new ArrayList<>();

            int dayCount = Integer.parseInt(s.nextLine());
            for (int i = 0; i < dayCount; ++i) {
                String date = s.nextLine();
                String hour = s.nextLine();
                times.add(new SubjectTime(date, hour));
            }
            Subject subject = new Subject(subjectName, times);
            subjects.add(subject);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        primaryStage.setTitle("Prisijungimas");
        primaryStage.setScene(new Scene(root, 800, 500));
        readSubjects();
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
