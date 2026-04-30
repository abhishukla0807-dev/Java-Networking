



/*
 * TcpClientProtocol
 *
 * PURPOSE:
 * --------
 * This client communicates with a TCP server using a custom command-based protocol.
 *
 * PROTOCOL FORMAT:
 * CMD:<COMMAND>;key:value

 * Examples:
 * CMD:HELLO
 * CMD:MSG;msg:Hello
 * CMD:EXIT
 *
 * RESPONSIBILITIES:
 * -----------------
 * 1. Establish connection with server
 * 2. Take user input (commands)
 * 3. Convert input into protocol format
 * 4. Send a structured message to server
 * 5. Receive response from server
 * 6. Display response
 *
 * COMMUNICATION FLOW:
 * -------------------
 * User Input → Format Message → Send → Wait → Receive → Print → Repeat
 */


package com.aurexiris.networking.foundations.sockets.tcp;

import java.io.*;
import java.net.*;

public class TcpClientProtocol {

    public static void main(String[] args) {

        try {
            //  Create socket and connect to server
            Socket socket = new Socket("localhost", 8080);
            System.out.println("Connected to server...");

            //  Output stream is used to SEND data to server
            // PrintWriter with autoFlush = true ensures immediate sending
            PrintWriter out = new PrintWriter(
                    socket.getOutputStream(), true
            );

            // Input stream is used to RECEIVE data from server.
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            // Read input from user i.e. keyboard
            BufferedReader keyboard = new BufferedReader(
                    new InputStreamReader(System.in)
            );

            String input; // stores user command

            // Continuous communication loop
            while (true) {

                // take input of command
                System.out.print("Enter command (HELLO / MSG / EXIT): ");
                input = keyboard.readLine();

                //  Convert user input into protocol message
                // HELLO command → simple handshake
                if (input.equalsIgnoreCase("HELLO")) {
                    out.println("CMD:HELLO");
                }


               //MSG command → requires additional message input from user
                else if (input.equalsIgnoreCase("MSG")) {
                    System.out.print("Enter message: ");
                    String msg = keyboard.readLine();

                    // structured message
                    out.println("CMD:MSG;msg:" + msg);
                }

                // EXIT command >> terminate connection
                else if (input.equalsIgnoreCase("EXIT")) {
                    out.println("CMD:EXIT");
                    break; // exit loop
                }

                // Invalid command handling
                else {
                    System.out.println("Invalid command. Try again.");
                    continue;
                }

                //  Wait for server response. Blocking call until response is received.
                String response = in.readLine();

                // If server disconnects
                if (response == null) {
                    System.out.println("Server disconnected.");
                    break;
                }

                //  Display server response
                System.out.println("Server: " + response);
            }

            //  Close resources
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
