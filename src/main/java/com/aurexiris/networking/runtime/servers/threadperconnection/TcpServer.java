

/*
Problem:
A server must handle multiple client connections concurrently.
A naive single-threaded design blocks on one client, preventing others from connecting.

Concept:
->Use the Thread-Per-Connection model.
->The main server thread accepts incoming sockets,
->, and each client is handed off to a dedicated handler running in its own thread.

Solution:
TCPServer listens on port 8080, accepts client connections, and spawns a new ClientHandler thread for each client. This ensures multiple clients can interact with the server simultaneously.

Steps to Implement:
1. Create a ServerSocket bound to port 8080.
2. Enter an infinite loop to accept client connections.
3. For each client connection, create a new ClientHandler instance, passing the client socket to its constructor.
4. Start a new Thread with the ClientHandler to achieve concurrency.
*/





package com.aurexiris.networking.runtime.servers.threadperconnection;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class TCPServer {

    private static final int PORT = 8080;

    public static void main(String[] args) {
        System.out.println("Server starting on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                // Accept a new client connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                // Pass the socket into ClientHandler
                ClientHandler handler = new ClientHandler(clientSocket);

                // Start a new thread for this client
                Thread thread = new Thread(handler);
                thread.start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
