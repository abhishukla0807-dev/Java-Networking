/*
Problem:
Raw byte streams don’t indicate where one message ends and another begins.
Without decoding, partial or combined messages cause corruption.

Concept:
A FrameDecoder reads from a ByteBuffer and extracts complete frames
based on a protocol (length-prefixed, delimiter-based, etc.).

Solution:
Implement a reusable interface for decoding frames from buffers.

Steps:
1. Define a method to decode frames.
2. Return null if the buffer doesn’t yet contain a full frame.
3. Let protocol-specific classes implement this interface.
*/

package com.aurexiris.networking.runtime.protocols.framing;

import java.nio.ByteBuffer;

public interface FrameDecoder {
    String decode(ByteBuffer buffer);
}
