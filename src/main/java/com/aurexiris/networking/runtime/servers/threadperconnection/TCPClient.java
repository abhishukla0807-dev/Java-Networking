/*
Problem:
--------
Clients need a way to connect to the server, send messages, and receive responses. Without a proper client, server functionality cannot be tested.

Concept:
--------
Implement a simple TCP client that connects to the server, uses blocking I/O for communication, and interacts via the console.

Solution:
---------
TCPClient connects to localhost:8080, reads user input, sends it to the server, and prints the server’s responses.
It closes gracefully when "exit" is typed.

Steps to Implement:
-------------------
1. Create a Socket to connect to the server host and port.
2. Set up BufferedReader for server input and user input.
3. Set up PrintWriter for sending messages to the server.
4. Loop: read user input, send to server, read and print server response.
5. Break and close resources when "exit" is entered.
*/


package com.aurexiris.networking.runtime.servers.threadperconnection;

import java.io.*;
import java.net.Socket;

public class TCPClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        System.out.println("Connecting to server...");

        try (
                Socket socket = new Socket(SERVER_HOST, SERVER_PORT);

                BufferedReader serverIn = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

                PrintWriter serverOut = new PrintWriter(
                        socket.getOutputStream(), true);

                BufferedReader userInput = new BufferedReader(
                        new InputStreamReader(System.in))
        ) {
            System.out.println("Connected to server. Type messages:");

            String input;
            while (true) {
                // Read from keyboard
                input = userInput.readLine();

                // Send it to server
                serverOut.println(input);

                // Read server response
                String response = serverIn.readLine();
                System.out.println("Server: " + response);

                // Exit condition
                if (input.equalsIgnoreCase("exit")) {
                    break;
                }
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

