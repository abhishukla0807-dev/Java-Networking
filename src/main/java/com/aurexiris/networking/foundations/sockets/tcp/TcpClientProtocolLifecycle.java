
package com.aurexiris.networking.foundations.sockets.tcp;


/*
 * File: TcpServerProtocolLifecycle.java
 *
 * Description:
 *  Implement a TCP server with a finite state machine (FSM) to control
 * client interaction lifecycle.
 *
 * Problem:
 * Build a server that enforces protocol rules and state transitions
 * (CONNECTED → ACTIVE → CLOSED) to prevent invalid commands.
 *
 * Solution:
 * Use an enum-based FSM to validate commands (HELLO, MSG, EXIT),
 * handle transitions safely, and close connections gracefully.
*/

import java.io.*;
import java.net.*;


public class TcpClientProtocolLifecycle {
     // Define states (FSM)
    // enum is used to represent the different states of the client connection lifecycle.
    // It provides a clear and type-safe way to manage the states and transitions between them.
    enum State {
        CONNECTED,
        ACTIVE,
        CLOSED
    }

    public static void main(String[] args) {

        try {
            Socket socket = new Socket("localhost", 8080);
            System.out.println("Connected to server...");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(
                    socket.getOutputStream(), true);
            BufferedReader keyboard = new BufferedReader(
                    new InputStreamReader(System.in));

            State state = State.CONNECTED;
            String input;

            while (state != State.CLOSED) {
                System.out.print("Enter command (HELLO / MSG / EXIT): ");
                input = keyboard.readLine();

                switch (input.toUpperCase()) {

                    case "HELLO":
                        if (state == State.CONNECTED) {
                            out.println("CMD:HELLO");
                            String response = in.readLine();
                            System.out.println("Server.... " + response);
                            state = State.ACTIVE;
                        } else {
                            System.out.println("Already active!");
                        }
                        break;

                    case "MSG":
                        if (state == State.ACTIVE) {
                            System.out.print("Enter message: ");
                            String msg = keyboard.readLine();
                            out.println("CMD:MSG;msg:" + msg);
                            String response = in.readLine();
                            System.out.println("Server.... " + response);
                        }
                        else {
                            System.out.println("You must send HELLO first");
                        }
                        break;

                    case "EXIT":
                        out.println("CMD:EXIT");
                        String response = in.readLine();
                        System.out.println("Server.... " + response);
                        state = State.CLOSED;
                        break;

                    default:
                        System.out.println("Invalid command!");
                }
            }


            // Clean up resources, because the client is closing the connection,
            // we need to ensure that all resources are properly released to avoid potential memory leaks or other issues.
            // This includes closing the socket connection, the keyboard input stream, and the input/output streams used for communication with the server.
            socket.close();
            keyboard.close();
            in.close();
            out.close();
            System.out.println("Client closed.");

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
