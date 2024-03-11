package client;


import java.io.*;
import java.net.Socket;

public class Client {

    public static final int PORT = 9082;
    public Client() throws IOException {
        Socket socket = new Socket("localhost", PORT);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        String message;

        message = in.readLine();
        System.out.println(message);

        message = keyboard.readLine();
        out.println(message);

        message = in.readLine();
        System.out.println(message);
        while(message.equalsIgnoreCase("Username already exists!")){
            message = keyboard.readLine();
            out.println(message);
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
                    if (line == null || line.startsWith("Goodbye"))
                        break;
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
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
                e.printStackTrace();
            }
        });

        writeThread.start();

        try {
            readThread.join();
            writeThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        in.close();
        out.close();
        keyboard.close();
        socket.close();
    }

    public static void main(String[] args) {
        try {
            new Client();
        } catch (IOException e) {
            System.out.println("Server is not running!");
        }
    }
}
