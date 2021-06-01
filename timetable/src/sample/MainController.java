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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import static sample.Main.*;

public class MainController implements dataPassing {
    Map<String, Integer> modes = Map.of("Vartotojas", 1, "Grupė", 2, "Mokomasis dalykas", 3);

    public Button timeTableButton;
    public ComboBox<String> filtersBox;
    public ComboBox<String> filteredBox;
    String FONT = "resources\\FreeSans.ttf";


    public static String timetableTopText;
    public static int mode;
    public static String chosen;

    public Student loggedStudent;
    public Student currentStudent;

    public void initialize() {

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

    public void setData(Stage stage) {
        loggedStudent = (Student)stage.getUserData();
        currentStudent = loggedStudent;
    }

    public void setChatCloseRequest(Stage stage) {
        stage.setOnCloseRequest(e -> {
            for (ListView<String> l : userLists.keySet())
                l.getItems().remove(currentStudent.name);
            try {
                sockets.get(currentStudent.name).close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            sockets.remove(currentStudent.name);
        });
    }

    public void loadTimetableScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("timetable.fxml"));
        Stage stage = new Stage();
        if (mode == 0)
            stage.setUserData(loggedStudent);
        else
            stage.setUserData(currentStudent);
        stage.setTitle("Tvarkaraštis");
        stage.setScene(new Scene(loader.load()));
        TimetableController newProjectController = loader.getController();
        newProjectController.setData(stage);
        stage.show();
    }

    public void showChat() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chat.fxml"));
        Stage stage = new Stage();
        stage.setUserData(loggedStudent.name);
        setChatCloseRequest(stage);
        stage.setTitle("Pokalbiai");
        stage.setScene(new Scene(loader.load()));
        ChatController newProjectController = loader.getController();
        newProjectController.setData(stage);
        stage.show();
    }

    public void showTimetable() throws IOException {
        mode = 0;
        timetableTopText = "Studentas: " + loggedStudent.name;
        loadTimetableScene();
    }

    public void getData() {
        chosen = filteredBox.getValue();
        mode = modes.get(filtersBox.getValue());
        if (mode == 1)
            currentStudent = Main.studentNames.get(chosen);
        else if (mode == 2)
            currentStudent = Main.groups.get(Integer.parseInt(chosen)).get(0);
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
        HashSet<String> visitedSubjs = currentStudent.visitedSubjects;
        for (LocalDate d = Main.startDate; d.compareTo(Main.endDate) <= 0; d = d.plusDays(1)) {
            String curr = dateFormatter.format(d);
            ArrayList<Subject> daySubjects;
            if (MainController.mode <= 2)
                daySubjects = groupsSubjects.get(currentStudent.group).get(curr);
            else
                daySubjects = filteredSubjects.get(MainController.chosen).get(curr);

            if (daySubjects != null) {
                for (Subject s : daySubjects) {
                    if (MainController.mode != 1 || visitedSubjs.contains(s.time.toString())) {
                        table.addCell(new Cell().add(new Paragraph(s.time.date.toString())));
                        table.addCell(new Cell().add(new Paragraph(s.time.getTime())));
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

        table.addCell(new Cell().add(new Paragraph("Data")));
        table.addCell(new Cell().add(new Paragraph("Laikas")));
        table.addCell(new Cell().add(new Paragraph("Mokomasis dalykas")));
        addCells(table);

        PdfWriter pdfWriter = new PdfWriter("filteredPdf.pdf");
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument, PageSize.A1);
        document.add(table);

        document.close();
    }
}
