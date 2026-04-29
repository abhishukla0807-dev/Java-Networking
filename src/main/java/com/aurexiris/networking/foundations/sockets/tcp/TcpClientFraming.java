package com.aurexiris.networking.foundations.sockets.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class TcpClientFraming {

    public static  void main(String[] args) {

        // This is a placeholder for the TcpClientFraming implementation.
        // The actual implementation would iP a client that connects to involve creating a TCP client that connects to a server,
        // sends framed messages, and handles responses appropriately.


   try {
       // Create a TCP client socket and connect to the server
       Socket socket = new Socket("localhost", 8080);
       System.out.println("Connected to server....");

       // send data to the server using the socket's output stream
       PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

       //receive data from the server using the socket's input stream
       BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

       //reading user input from the console

       BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

       while (true) {
           System.out.println("enter the message or type 'exit' to quit");
           String userMsg = keyboard.readLine();
           if (userMsg.equalsIgnoreCase("exit")) {
               break;
           }
           // Convert into a structured message

           String structuredMsg = "name:Abhishek;Msg" + userMsg;

           // Send the structured message to the server
           out.println(structuredMsg);

// Read the response from the server
           String response = in.readLine();

           if (response == null) {
               System.out.println("Server closed the connection.");
               break;
           }

           //print the response from the server
           System.out.println("Response from server: " + response);


       }
         // Close the resources
         keyboard.close();
            in.close();
            out.close();
            socket.close();
         }
   catch (IOException e) {
        e.printStackTrace();

   }

    }
}
