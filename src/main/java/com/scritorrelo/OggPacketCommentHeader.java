package com.scritorrelo;

import java.io.EOFException;

public class OggPacketCommentHeader extends  OggPacket{
    public OggPacketCommentHeader(byte[] data) throws EOFException {
        super(data);
    }
}
