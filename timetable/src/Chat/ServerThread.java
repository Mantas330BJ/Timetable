package Chat;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public class ServerThread implements Runnable {
    Socket socket;
    String userName;
    final LinkedList<String> messagesToSend;
    boolean hasMessages = false;

    public static LinkedList<String> messages;
    public static boolean sendMessage = false;

    public ServerThread(Socket socket, String userName){
        this.socket = socket;
        this.userName = userName;
        messagesToSend = new LinkedList<>();
        messages = new LinkedList<>();
    }

    public void addNextMessage(String message) {
        synchronized (messagesToSend){
            hasMessages = true;
            messagesToSend.push(message);
        }
    }

    @Override
    public void run(){
        try {
            PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), false);
            InputStream serverInStream = socket.getInputStream();
            Scanner serverIn = new Scanner(serverInStream); //looks weird
            while (!socket.isClosed()) {
                if (serverInStream.available() > 0 && serverIn.hasNextLine()) {
                    String line = serverIn.nextLine();
                    System.out.println(line);
                    messages.add(line);
                }
                if (hasMessages){
                    String nextSend = "";
                    synchronized(messagesToSend) {
                        nextSend = messagesToSend.pop();
                        hasMessages = !messagesToSend.isEmpty();
                    }
                    System.out.println(userName + ": " + nextSend);
                    serverOut.println(userName + ": " + nextSend);
                    sendMessage = true;
                    messages.add(userName + ": " + nextSend);
                    serverOut.flush();
                }
            }
        }
        catch(IOException ex){
            ex.printStackTrace();
        }

    }
}