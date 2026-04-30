
/*
 * TcpServerProtocol
 *
 * PURPOSE:
 * --------
 * This server implements a command-based protocol over TCP.
 *
 * PROTOCOL FORMAT:
 * ----------------
 * CMD:<COMMAND>;key:value
 *
 * Examples:
 * CMD:HELLO
 * CMD:MSG;msg:Hello
 * CMD:EXIT
 *
 * RESPONSIBILITIES:
 * -----------------
 * 1. Accept client connection
 * 2. Read incoming messages
 * 3. Parse protocol message
 * 4. Execute logic based on command
 * 5. Send response back to a client
 *
 * IMPORTANT:
* - Uses blocking I/O
 * - Single-client server
 * - Runs until a client sends EXIT or disconnects
 */


package com.aurexiris.networking.foundations.sockets.tcp;

import java.io.*;
import java.net.*;

public class TcpServerProtocol {

    public static void main(String[] args) {

        try {
            //  Start server on port 888
            ServerSocket server = new ServerSocket(8080);
            System.out.println("Server started on port 8080...");

            //  Wait for client connection, i.e., block until a client connects.
            Socket client = server.accept();
            System.out.println("Client connected: " + client.getInetAddress());

            // 🔹 Step 3: Input stream to read data from a client.
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(client.getInputStream())
            );

            //  Output stream to send data back to a client. Auto-flush enabled for immediate sending.
            PrintWriter out = new PrintWriter(
                    client.getOutputStream(), true);

            String msg;

            //  Communication loop
            while ((msg = in.readLine()) != null) {

                // Print raw incoming message
                System.out.println("Raw message: " + msg);

                //  Parse message
                String[] parts = msg.split(";");

                String command = "";
                String message = "";

                // Extract command and parameters
                for (String part : parts) {

                    if (part.startsWith("CMD:")) {
                        command = part.substring(4);
                    } else if (part.startsWith("msg:")) {
                        message = part.substring(4);
                    }
                }

                // Execute command logic
                switch (command) {

                    case "HELLO":
                        out.println("Hi from server!");
                        break;

                    case "MSG":
                        System.out.println("Client says: " + message);
                        out.println("Message received by server");
                        break;

                    case "EXIT":
                        out.println("Goodbye!");
                        System.out.println("Client disconnected.");

                        // Close resources
                        client.close();
                        server.close();
                        return;

                    default:
                        out.println("ERROR: Unknown command");
                }
            }

            //  Cleanup if a client disconnects unexpectedly
            System.out.println("Client connection closed.");
            client.close();
            server.close();

        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
