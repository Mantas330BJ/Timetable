package sample;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class ChatController extends Windows {

    public ListView<String> usersListView;
    public ListView<String> chatListView;
    public TextField textField;
    public String currentStudent;
    public Stack<HBox> hBoxes = new Stack<>();
    public Label currentUserLabel;

    public void addListener() {
        Platform.runLater(() ->
            usersListView.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) ->  {
                Platform.runLater(() -> chatListView.getItems().clear());


                String directory = "messages\\" + currentStudent + "#" + newValue + ".txt";
                directory = directory.replace(" ", "_");
                File f = new File(directory);

                if (f.exists()) {
                    try {
                        Scanner s = new Scanner(f);
                        while (s.hasNextLine()) {
                            String next = s.nextLine();
                            Platform.runLater(() -> chatListView.getItems().add(next));
                        }
                        s.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }


            })
        );
    }

    public void setData(Stage stage) {
        currentStudent = (String)stage.getUserData();
        currentUserLabel.setText("Dabartinis vartotojas: " + currentStudent);
        addListener();
        Thread t = new Thread(() -> {
            try {
                for (String s : Main.sockets.keySet())
                    usersListView.getItems().add(s);
                Socket socket;
                BufferedReader bufferedReader;
                Main.currentChatStudent = currentStudent;
                socket = new Socket("localhost", 6363);
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                while (true) {
                    if (bufferedReader.ready()) {
                        try {
                            String line = bufferedReader.readLine();
                            int splitIndex = line.indexOf("#");
                            String sender = line.substring(splitIndex + 1);
                            Platform.runLater(() -> {
                                if (sender.equals(usersListView.getSelectionModel().getSelectedItem())) {
                                    String text = line.substring(0, splitIndex);
                                    chatListView.getItems().add(text);
                                }
                            });
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }
                    }
                    Thread.sleep(100);
                }
            } catch (IOException | InterruptedException exception) {
                exception.printStackTrace();
            }
        });
        t.start();
        Main.userLists.put(usersListView, currentStudent);
    }

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

                            boolean centerRight = item.charAt(item.length() - 1) == '1';
                            String text = item.substring(0, item.length() - 1);
                            if (centerRight)
                                hBox.setAlignment(Pos.CENTER_RIGHT);
                            else
                                hBox.setAlignment(Pos.CENTER_LEFT);


                            Label label = new Label(text);
                            label.setAlignment(Pos.CENTER);
                            hBoxes.add(hBox);
                            hBox.getChildren().add(label);
                            setGraphic(hBox);
                        }
                        else {
                            setText("");
                            setGraphic(null);
                        }
                    }
                };
            }
        });

    }

    public void initialize() {
        setFactory();
    }

    public void writeTextToFile (String sender, String recipient, String text, boolean centerRight) throws IOException {
        String directory = "messages\\" + sender + "#" + recipient + ".txt";
        directory = directory.replace(" ", "_");
        FileWriter fw = new FileWriter(directory, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(text + (centerRight ? 1 : 0) + "\n");
        bw.close();
    }

    public void sendMessageTo (String sender, String recipient, boolean centerRight) throws IOException {
        String text = textField.getText();
        writeTextToFile(sender, recipient, text, centerRight);
        Socket s = Main.sockets.get(recipient);
        PrintWriter printWriter = new PrintWriter(s.getOutputStream());
        printWriter.println(text + ((centerRight ^ sender.equals(recipient)) ? 0 : 1) + "#" + sender);
        printWriter.flush();
    }

    public void sendMessage() throws IOException {
        String recipient = usersListView.getSelectionModel().getSelectedItem();
        if (recipient != null && !recipient.equals(currentStudent)) {
            sendMessageTo(recipient, currentStudent, false);
            sendMessageTo(currentStudent, recipient, true);
        }
        else if (recipient == null) {
            showAlert("Siuntimo klaida", "Nepasirinktas gavėjas");
        }
        else {
            sendMessageTo(currentStudent, currentStudent, true);
        }
    }
}
