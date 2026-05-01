package com.aurexiris.networking.foundations.sockets.tcp;

/*
 * File->TcpServerProtocolLifecycle.java
 * Description:
 * -------------
 * Implements a TCP server with a finite state machine to control client interaction lifecycle.

 * Key Concepts:
 * -------------
 * Problem:
 * Build a server that enforces protocol rules and state transitions
 * (CONNECTED → ACTIVE → CLOSED) to prevent invalid commands.

 *Solution:
 * Use an enum-based FSM to validate commands (HELLO, MSG, EXIT),
 * handle transitions safely, and close connections gracefully.
*/


import java.io.*;
import java.net.*;
public class TcpServerProtocolLifecycle {

    // Define states (FSM)
    //enum is used to represent the different states of the server connection lifecycle.
    // It provides a clear and type-safe way to manage the states and transitions between them.
    enum State {
        CONNECTED,
        ACTIVE,
        CLOSED
    }

    public static void main(String[] args) {

        try {

            // Create a server socket that listens to on port 8080
            ServerSocket server = new ServerSocket(8080);
            System.out.println("Server started on port 8080...");

            Socket client = server.accept();
            System.out.println("Client connected");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(client.getInputStream()));

            PrintWriter out = new PrintWriter(
                    client.getOutputStream(), true);

            // Initial state
            State state = State.CONNECTED;

            String msg;

            while ((msg = in.readLine()) != null) {

                System.out.println("Raw: " + msg);

                // Parse message
                String[] parts = msg.split(";");

                String command = "";
                String message = "";

                for (String part : parts) {
                    if (part.startsWith("CMD:")) {
                        command = part.substring(4);
                    } else if (part.startsWith("msg:")) {
                        message = part.substring(4);
                    }
                }

                // STATE-BASED EXECUTION
                switch (command) {

                    case "HELLO":

                        if (state == State.CONNECTED) {
                            out.println("Hi! You are now active.");
                            state = State.ACTIVE; // Transition to ACTIVE
                        } else {
                            out.println("ERROR:INVALID_STATE (HELLO not allowed)");
                        }

                        break;

                    case "MSG":

                        if (state == State.ACTIVE) {
                            System.out.println("Client says: " + message);
                            out.println("Message received");
                        }
                        else {
                            out.println("ERROR.....INVALID_STATE (Send HELLO first)");
                        }

                        break;

                    case "EXIT":

                        out.println("Goodbye");
                        state = State.CLOSED; // Transition to CLOSED

                        // cleanup
                        client.close();
                        server.close();
                        return;

                    default:
                        out.println("ERROR.....UNKNOWN_COMMAND");
                }

                // Safety: if the state becomes CLOSED
                if (state == State.CLOSED) {
                    break;
                }
            }

            System.out.println("Client disconnected.");
            client.close();
            server.close();

        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }
}


