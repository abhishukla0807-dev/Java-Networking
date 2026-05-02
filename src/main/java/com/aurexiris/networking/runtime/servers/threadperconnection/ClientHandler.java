
/*
Problem:
--------
Managing client communication directly in the server loop would block other clients and complicate resource handling.

Concept:
---------
Encapsulate client communication logic in a Runnable class.
Each client runs independently in its own thread, isolating I/O operations.

Solution:
---------
->ClientHandler receives a Socket, sets up i/o streams, listens for client messages, echoes them back, and closes gracefully on "exit".

Steps to Implement:
1. Store the client Socket in the constructor.
2. Implement Runnable and override run().
3. Create BufferedReader and PrintWriter for I/O streams.
4. Continuously read client messages, echo responses, and handle "exit".
5. Close the socket and resources in a finally block.
*/




package com.aurexiris.networking.runtime.servers.threadperconnection;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;

    // Constructor now accepts the client socket
    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("Handling client: " + socket);

        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        )

        {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("[" + socket.getPort() + "] Client: " + message);

                if (message.equalsIgnoreCase("exit")) {
                    out.println("Connection closed.");
                    break;
                }

                // Echo response
                out.println("Server Echo: " + message);
            }
        }

        catch (IOException e) {
            System.out.println("Client disconnected abruptly: " + socket);
        }
        finally {
            try {
                socket.close();
                System.out.println("Connection closed: " + socket);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
