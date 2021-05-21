package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static sample.Main.filteredSubjects;
import static sample.Main.subjects;


public class timetableController extends Windows {
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
    HashMap<String, StackPane> visitedSubjects = new HashMap<>(); //todo: seems weird should delete
    String person = loginController.name + " " + loginController.surname;
    ArrayList<Label> columnNames = new ArrayList<>();

    String[] days = new String[] {"Pirmadienis", "Antradienis", "Trečiadienis", "Ketvirtadienis", "Penktadienis", "Šeštadienis", "Sekmadienis"};
    LocalDate startingDay;

    Stack<StackPane> stacks = new Stack<>();

    public void initialize() {
        gridPane.setVisible(false);
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

    void setAction(StackPane stack, Rectangle r) {
        stack.setOnMousePressed(e -> {
            if (r.getFill().equals(notVisitedColor)) {
                r.setFill(visitedColor); //todo: save colour values in variables
                visitedSubjects.put(person, stack);
            }
            else
                r.setFill(notVisitedColor);
        });
    }

    StackPane getRectangle(Rectangle r, int column, double startHour, double endHour, String subjectName) {
        double hourRange = maxHour - minHour;
        double cellWidth = (timetableController.x2 - timetableController.x1) / (daysInWeek + 1);
        double cellHeight = (timetableController.y2 - timetableController.y1) / (hourRange + 1);
        String time = getTime(startHour, endHour);

        r.setX(cellWidth * column);
        r.setY(cellHeight * (startHour - minHour + 1));
        r.setHeight(cellHeight * (endHour - startHour));
        r.setWidth(cellWidth);
        r.setStroke(Color.BLACK);
        r.setFill(Color.rgb(230,230,230));

        StackPane stack = new StackPane();
        stack.setLayoutX(r.getX() + deviation);
        stack.setLayoutY(r.getY() + deviation);
        Text text = new Text(time + "\n" + subjectName);
        text.setWrappingWidth(cellWidth);
        stack.getChildren().addAll(r, text);
        setAction(stack, r);
        return stack;
    }

    public void showWeek() {
        if (datePicker.getValue() != null) {
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

            HashSet<String> visitedSubjs = Main.loggedStudent.visitedSubjects;
            for (int i = 0; i < days.length; ++i, d = d.plusDays(1)) {
                String curr = dateFormatter.format(d);
                if (subjects.get(curr) != null) {
                    for (Subject s : subjects.get(curr)) {
                        int dayOfWeek = LocalDate.of(s.time.date.year, s.time.date.month, s.time.date.dayOfMonth).getDayOfWeek().getValue();
                        Rectangle r = new Rectangle();
                        StackPane stack = getRectangle(r, dayOfWeek, s.time.startHour, s.time.endHour, s.subjectName);
                        pane.getChildren().add(stack);

                        if (visitedSubjs.contains(s.time.toString()))
                            r.setFill(visitedColor);
                        visitedSubjects.put(person, stack); //todo: remove this bullshit from 2 places
                        stacks.add(stack);
                    }
                }
            }
        }
        else
            showAlert("Pasirinkimo klaida.", "Nepasirinkta data.");
    }
}
