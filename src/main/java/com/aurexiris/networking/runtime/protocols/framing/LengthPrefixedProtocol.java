/*
Problem:
Delimiter-based framing can fail if message contains delimiter characters.
Length-prefixed framing avoids this by specifying message size upfront.

Concept:
Each message starts with a fixed-size header (e.g., 4 bytes) indicating length.

Solution:
Implement FrameDecoder + MessageFramer using length prefix.

Steps:
1. Frame: prepend message length as 4-byte int.
2. Decode: read length, then read that many bytes.
3. Handle partial reads until full message is available.
*/

package com.aurexiris.networking.runtime.protocols.framing;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class LengthPrefixedProtocol implements FrameDecoder, MessageFramer {

    @Override
    public String decode(ByteBuffer buffer) {
        if (buffer.remaining() < 4) return null; // not enough for length
        buffer.mark();
        int length = buffer.getInt();
        if (buffer.remaining() < length) {
            buffer.reset();
            return null; // wait for more data
        }
        byte[] data = new byte[length];
        buffer.get(data);
        return new String(data, StandardCharsets.UTF_8);
    }

    @Override
    public ByteBuffer frame(String message) {
        byte[] data = message.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(4 + data.length);
        buffer.putInt(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }
}
