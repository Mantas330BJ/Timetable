package sample;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static sample.Main.*;


public class TimetableController extends Windows {
    public Label nameLabel;

    Color visitedColor = Color.rgb(100, 230, 230);
    Color notVisitedColor = Color.rgb(230, 230, 230);
    public Button weeksButton;
    public DatePicker datePicker;
    public GridPane gridPane;
    public static double x1, x2, y1, y2;
    public Pane pane;

    double deviation = 2;
    int daysInWeek = 7;
    double maxHour = 22, minHour = 8;
    String person = LoginController.name;
    ArrayList<Label> columnNames = new ArrayList<>();

    String[] days = new String[] {"Pirmadienis", "Antradienis", "Trečiadienis", "Ketvirtadienis", "Penktadienis", "Šeštadienis", "Sekmadienis"};
    LocalDate startingDay;

    Stack<StackPane> stacks = new Stack<>();

    public void initialize() {
        gridPane.setVisible(false);
        MainController.primaryStage.setOnCloseRequest(e -> { //todo: move to another controller??
            currentStudent = loggedStudent;
            try {
                FileWriter f = new FileWriter("C:\\timetable\\groupData.txt");
                for (Map.Entry<Integer, ArrayList<Student>> entry : Main.groups.entrySet()) {
                    f.write(entry.getKey() + "\n");
                    f.write(entry.getValue().size() + "\n");
                    for (Student student : entry.getValue()) {
                        f.write(student.name + "\n");
                        f.write(student.visitedSubjects.size() + "\n");
                        for (String s : student.visitedSubjects) {
                            String[] data = s.split("\\*");
                            f.write(data[0] + "\n");
                            f.write(data[1] + "\n");
                        }
                    }
                }
                f.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });
    }


    void setWeekDays() {
        String pattern = "MM-dd";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

        startingDay = datePicker.getValue();
        startingDay = startingDay.minusDays(startingDay.getDayOfWeek().getValue() - 1);
        LocalDate d = startingDay;

        if (columnNames.isEmpty()) {
            for (int i = 0; i < days.length; ++i, d = d.plusDays(1)) {
                Label n = new Label(dateFormatter.format(d) + " " + days[i]);
                gridPane.add(n, i + 1, 0);
                columnNames.add(n);
            }
        }
        else {
            for (int i = 0; i < days.length; ++i, d = d.plusDays(1)) {
                Label n = columnNames.get(i);
                n.setText(dateFormatter.format(d) + " " + days[i]);
            }
        }

    }

    String getTime(double startHour, double endHour) {
        int startH = (int)startHour, startM = (int)((startHour - startH) * 60), endH = (int)endHour, endM = (int)((endHour - endH) * 60);
        String startMins = "", endMins = "";
        if (startM < 10)
            startMins += "0";
        startMins += Integer.toString(startM);
        if (endM < 10)
            endMins += "0";
        endMins += Integer.toString(endM);

        return startH + ":" + startMins + " - " + endH + ":" + endMins;
    }

    void setAction(StackPane stack, Rectangle r, Subject s) {
        stack.setOnMousePressed(e -> {
            if (r.getFill().equals(notVisitedColor)) {
                r.setFill(visitedColor);
                currentStudent.visitedSubjects.add(s.time.toString());
            }
            else {
                r.setFill(notVisitedColor);
                currentStudent.visitedSubjects.remove(s.time.toString());
            }
        });
    }

    StackPane getRectangle(Rectangle r, int column, Subject s) {
        String subjectName = s.subjectName;
        double startHour = s.time.startHour;
        double endHour = s.time.endHour;
        double hourRange = maxHour - minHour;
        double cellWidth = (TimetableController.x2 - TimetableController.x1) / (daysInWeek + 1);
        double cellHeight = (TimetableController.y2 - TimetableController.y1) / (hourRange + 1);
        String time = getTime(startHour, endHour);

        r.setX(cellWidth * column);
        r.setY(cellHeight * (startHour - minHour + 1));
        r.setHeight(cellHeight * (endHour - startHour));
        r.setWidth(cellWidth);
        r.setStroke(Color.BLACK);
        r.setFill(notVisitedColor);

        StackPane stack = new StackPane();
        stack.setLayoutX(r.getX() + deviation);
        stack.setLayoutY(r.getY() + deviation);
        Text text = new Text(time + "\n" + subjectName);
        text.setWrappingWidth(cellWidth);
        stack.getChildren().addAll(r, text);
        if (MainController.mode == 0)
            setAction(stack, r, s);
        return stack;
    }

    public void showWeek() {
        if (datePicker.getValue() != null) {
            nameLabel.setText(MainController.timetableTopText);
            while (!stacks.empty())
                pane.getChildren().remove(stacks.pop());
            setWeekDays();

            x1 = gridPane.getLayoutX();
            x2 = x1 + gridPane.getWidth();
            y1 = gridPane.getLayoutY();
            y2 = y1 + gridPane.getHeight();
            gridPane.setVisible(true);

            String pattern = "YYYY M d";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
            LocalDate d = startingDay;

            HashSet<String> visitedSubjs = Main.currentStudent.visitedSubjects;
            for (int i = 0; i < days.length; ++i, d = d.plusDays(1)) {
                String curr = dateFormatter.format(d);
                ArrayList<Subject> daySubjects;
                if (MainController.mode <= 2)
                    daySubjects = groupsSubjects.get(Main.currentStudent.group).get(curr);
                else
                    daySubjects = filteredSubjects.get(MainController.chosen).get(curr);

                if (daySubjects != null) {
                    for (Subject s : daySubjects) {
                        if (MainController.mode != 1 || visitedSubjs.contains(s.time.toString())) {
                            int dayOfWeek = LocalDate.of(s.time.date.year, s.time.date.month, s.time.date.dayOfMonth).getDayOfWeek().getValue();
                            Rectangle r = new Rectangle();
                            StackPane stack = getRectangle(r, dayOfWeek, s);
                            pane.getChildren().add(stack);

                            if (MainController.mode <= 1 && visitedSubjs.contains(s.time.toString()))
                                r.setFill(visitedColor);
                            stacks.add(stack);
                        }
                    }
                }
            }
        }
        else
            showAlert("Pasirinkimo klaida.", "Nepasirinkta data.");
    }
}
