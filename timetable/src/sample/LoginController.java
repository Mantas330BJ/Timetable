package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

import static sample.Main.sockets;
import static sample.Main.studentNames;

public class LoginController extends Windows {
    public static String name;
    public Button loginButton;
    public TextField nameTextField;
    public TextField surnameTextField;

    public Student loggedStudent = null;

    public void showMainWindow() throws IOException {
        name = nameTextField.getText() + " " + surnameTextField.getText();
        if (studentNames.containsKey(name)) {
            if (loggedStudent != null && sockets.containsKey(name))
                showAlert("Įvesties klaida.", "Šis vartotojas jau prisijungęs.");
            else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("mainWindow.fxml"));
                Stage stage = new Stage();
                loggedStudent = studentNames.get(name);
                stage.setUserData(loggedStudent);
                stage.setTitle("Pagrindinis langas");
                stage.setScene(new Scene(loader.load()));
                MainController newProjectController = loader.getController();
                newProjectController.setData(stage);

                stage.show();
            }
        }
        else
            showAlert("Įvesties klaida.", "Neatpažintas vartotojas.");
    }
}
