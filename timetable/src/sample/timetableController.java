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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static sample.Main.subjects;

public class timetableController extends Windows {
    public Button weeksButton;
    public DatePicker datePicker;
    public GridPane gridPane;
    public static double x1, x2, y1, y2;
    public Pane pane;

    double deviation = 2;
    int daysInWeek = 7;
    double maxHour = 22, minHour = 8;
    HashMap<String, StackPane> visitedSubjects = new HashMap<>();
    String person = loginController.name + " " + loginController.surname;
    ArrayList<Label> columnNames = new ArrayList<>();

    public void initialize() {
        gridPane.setVisible(false);
    }

    void setWeekDays() {
        String pattern = "MM-dd";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

        String[] days = new String[]{"Pirmadienis", "Antradienis", "Trečiadienis", "Ketvirtadienis", "Penktadienis", "Šeštadienis", "Sekmadienis"};
        LocalDate d = datePicker.getValue();
        d = d.minusDays(d.getDayOfWeek().getValue() - 1);

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
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Dalyko lankomumas");
            alert.setHeaderText("Ar " + person + " buvo šioje paskaitoje?");

            ButtonType yesButton = new ButtonType("Taip");
            ButtonType noButton = new ButtonType("Ne");

            alert.getButtonTypes().setAll(yesButton, noButton);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == yesButton) {
                r.setFill(Color.rgb(100, 230, 230));
                visitedSubjects.put(person, stack);
            }
            else if (result.get() == noButton) {
                r.setFill(Color.rgb(230,230,230));
                visitedSubjects.remove(person, stack);
            }
        });
    }

    void addRectangle(int column, double startHour, double endHour, String subjectName) {
        double hourRange = maxHour - minHour;
        double cellWidth = (timetableController.x2 - timetableController.x1) / (daysInWeek + 1);
        double cellHeight = (timetableController.y2 - timetableController.y1) / (hourRange + 1);
        String time = getTime(startHour, endHour);

        Rectangle r = new Rectangle();
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

        pane.getChildren().add(stack);
    }

    public void showWeek() {
        if (datePicker.getValue() != null) {
            setWeekDays();
            //todo: ordered set with times lower_bound upper_bound
            x1 = gridPane.getLayoutX();
            x2 = x1 + gridPane.getWidth();
            y1 = gridPane.getLayoutY();
            y2 = y1 + gridPane.getHeight();
            gridPane.setVisible(true);

            for (Subject sub : subjects) {
                for (SubjectTime t : sub.times) {
                    int dayOfWeek = LocalDate.of(t.year, t.month, t.dayOfMonth).getDayOfWeek().getValue();
                    addRectangle(dayOfWeek, t.startHour, t.endHour, sub.subjectName);
                }
            }
        }
        else
            showAlert("Pasirinkimo klaida.", "Nepasirinkta data.");
    }
}
