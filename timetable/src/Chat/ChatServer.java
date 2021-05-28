package Chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {

    private static final int portNumber = 4444;

    ArrayList<ClientThread> clients;

    public static void main(String[] args) throws IOException {
        ChatServer server = new ChatServer();
        server.startServer();
    }

    private void startServer() throws IOException {
        System.out.println("Serveris paleistas.");
        clients = new ArrayList<>();
        ServerSocket serverSocket;
        serverSocket = new ServerSocket(portNumber);
        while (true){
            Socket socket = serverSocket.accept();
            ClientThread client = new ClientThread(this, socket);
            Thread thread = new Thread(client);
            thread.start();
            clients.add(client);
        }
    }
}
