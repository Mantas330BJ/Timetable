package sample;

import javafx.scene.control.Alert;

abstract public class Windows {
    void showAlert(String title, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Klaida.");
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}
