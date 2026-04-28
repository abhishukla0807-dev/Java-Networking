package com.aurexiris.networking.foundations.sockets.tcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TwoWayTcpServer {








    public static void main(String[] args) throws Exception {

        // Create the server socket and listen on port 8080
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server started on port 8080 and waiting for client...");

        //  waiting for client connection (BLOCKING), the server will pause here until a client connects
        Socket client = serverSocket.accept();
        System.out.println("Client connected");

        //  Output stream >> PrintStream is used to send data to the client, it provides convenient methods for printing various data types (like println, print, etc.) and it automatically flushes the output after each call,
        //  ensuring that the data is sent immediately to the client without needing to call out.flush() explicitly.
        PrintStream out = new PrintStream(client.getOutputStream());

        //  Input stream → Reads data sent by client;
        //  BufferedReader is used to read text from the client's input stream, it allows us to read data line by line using the readLine() method, which is convenient for processing text-based communication.
        BufferedReader in = new BufferedReader(
                new InputStreamReader(client.getInputStream())
        );

        // Keyboard input >>reads input from keyboard
        BufferedReader keyboard = new BufferedReader(
                new InputStreamReader(System.in)
        );

        String clientMsg, serverReply;

        // The Server runs continuously to read messages from the client and send replies until the client disconnects. The loop will keep running as long as the client is connected and sending messages.
        // If the client disconnects (i.e., in.readLine() returns null), the loop will break, and the server will proceed to close all resources.
        while (true) {

            // Read the message from the client
            clientMsg = in.readLine();

            if (clientMsg == null) {
                System.out.println("Client disconnected");
                break;
            }

            //print the client's message to the server console
            System.out.println("Client: " + clientMsg);

            // Server replies to a client
            System.out.print("You: ");
            serverReply = keyboard.readLine();

            // send the reply to the client
            out.println(serverReply);
        }

        //
        out.close();
        in.close();
        keyboard.close();
        client.close();
        serverSocket.close();
    }
}
