/*
Problem:
Length-prefixed requires fixed headers. Sometimes simpler protocols
use delimiters (e.g., newline) to mark message boundaries.

Concept:
Messages end with a delimiter character (e.g., '\n').

Solution:
Implement FrameDecoder + MessageFramer using delimiter.

Steps:
1. Frame: append delimiter to message.
2. Decode: scan buffer until delimiter found.
3. Extract message, leave remainder for next read.
*/

package com.aurexiris.networking.runtime.protocols.framing;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class DelimiterBasedProtocol implements FrameDecoder, MessageFramer {
    private static final byte DELIMITER = '\n';

    @Override
    public String decode(ByteBuffer buffer) {
        buffer.mark();
        int start = buffer.position();
        while (buffer.hasRemaining()) {
            if (buffer.get() == DELIMITER) {
                int end = buffer.position();
                int length = end - start - 1;
                byte[] data = new byte[length];
                buffer.reset();
                buffer.get(data);
                buffer.get(); // consume delimiter
                return new String(data, StandardCharsets.UTF_8);
            }
        }
        buffer.reset();
        return null; // no complete message yet
    }

    @Override
    public ByteBuffer frame(String message) {
        byte[] data = message.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(data.length + 1);
        buffer.put(data);
        buffer.put(DELIMITER);
        buffer.flip();
        return buffer;
    }
}
