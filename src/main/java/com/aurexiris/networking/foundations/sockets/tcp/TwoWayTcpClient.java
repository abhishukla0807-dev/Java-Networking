package com.aurexiris.networking.foundations.sockets.tcp;



import java.io.*;
import java.net.*;

public class TwoWayTcpClient {

    public static void main(String[] args) throws Exception {

        Socket socket = new Socket("localhost", 8080);
        System.out.println("Connected to server!");


        PrintStream out = new PrintStream(socket.getOutputStream());


        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
        );


        BufferedReader keyboard = new BufferedReader(
                new InputStreamReader(System.in)
        );

        String userMsg, serverReply;


        while (true) {


            System.out.print("You: ");
            userMsg = keyboard.readLine();


            out.println(userMsg);

            serverReply = in.readLine();

            if (serverReply == null) {
                System.out.println("Server disconnected");
                break;
            }


            System.out.println("Server: " + serverReply);
        }


        out.close();
        in.close();
        keyboard.close();
        socket.close();
    }
}
