package com.scritorrelo.opus;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Stream {

    @Setter
    IDHeaderPacket idHeaderPacket;
    List<CommentHeaderPacket> commentHeaderPackets;
    List<Packet> dataPackets;


    public Stream() {

        commentHeaderPackets = new ArrayList<>();
        dataPackets = new ArrayList<>();
    }


    public void addCommentPacket(CommentHeaderPacket packet){
        commentHeaderPackets.add(packet);
    }

    public void addDataPacket(DataPacket packet){
        dataPackets.add(packet);
    }
}
