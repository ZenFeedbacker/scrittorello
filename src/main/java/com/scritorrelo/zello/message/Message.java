package com.scritorrelo.zello.message;

import lombok.Getter;
import lombok.ToString;
import org.json.JSONObject;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

@ToString
public abstract class Message  {

    protected static final String MESSAGE_FOLDER = File.separator + "data" + File.separator  + "messages" + File.separator ;

    @Getter
    protected final UUID uuid;
    @Getter
    protected final int id;
    protected final String channel;
    protected final String fromUser;
    protected final String forUser;
    protected final LocalDateTime timestamp;

    protected Message(JSONObject obj, LocalDateTime ts) {

        uuid = UUID.randomUUID();

        timestamp = ts;

        channel = obj.optString("channel");
        fromUser = obj.optString("from");
        forUser = obj.optString("for");

        id = obj.optInt("message_id") != 0 ? obj.optInt("message_id") : obj.optInt("stream_id");
    }

    public abstract PreparedStatement getSqlStatement(Connection conn) throws SQLException;
}
