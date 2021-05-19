package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class mainController {
    public Button timeTableButton;

    public void showTimeTable() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("timetable.fxml"));
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Tvarkaraštis");
        primaryStage.setScene(new Scene(root, 1300, 900));
        primaryStage.show();
    }
}
