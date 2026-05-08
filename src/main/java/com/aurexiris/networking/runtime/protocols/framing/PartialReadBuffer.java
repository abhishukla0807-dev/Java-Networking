/*
Problem:
In NIO, reads may return partial data. Without buffering,
messages can be lost or misinterpreted.

Concept:
PartialReadBuffer stores incomplete data until a full frame is available.

Solution:
Maintain an internal ByteBuffer that accumulates reads.

Steps:
1. Append new data into buffer.
2. Allow decoder to extract complete frames.
3. Preserve leftover bytes for next read.
*/

package com.aurexiris.networking.runtime.protocols.framing;

import java.nio.ByteBuffer;

public class PartialReadBuffer {
    private final ByteBuffer buffer;

    public PartialReadBuffer(int capacity) {
        this.buffer = ByteBuffer.allocate(capacity);
    }

    public void append(ByteBuffer src) {
        buffer.put(src);
    }

    public ByteBuffer getBuffer() {
        buffer.flip();
        return buffer;
    }

    public void compact() {
        buffer.compact();
    }
}
