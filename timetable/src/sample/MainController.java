package sample;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import static sample.Main.filteredSubjects;
import static sample.Main.groupsSubjects;

public class MainController {
    Map<String, Integer> modes = Map.of("Vartotojas", 1, "Grupė", 2, "Mokomasis dalykas", 3);

    public Button timeTableButton;
    public static Stage primaryStage = new Stage();
    public ComboBox<String> filtersBox;
    public ComboBox<String> filteredBox;
    String FONT = "C:\\timetable\\resources\\FreeSans.ttf";


    public static String timetableTopText;
    public static int mode;
    public static String chosen;
    public Pane pane; //todo: remove later !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    public void initialize() throws IOException {
        filtersBox.getItems().addAll("Vartotojas", "Grupė", "Mokomasis dalykas");
        filtersBox.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
            filteredBox.getItems().clear();
            if (newValue.equals("Vartotojas")) {
                filteredBox.getItems().addAll(Main.studentNames.keySet());
            }
            else if (newValue.equals("Grupė")) {
                int range = Main.groupsSubjects.keySet().size();
                String[] groupValues = new String[range];
                int i = 0;
                for (int key : Main.groupsSubjects.keySet())
                    groupValues[i++] = Integer.toString(key);
                filteredBox.getItems().addAll(groupValues);
            }
            else {
                filteredBox.getItems().addAll(Main.filteredSubjects.keySet());
            }

            filteredBox.getSelectionModel().selectFirst();
        });
        filtersBox.getSelectionModel().selectFirst();

    }

    public void loadTimetableScene() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("timetable.fxml"));
        primaryStage.setTitle("Tvarkaraštis");
        primaryStage.setScene(new Scene(root, 1300, 900));
        primaryStage.show();
    }

    public void showChat() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("chat.fxml"));
        primaryStage.setTitle("Pokalbiai");
        primaryStage.setScene(new Scene(root, 1300, 900));
        primaryStage.show();
    }

    public void showTimetable() throws IOException {
        mode = 0;
        timetableTopText = "Studentas: " + Main.loggedStudent.name;
        loadTimetableScene();
    }

    public void getData() {
        chosen = filteredBox.getValue();
        mode = modes.get(filtersBox.getValue());
        if (mode == 1)
            Main.currentStudent = Main.studentNames.get(chosen);
        else if (mode == 2)
            Main.currentStudent = Main.groups.get(Integer.parseInt(chosen)).get(0); //todo: think about this
    }

    public void filter() throws IOException {
        getData();
        if (mode == 1) {
            timetableTopText = "Studentas: " + chosen;
        }
        else if (mode == 2) {
            timetableTopText = "Grupė: " + chosen;
        }
        else {
            timetableTopText = "Mokomasis dalykas: " + chosen;
        }
        loadTimetableScene();
    }

    void addCells(Table table) throws IOException {
        PdfFont f = PdfFontFactory.createFont(FONT, PdfEncodings.IDENTITY_H);
        String pattern = "YYYY M d";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
        LocalDate d = LocalDate.parse("2018-11-27"); //todo: think about this!!!!!!!!

        HashSet<String> visitedSubjs = Main.currentStudent.visitedSubjects;
        for (int i = 0; i < 1000; ++i, d = d.plusDays(1)) { //todo: change ambiguity
            String curr = dateFormatter.format(d);
            ArrayList<Subject> daySubjects;
            if (MainController.mode <= 2)
                daySubjects = groupsSubjects.get(Main.currentStudent.group).get(curr);
            else
                daySubjects = filteredSubjects.get(MainController.chosen).get(curr);

            if (daySubjects != null) {
                for (Subject s : daySubjects) {
                    if (MainController.mode != 1 || visitedSubjs.contains(s.time.toString())) {
                        table.addCell(new Cell().add(new Paragraph(s.time.date.toString())));
                        table.addCell(new Cell().add(new Paragraph(s.time.startHour + "-" + s.time.endHour))); //todo: add new class
                        table.addCell(new Cell().add(new Paragraph(s.subjectName).setFont(f)));
                    }
                }
            }
        }
    }


    public void createPDF() throws IOException {
        float n = 150;
        float[] pointColumnWidths = {n, n, 2 * n};
        Table table = new Table(pointColumnWidths);
        getData();

        System.out.println(mode);
        table.addCell(new Cell().add(new Paragraph("Data")));
        table.addCell(new Cell().add(new Paragraph("Laikas")));
        table.addCell(new Cell().add(new Paragraph("Mokomasis dalykas")));
        addCells(table);

        PdfWriter pdfWriter = new PdfWriter("C:\\timetable\\filteredPdf.pdf");
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument, PageSize.A1);
        document.add(table);

        document.close();
    }
}
