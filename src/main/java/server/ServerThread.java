package server;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

public class ServerThread implements Runnable{

    private Socket socket;
    private List<String> usernames = Server.usernames;
    private List<String> censuredWords = Server.censuredWords;
    private List<String> messages = Server.messages;
    private List<ServerThread> serverThreads = Server.serverThreads;
    private String username;
    private static final int historySize = 100;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss");
    private PrintWriter out;
    public ServerThread(Socket socket) {
        this.socket =  socket;
    }

    @Override
    public void run() {
        String message;

        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            )
        {
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

            serverThreads.add(this);

            sendMessageToAll(username + " has joined the chat.");

            messages.iterator().forEachRemaining(out::println);

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
                if(messages.size() > historySize)
                    messages.remove(0);

                sendMessageToAll(formatMessage(message));
                out.println(formatMessage(message));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            serverThreads.remove(this);

            serverThreads.iterator().forEachRemaining(serverThread ->
                serverThread.sendMessage(username + " has left the chat.\n"));

            if(out != null) out.close();
            if(socket != null)
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

        }
    }
    private String censorMessage(String message) {
        Iterator<String> iterator = censuredWords.iterator();
        while (iterator.hasNext()) {
            String word = iterator.next();
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
        serverThreads.iterator().forEachRemaining(serverThread -> {
            if (serverThread != this)
                serverThread.sendMessage(message);
        });
    }
    public void sendMessage(String message) {
        out.println(message);
    }
}
