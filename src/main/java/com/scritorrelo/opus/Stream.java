package com.scritorrelo.opus;

import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class Stream {

    List<Packet> packetList;

    public void addPacket(Packet packet){
        packetList.add(packet);
    }
}
