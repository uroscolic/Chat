package server;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ServerThread implements Runnable{

    private Socket socket;
    private List<String> usernames = Server.usernames;
    private List<String> censuredWords = Server.censuredWords;
    private List<String> messages = Server.messages;
    private String username;
    private final Object lock = new Object();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss");
    public ServerThread(Socket socket) {
        this.socket =  socket;
    }
    private PrintWriter out;

    @Override
    public void run() {
        String message;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
            out.println("Enter your username:");
            message = in.readLine();

            while(usernames.contains(message)){
                out.println("Username already exists!");
                message = in.readLine();
            }

            usernames.add(message);
            username = message;
            out.println("Welcome, " + message + "!");
            Server.serverThreads.add(this);
            synchronized (lock) {
                sendMessageToAll(username + " has joined the chat.");
            }

            for (String message1 : messages)
                out.println(message1);

            out.println("END_OF-MESSAGE.HISTORY55638");

            while(true){
                message = in.readLine();
                if(message.equalsIgnoreCase("quit")){
                    out.println("Goodbye, " + username + "!");
                    usernames.remove(username);
                    break;
                }
                message = censorMessage(message);
                messages.add(formatMessage(message));
                if(messages.size() > 100){
                    messages.remove(0);
                }
                sendMessageToAll(formatMessage(message));
                out.println(formatMessage(message));
            }

        } catch (IOException ignored) {

        }
        finally {
            Server.serverThreads.remove(this);
            synchronized (lock) {
                for (ServerThread thread : Server.serverThreads) {
                    thread.sendMessage(username + " has left the chat.");
                }
            }
            try {
                if(out != null) out.close();
                if(in != null) in.close();
                if(socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private String censorMessage(String message) {
        for (String word : censuredWords) {
            if (message.toLowerCase().contains(word.toLowerCase())) {
                String censuredWord = word.charAt(0) + "*".repeat(Math.max(0, word.length() - 2)) + word.substring(word.length() - 1);
                message = message.replace(word, censuredWord);
            }
        }
        return message;
    }
    private String formatMessage(String message) {
        return LocalDateTime.now().format(formatter) + " - " + username + ": " + message;
    }
    private void sendMessageToAll(String message) throws IOException {
        for(ServerThread serverThread : Server.serverThreads){
            if(serverThread != this){
                serverThread.getSocket().getOutputStream().write((message + "\n").getBytes());
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }
    public void sendMessage(String message) {
        out.println(message);
    }
}
