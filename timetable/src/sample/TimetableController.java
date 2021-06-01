package sample;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static sample.Main.*;


public class TimetableController extends Windows implements dataPassing {
    public Label nameLabel;
    public ComboBox<Integer> yearsComboBox;
    public ComboBox<String> monthsComboBox;
    public Button previousWeekButton;
    public Button nextWeekButton;
    public Pane legendPane;
    public Rectangle beenRectangle;
    public Rectangle skippedRectangle;

    Color visitedColor = Color.rgb(100, 230, 230);
    Color notVisitedColor = Color.rgb(230, 230, 230);
    public Button weeksButton;
    public GridPane gridPane;
    public static double x1, x2, y1, y2;
    public Pane pane;

    double deviation = 1;
    int daysInWeek = 7;
    double maxHour = 22, minHour = 8;
    ArrayList<Label> columnNames = new ArrayList<>();

    String[] days = new String[] {"Pirmadienis", "Antradienis", "Trečiadienis", "Ketvirtadienis", "Penktadienis", "Šeštadienis", "Sekmadienis"};
    String[] months = {"Sausis", "Vasaris", "Kovas", "Balandis", "Gegužė", "Birželis", "Liepa", "Rugpjūtis", "Rugsėjis", "Spalis", "Lapkritis", "Gruodis"};

    HashMap<String, Integer> monthMap = new HashMap<>();

    LocalDate startingDay;

    Stack<StackPane> stacks = new Stack<>();
    Student currentStudent;

    int currentDay = 1;

    public void setData(Stage stage) {
        stage.setOnCloseRequest(e -> {
            try {
                FileWriter f = new FileWriter("StudentInfo\\groupData.txt");
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

                f = new FileWriter("StudentInfo\\datePick.txt");
                f.write(yearsComboBox.getValue() + "\n");
                f.write(monthsComboBox.getValue());
                f.close();

            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });
        currentStudent = (Student)stage.getUserData();
    }

    public void initialize() throws FileNotFoundException {
        previousWeekButton.setVisible(false);
        nextWeekButton.setVisible(false);
        legendPane.setVisible(false);

        ArrayList<Integer> years = new ArrayList<>();
        for (int year = 1900; year <= 2030; ++year)
            years.add(year);
        for (int i = 0; i < months.length; ++i) {
            monthMap.put(months[i], i + 1);
        }
        yearsComboBox.getItems().addAll(years);
        monthsComboBox.getItems().addAll(months);
        gridPane.setVisible(false);

        File f = new File("StudentInfo\\datePick.txt");
        Scanner s = new Scanner(f);
        if (!s.hasNextLine()) {
            yearsComboBox.getSelectionModel().select(Integer.valueOf(LocalDate.now().getYear()));
            monthsComboBox.getSelectionModel().select(months[LocalDate.now().getMonthValue() - 1]);
        }
        else {
            yearsComboBox.getSelectionModel().select(Integer.valueOf(s.nextLine()));
            monthsComboBox.getSelectionModel().select(s.nextLine());
        }
    }


    void setWeekDays() {
        String pattern = "MM-dd";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
        startingDay = LocalDate.of(yearsComboBox.getValue(), monthMap.get(monthsComboBox.getValue()), currentDay);
        LocalDate d = startingDay.minusDays(startingDay.getDayOfWeek().getValue() - 1);

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
        String time = s.time.getTime();

        r.setX(cellWidth * column);
        r.setY(cellHeight * (startHour - minHour + 1));
        r.setHeight(cellHeight * (endHour - startHour));
        r.setWidth(cellWidth - deviation);
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

    public void showNodes() {
        previousWeekButton.setVisible(true);
        nextWeekButton.setVisible(true);
        nameLabel.setText(MainController.timetableTopText);
        beenRectangle.setFill(visitedColor);
        skippedRectangle.setFill(notVisitedColor);
    }

    public void showMonth() {
        currentDay = 1;
        showWeek();
    }

    public void showWeek() {
        showNodes();
        legendPane.setVisible(MainController.mode == 0);

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
        LocalDate d = startingDay.minusDays(startingDay.getDayOfWeek().getValue() - 1);

        HashSet<String> visitedSubjs = currentStudent.visitedSubjects;
        for (int i = 0; i < days.length; ++i, d = d.plusDays(1)) {
            String curr = dateFormatter.format(d);
            ArrayList<Subject> daySubjects;
            if (MainController.mode <= 2)
                daySubjects = groupsSubjects.get(currentStudent.group).get(curr);
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

    public void showPreviousWeek() {
        currentDay = Math.max(currentDay - 7, 1);
        showWeek();
    }

    public void showNextWeek() {
        int maxDay = startingDay.lengthOfMonth();
        currentDay = Math.min(currentDay + 7, maxDay);
        showWeek();
    }
}
