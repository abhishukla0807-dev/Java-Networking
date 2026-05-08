/*
Problem:
Current server reads raw strings directly from ClientSession, which fails
when messages arrive in partial chunks or multiple messages in one read.

Concept:
Integrate a framing protocol (LengthPrefixed or DelimiterBased) with a
PartialReadBuffer to handle partial reads and reconstruct complete messages.

Solution:
Use FrameDecoder + MessageFramer inside NioServer. Each client gets its own
PartialReadBuffer. Messages are decoded from the buffer and broadcast.

Steps:
1. Add a Map<SocketChannel, PartialReadBuffer> to track per-client buffers.
2. On OP_READ, append new data into the client’s buffer.
3. Use FrameDecoder to extract complete messages.
4. Broadcast decoded messages using MessageHandler.
*/

package com.aurexiris.networking.runtime.servers.nio;

import com.aurexiris.networking.runtime.protocols.framing.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public class NioServer {

    private static final int PORT = 8080;
    private final Selector selector;
    private final ServerSocketChannel serverChannel;
    private final List<ClientSession> clients;
    private final MessageHandler handler;

    // Track per-client buffers
    private final Map<SocketChannel, PartialReadBuffer> buffers;

    // Protocols
    private final FrameDecoder decoder;
    private final MessageFramer framer;

    public NioServer() throws IOException {
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();

        serverChannel.bind(new InetSocketAddress(PORT));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        clients = new ArrayList<>();
        handler = new MessageHandler(clients);
        buffers = new HashMap<>();

        // Choose protocol (LengthPrefixed or DelimiterBased)
        decoder = new LengthPrefixedProtocol();
        framer = new LengthPrefixedProtocol();

        System.out.println("NIO Chat Server started on port " + PORT);
    }

    public void start() throws IOException {
        while (true) {
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

    private void acceptClient() throws IOException {
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);

        ClientSession session = new ClientSession(clientChannel);
        clients.add(session);

        // Allocate buffer for this client
        buffers.put(clientChannel, new PartialReadBuffer(1024));

        System.out.println("Connected: " + clientChannel.getRemoteAddress());
    }

    private void readMessage(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ClientSession session = findSession(clientChannel);

        if (session != null) {
            try {
                PartialReadBuffer readBuffer = buffers.get(clientChannel);
                ByteBuffer buf = ByteBuffer.allocate(1024);

                int bytesRead = clientChannel.read(buf);
                if (bytesRead == -1) {
                    clients.remove(session);
                    buffers.remove(clientChannel);
                    clientChannel.close();
                    System.out.println("Client disconnected");
                    return;
                }

                buf.flip();
                readBuffer.append(buf);

                // Try decoding complete messages
                ByteBuffer internal = readBuffer.getBuffer();
                String msg;
                while ((msg = decoder.decode(internal)) != null) {
                    System.out.println("Received: " + msg);
                    handler.broadcast(session, msg);
                }
                readBuffer.compact();

            }
            catch (Exception e) {
                clientChannel.close();
                clients.remove(session);
                buffers.remove(clientChannel);
                System.out.println("Error: Client disconnected unexpectedly");
            }
        }
    }

    private ClientSession findSession(SocketChannel channel) {
        for (ClientSession client : clients) {
            if (client.getChannel().equals(channel)) {
                return client;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        try {
            new NioServer().start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
