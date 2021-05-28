package sample;

import Chat.Client;
import Chat.ServerThread;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.util.LinkedList;
import java.util.Locale;

public class ChatController {
    public ListView<String> usersListView;
    public ListView<String> chatListView;
    public static LinkedList<String> messages = new LinkedList<>();
    private static final String host = "localhost";
    private static final int portNumber = 4444;
    public TextField textField;

    int x = 1;

    public void setFactory() {
        chatListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            HBox hBox = new HBox();
                            //if (x == 0)
                            //    hBox.setAlignment(Pos.CENTER_LEFT);
                            if (x == 1)
                                hBox.setAlignment(Pos.CENTER_RIGHT);
                            Label label = new Label(item.toUpperCase(Locale.ROOT));
                            label.setAlignment(Pos.CENTER);
                            hBox.getChildren().add(label);
                            setGraphic(hBox);
                        }
                        else {
                            setText("");
                        }
                    }
                };
            }
        });
    }

    public void initialize() {
        setFactory();
        Client client = new Client(Main.loggedStudent.name, host, portNumber);
        client.startClient();
        Thread thread = new Thread(client);
        thread.start();
    }

    public void sendMessage() {
        messages.add(textField.getText());
        while (!ServerThread.sendMessage)
            ;
        while (!ServerThread.messages.isEmpty()) {
            chatListView.getItems().add(ServerThread.messages.poll());
        }
        ServerThread.sendMessage = false;
    }
}
