/*
Problem:
Managing multiple clients directly with raw SocketChannels and ByteBuffers inside the server loop
quickly becomes messy and hard to maintain. Each client needs its own state (connection + buffer),
and the server must know how to read and write messages cleanly.

Concept:
- We encapsulate each client connection in a ClientSession class.
- This abstraction holds the SocketChannel and its ByteBuffer, and provides methods to read and send messages.
- This keeps the server code clean and makes it easier to manage multiple clients.

Solution:
We implement a ClientSession class that wraps a SocketChannel and ByteBuffer. It provides
methods to read incoming messages and send outgoing messages. If a client disconnects,
the session closes gracefully.

Steps to Implement:
1. Store the SocketChannel representing the client connection.
2. Allocate a ByteBuffer for temporary message storage per client.
3. Provide getters for channel and buffer so the server can access them.
4. Implement readMessage() to read data from the client and convert it to a String.
5. Implement sendMessage() to write a String back to the client.
6. Handle client disconnection by closing the channel when read returns -1.
*/

package com.aurexiris.networking.runtime.servers.nio;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientSession {
    private final SocketChannel channel;   // Each client's connection pipe
    private final ByteBuffer buffer;       // Per-client temporary storage

    public ClientSession(SocketChannel channel) {
        this.channel = channel;
        this.buffer = ByteBuffer.allocate(256); // Allocate buffer for messages
    }

    // Server can access the client's channel (connection)
    public SocketChannel getChannel() {
        return channel;
    }

    // Server can access the client's buffer like a temporary workspace for reading/writing.
    public ByteBuffer getBuffer() {
        return buffer;
    }

    // Read a message from the client
    public String readMessage() throws Exception {
        buffer.clear(); // Reset buffer for new data
        int bytesRead = channel.read(buffer);

        if (bytesRead == -1) {
            channel.close(); // Client disconnected
            return null;
        }

        buffer.flip(); // Switch buffer to read mode
        return new String(buffer.array(), 0, buffer.limit()).trim();
    }

    // Send a message to the client
    public void sendMessage(String msg) throws Exception {
        channel.write(ByteBuffer.wrap((msg + "\n").getBytes()));
    }
}
