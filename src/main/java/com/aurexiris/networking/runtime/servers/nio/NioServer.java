/*
Problem:
A single-threaded TCP server blocks on one client connection, preventing other clients from connecting or communicating concurrently.

Concept:
- The Non-Blocking I/O (NIO) model solves this by allowing a single thread to monitor thousands of client connections simultaneously.
- The main server thread uses a Selector to watch multiple channels, and only reacts when a channel is ready for I/O.
- This enables multiple clients to interact with the server efficiently without dedicating one thread per client.

Solution:
We implement a NioServer that listens to on port 8080, accepts incoming connections, and uses ClientSession + MessageHandler
to manage communication. Each client is represented by a non-blocking SocketChannel, and messages are broadcast to all
other clients in a WhatsApp-style group chat system.

Steps to Implement:
1. Create a NioServer class with a ServerSocketChannel bound to port 8080.
2. Configure the server channel as non-blocking and register it with a Selector for OP_ACCEPT events.
3. In a loop, use selector.select() to wait for events (new connections or incoming data).
4. On OP_ACCEPT, accept the client, wrap it in a ClientSession, and register for OP_READ.
5. On OP_READ, read the message from the client’s buffer, and use MessageHandler to broadcast it to all other clients.
6. Run the server, then connect multiple clients to test concurrent communication.
*/

package com.aurexiris.networking.runtime.servers.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.*;

public class NioServer {

    private static final int PORT = 8080;
    private final Selector selector;
    private final ServerSocketChannel serverChannel;
    private final List<ClientSession> clients;
    private final MessageHandler handler;

    public NioServer() throws IOException {
        // Open selector and server channel
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();

        // Bind server to port 8080
        serverChannel.bind(new InetSocketAddress(PORT));
        serverChannel.configureBlocking(false);

        // Register a server channel with selector for ACCEPT events
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        // Initialize client list and message handler
        clients = new ArrayList<>();
        handler = new MessageHandler(clients);

        System.out.println("NIO Chat Server started on port " + PORT);
    }

    public void start() throws IOException {
        while (true) {
            // Wait for events
            selector.select();
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();

                if (key.isAcceptable()) {
                    acceptClient();
                } else if (key.isReadable()) {
                    readMessage(key);
                }
            }
        }
    }

    // Accept new client connections
    private void acceptClient() throws IOException {
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);

        // Register client for READ events
        clientChannel.register(selector, SelectionKey.OP_READ);

        // Wrap in ClientSession and add to list
        ClientSession session = new ClientSession(clientChannel);
        clients.add(session);

        System.out.println("Connected: " + clientChannel.getRemoteAddress());
    }

    // Read the message from client and broadcast
    private void readMessage(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ClientSession session = findSession(clientChannel);

        if (session != null) {
            try {
                String msg = session.readMessage();
                if (msg == null) {
                    clients.remove(session);
                    System.out.println("Client disconnected");
                } else {
                    System.out.println("Received: " + msg);
                    handler.broadcast(session, msg);
                }
            }
            catch (Exception e) {
                clientChannel.close();
                clients.remove(session);
                System.out.println("Error: Client disconnected unexpectedly");
            }
        }
    }

    // Helper to find ClientSession by channel
    private ClientSession findSession(SocketChannel channel) {
        for (ClientSession client : clients) {
            if (client.getChannel().equals(channel)) {
                return client;
            }
        }
        return null;
    }

    // Entry point
    public static void main(String[] args) {
        try {
            new NioServer().start();
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

