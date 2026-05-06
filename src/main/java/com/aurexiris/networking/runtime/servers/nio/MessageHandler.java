/*
Problem:
When multiple clients are connected to the server, simply echoing messages back to the sender
is not enough.
- We need a way to broadcast one client’s message to all other connected clients,
like in a group chat system.

Concept:
- A MessageHandler class manages broadcasting.
- It takes a list of connected ClientSession objects and forwards any incoming message from one client to all others.
- This separates business logic (message distribution) from low-level I/O handling.

Solution:
- We implement a MessageHandler that holds a list of ClientSession objects.
- When a client sends a message, the handler loops through all sessions and sends the message to everyone except
  the sender.
- This creates a WhatsApp-style group chat experience.

Steps to Implement:
1. Store a list of ClientSession objects representing all connected clients.
2. Provide a constructor to initialize the handler with this list.
3. Implement a broadcast() method that takes the sender and the message.
4. Loop through all clients in the list.
5. For each client (except the sender), call sendMessage() to deliver the message.
6. Attach sender information (like address) to the message for clarity.
*/

package com.aurexiris.networking.runtime.servers.nio;

import java.util.List;

public class MessageHandler {

    private final List<ClientSession> clients; // All connected clients

    public MessageHandler(List<ClientSession> clients) {
        this.clients = clients;
    }

    // Broadcast a message from one client to all others
    public void broadcast(ClientSession sender, String msg) throws Exception {
        for (ClientSession client : clients) {
            if (client != sender) {
                // Add sender info before forwarding
                client.sendMessage("[" + sender.getChannel().getRemoteAddress() + "]: " + msg);
            }
        }
    }
}
