package com.aurexiris.networking.foundations.sockets.tcp;
import java.util.logging.Logger;
import java.util.logging.Level;

import java.io.*;
import java.net.*;


public class TcpServer {

    public static void main(String[] args) {

        int port = 8080;

        //Start the server and listen for connections.
        // Try with resources is used to automatically close the server socket when done

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server started and listening on port " + port);

            // Wait for a client to connect
            Socket clientSocket = serverSocket.accept();

            System.out.println("Client connected: " + clientSocket.getInetAddress());

            // Create a PrintWriter to send messages to the client.
            // The second argument 'true' enables an auto-flush,
            // which means that the output will be sent immediately without needing to call out.flush() explicitly.
            PrintWriter out = new PrintWriter(
                    clientSocket.getOutputStream(), true);

        // Send a welcome message to the client
            out.println("Hello from the server side!");

            // Connection close
            clientSocket.close();

        }
        // Catch any IO exceptions that may occur during server operation.
        // Because we are dealing with network operations, which can fail for various reasons, e.g., port already in use, network issues, etc.
        catch (IOException e) {


            //e.printStackTrace(): bad for production code, it can expose sensitive information about the server's internal workings.
            // Instead, we should log the error message in a way that is secure and does not reveal unnecessary details to potential attackers.

            Logger logger = Logger.getLogger(TcpServer.class.getName());
            logger.log(Level.SEVERE, "Network error occurring in server", e);
        }
    }
}