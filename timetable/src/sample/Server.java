package sample;

import javafx.application.Platform;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    ServerSocket serverSocket;

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(8050);
            while (Main.shouldRunServer) {
                Socket socket = serverSocket.accept();
                Main.sockets.put(Main.currentChatStudent, socket);
                for (ListView<String> l : Main.userLists.keySet()) {
                    Platform.runLater(() -> l.getItems().add(Main.currentChatStudent));
                }
            }
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
        finally {
            try {
                serverSocket.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }
}
