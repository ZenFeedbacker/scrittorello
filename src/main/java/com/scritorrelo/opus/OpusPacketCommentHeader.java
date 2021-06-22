package com.scritorrelo.opus;

import com.scritorrelo.Utils;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.List;

public class OpusPacketCommentHeader extends OpusPacket {

    String signature;
    int vendorStrLen;
    String vendorStr;
    int userCommentListLen;
    List<Integer> userCommentLens;
    List<String> userComments;


    public OpusPacketCommentHeader(byte[] data) throws EOFException {

        super(data);

        signature = Utils.readByteStreamToString(stream, 8);
        vendorStrLen = Utils.readByteStreamToInt(stream, 4);
        vendorStr = Utils.readByteStreamToString(stream, vendorStrLen);
        userCommentListLen = Utils.readByteStreamToInt(stream, 4);

        userComments = new ArrayList<>();
        userCommentLens = new ArrayList<>();

        for (int i = 0; i < userCommentListLen; i++) {
            int len = Utils.readByteStreamToInt(stream, 4);
            userCommentLens.add(len);
            userComments.add(Utils.readByteStreamToString(stream, len));
        }
    }

    @Override
    public String toString() {


        StringBuilder str = new StringBuilder("Length: " + data.length + "\n" +
                "Signature: " + signature + "\n" +
                "Vendor String Length: " + vendorStrLen + "\n" +
                "Vendor String: " + vendorStr + "\n" +
                "User Comment List Length: " + userCommentListLen + "\n");

        for (int i = 0; i < userCommentListLen; i++) {
            str.append("Comment #").append(i).append(" Length: ").append(userCommentLens.get(i)).append("\n").append("Comment #").append(i).append(": ").append(userComments.get(i)).append("\n");
        }

        return str.toString();
    }
}
