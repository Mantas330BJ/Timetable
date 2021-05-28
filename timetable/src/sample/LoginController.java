package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

import static sample.Main.studentNames;

public class LoginController extends Windows {
    public static String name;
    public Button loginButton;
    public TextField nameTextField;
    public TextField surnameTextField;

    public void showMainWindow() throws IOException {
        name = nameTextField.getText() + " " + surnameTextField.getText();
        if (studentNames.containsKey(name)) {
            Main.currentStudent = studentNames.get(name);
            Main.loggedStudent = studentNames.get(name);
            Parent root = FXMLLoader.load(getClass().getResource("mainWindow.fxml"));
            Stage primaryStage = new Stage();
            primaryStage.setTitle("Pagrindinis langas");
            primaryStage.setScene(new Scene(root, 800, 500));
            primaryStage.show();
        }
        else
            showAlert("Įvesties klaida.", "Neatpažintas vartotojas.");
    }
}
