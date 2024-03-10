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

        while(!(message = in.readLine()).equals("END_OF-MESSAGE.HISTORY55638")){
            System.out.println(message);
        }

        while(true){
            message = keyboard.readLine();
            if(message.equalsIgnoreCase("quit")){
                out.println(message);
                message = in.readLine();
                System.out.println(message);
                in.close();
                out.close();
                socket.close();
                break;
            }
            out.println(message);
            System.out.println(in.readLine());

        }

    }

    public static void main(String[] args) {
        try {
            new Client();
        } catch (IOException e) {
            System.out.println("Server is not running!");
        }
    }
}
