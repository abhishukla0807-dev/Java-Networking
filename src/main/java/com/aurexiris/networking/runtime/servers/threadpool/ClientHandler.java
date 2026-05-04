/*
Problem:
Directly handling client communication in the server loop blocks other clients and makes resource management difficult.

Concept:
Use a separate handler class that implements Runnable. Each client runs in its own thread, isolating input/output operations.

Solution:
ClientHandler manages one client socket. It reads messages, echoes them back, and closes the connection when "exit" is received.

Steps to Implement:
1. Store the client socket in the constructor.
2. Implement Runnable and override run().
3. Create input and output streams for communication.
4. Continuously read client messages and send responses.
5. Handle "exit" to close the connection gracefully.
6. Ensure socket and resources are closed in a finally block.
*/


package com.aurexiris.networking.runtime.servers.threadpool;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("Handling client " + socket);

        try (
                // Create input and output streams for communication
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

               //Create a PrintWriter for sending responses to the client
                // with auto-flush enabled
                PrintWriter out = new PrintWriter(
                        socket.getOutputStream(), true)
        ) {
            String msg;
            while ((msg = in.readLine()) != null) {
                System.out.println("[" + socket.getPort() + "] " + msg);

                if ("exit".equalsIgnoreCase(msg)) {
                    out.println("Goodbye...");
                    break;
                }

                // Echo response
                out.println("Echo: " + msg);
            }
        }
        catch (IOException e) {
            System.out.println("Client error " + socket);
        }
        finally {
            try {
                socket.close();
                System.out.println("Closed " + socket);
            }
            catch (IOException ignored) {}
        }
    }
}
