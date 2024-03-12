package client;


import java.io.*;
import java.net.Socket;

public class Client {

    public static final int PORT = 9082;
    public Client() {
        try (
                Socket socket = new Socket("localhost", PORT);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
                BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            )
        {
            String message;

            System.out.println(in.readLine());

            out.println(keyboard.readLine());

            message = in.readLine();
            System.out.println(message);
            while(message.equalsIgnoreCase("Username already exists!")){
                out.println(keyboard.readLine());
                message = in.readLine();
                System.out.println(message);
            }

            while(!(message = in.readLine()).equals("END_OF-MESSAGE.HISTORY55638"))
                System.out.println(message);

            Thread readThread = new Thread(() -> {
                try {
                    String line;
                    while (true) {
                        line = in.readLine();
                        System.out.println(line);
                        if (line == null || line.startsWith("Goodbye"))
                            break;
                    }
                } catch (IOException e) {
                    System.out.println("Error reading from server: " + e.getMessage());
                    System.exit(0);
                }
            });
            readThread.start();

            Thread writeThread = new Thread(() -> {
                try {
                    String userInput;
                    while (!(userInput = keyboard.readLine()).equals("quit"))
                        out.println(userInput);
                    out.println(userInput);
                } catch (IOException e) {
                    System.out.println("Error writing to server: " + e.getMessage());
                    System.exit(0);
                }
            });

            writeThread.start();

            try {
                readThread.join();
                writeThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        catch (IOException e) {
            System.out.println("Server is not running!");
        }

    }

    public static void main(String[] args) {
        new Client();
    }
}
