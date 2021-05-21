package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

import static sample.Main.studentNames;

public class loginController extends Windows {
    public static String name; //todo: should delete
    public static String surname;

    public Button loginButton;
    public TextField nameTextField;
    public TextField surnameTextField;

    boolean emptyInputs() {
        return nameTextField.getText().isEmpty() || surnameTextField.getText().isEmpty();
    }

    public void showMainWindow() throws IOException {
        String name = nameTextField.getText() + " " + surnameTextField.getText();
        if (studentNames.containsKey(name)) {
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
