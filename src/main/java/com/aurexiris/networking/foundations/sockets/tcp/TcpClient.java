package com.aurexiris.networking.foundations.sockets.tcp;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException; // Naya import
import java.util.logging.Level;
import java.util.logging.Logger;

public class TcpClient {
    private static final Logger logger = Logger.getLogger(TcpClient.class.getName());

    public static void main(String[] args) {

        // Try with resources to ensure that the socket and streams are closed properly
        try (Socket socket = new Socket("localhost", 8080);
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Set a timeout of 5 seconds for reading from the server
            socket.setSoTimeout(5000);
            logger.info("Connected to server. Timeout set to 5s.");

            output.println("Hello Server!");

            try {
              // Read the response from the server.
                // If the server does not respond within 5 seconds, a SocketTimeoutException will be thrown.
                String response = input.readLine();
                if (response != null) {
                    System.out.println("Server says: " + response);
                }
            }
            catch (SocketTimeoutException e) {
                // This block will execute if the server does not respond within the specified timeout.
                logger.warning("Timeout! Server delayed for response.");
            }

        }
        catch (IOException e) {
            logger.log(Level.SEVERE, "Connection error: ", e);
        }
    }
}
