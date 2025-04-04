package server;

import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    public static final int PORT = 9082;
    public static List<String> usernames = new CopyOnWriteArrayList<>();
    public static List<String> censuredWords = new CopyOnWriteArrayList<>();
    public static List<String> messages = new CopyOnWriteArrayList<>();
    public static List<ServerThread> serverThreads = new CopyOnWriteArrayList<>();
    public Server() {
        try {
            addCensuredWords();
            ServerSocket serverSocket = new ServerSocket(PORT);
            while(true) {
                ServerThread serverThread = new ServerThread(serverSocket.accept());
                Thread thread = new Thread(serverThread);
                thread.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void addCensuredWords(){
        censuredWords.add("stupid");
        censuredWords.add("dumb");
        censuredWords.add("ugly");
    }
    public static void main(String[] args) {
        new Server();
    }
}
