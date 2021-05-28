package Chat;

import sample.ChatController;
import sample.Main;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable{

    String host = "localhost";
    int portNumber = 4444;

    String userName;
    String serverHost;
    int serverPort;

    ServerThread serverThread;
    Thread serverAccessThread;

    public Client(String userName, String host, int portNumber){
        this.userName = userName;
        this.serverHost = host;
        this.serverPort = portNumber;
    }

    public void startClient(){
        try {
            Socket socket = new Socket(serverHost, serverPort);
            Thread.sleep(1000);

            serverThread = new ServerThread(socket, userName);
            serverAccessThread = new Thread(serverThread);
            serverAccessThread.start();
        }
        catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }

    public void run() {
        while (serverAccessThread.isAlive()) {
            if (!ChatController.messages.isEmpty()){
                serverThread.addNextMessage(ChatController.messages.poll());
            }
        }
    }
}