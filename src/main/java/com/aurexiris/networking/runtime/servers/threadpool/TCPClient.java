/*
Problem:
Clients need a way to connect to the server, send messages, and receive responses. Without a client, server functionality cannot be tested.

Concept:
Use a simple TCP client that connects to the server, sends user input, and print server responses.

Solution:
TCPClient connects to localhost on port 8080, reads input from the console, sends it to the server, and displays the server’s reply. The connection closes when "exit" is entered.

Steps to Implement:
1. Create a Socket to connect to the server host and port.
2. Set up input and output streams for communication.
3. Read user input from the console.
4. Send each message to the server and print the response.
5. Break the loop and close resources when "exit" is typed.
*/



package com.aurexiris.networking.runtime.servers.threadpool;

import java.io.*;
import java.net.Socket;

public class TCPClient {

    public static void main(String[] args) {

        try (
                Socket socket = new Socket("localhost", 8080);
                BufferedReader serverIn = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter serverOut = new PrintWriter(
                        socket.getOutputStream(), true);
                BufferedReader userIn = new BufferedReader(
                        new InputStreamReader(System.in))
        ) {
            System.out.println("Connected. Type messages:");

            String input;
            while ((input = userIn.readLine()) != null) {
                serverOut.println(input);

                String response = serverIn.readLine();
                System.out.println("Server: " + response);

                if ("exit".equalsIgnoreCase(input)) break;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
