package com.aurexiris.networking.foundations.sockets.tcp;


import java.io.*;

import java.net.*;

public class TcpServerFraming {
    public static void main(String[] args) throws Exception {


        // Create a TCP server socket that listens to on port 8080
        ServerSocket server = new ServerSocket(8080);
        System.out.println("Server started...");

        // Wait for a client to connect and accept the connection
        Socket client = server.accept();
        System.out.println("Client connected");

        // Create a BufferedReader to read data from the client's input stream
        BufferedReader in = new BufferedReader(
                new InputStreamReader(client.getInputStream()));

        // Create a PrintWriter to send data to the client's output stream
        PrintWriter out = new PrintWriter(
                client.getOutputStream(), true);

        String msg;

        // Read messages from the client until the connection is closed
        while (( msg = in.readLine()) != null) {

            System.out.println("Raw: " + msg);

            // Parse the structured message (e.g., "name:Abhishek;msg:Hello")
            String[] parts = msg.split(";");

            String name = "";
            String message = "";

            for (String part : parts) {
                if (part.startsWith("name:")) {
                    name = part.substring(5);
                }
                else if (part.startsWith("msg:")) {
                    message = part.substring(4);
                }
            }

            System.out.println(name + " says: " + message);

            out.println("server:Received your message");
        }

        client.close();
        server.close();
    }
}



