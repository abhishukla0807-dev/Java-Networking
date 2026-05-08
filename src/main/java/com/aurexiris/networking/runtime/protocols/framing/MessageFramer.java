/*
Problem:
Messages must be encoded into a framed format before sending,
otherwise receivers can’t reconstruct them correctly.

Concept:
MessageFramer takes a logical message and applies framing rules
(length prefix, delimiter, etc.).

Solution:
Provide a method to frame outgoing messages.

Steps:
1. Accept a String message.
2. Encode into bytes with framing.
3. Return a ByteBuffer ready for writing.
*/

package com.aurexiris.networking.runtime.protocols.framing;

import java.nio.ByteBuffer;

public interface MessageFramer {
    ByteBuffer frame(String message);
}

