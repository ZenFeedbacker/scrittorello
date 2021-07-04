package com.scritorrelo.zello;

public class Channel {

    String name;
    ChannelStatus status;
    int usersOnline;
    boolean imagesSupported;
    boolean textingSupported;
    boolean locationsSupported;
    String error;
    String errorType;

    private enum ChannelStatus {
        online, offline;
    }
}
