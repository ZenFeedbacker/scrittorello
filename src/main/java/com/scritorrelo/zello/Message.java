package com.scritorrelo.zello;

import java.time.LocalDateTime;

public abstract class Message {

    String channel;
    String fromUser;
    String forUser;
    int id;
    LocalDateTime timestamp;
}
