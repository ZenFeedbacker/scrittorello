package com.scritorrelo.zello.message;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Message {

    protected final UUID uuid;
    protected final int id;
    protected final String channel;
    protected final String fromUser;
    protected final String forUser;
    protected final LocalDateTime timestamp;

    public Message(JSONObject obj, LocalDateTime timestamp) throws JSONException {

        this.uuid = UUID.randomUUID();

        this.timestamp = timestamp;

        channel = obj.getString("channel");
        fromUser = obj.getString("from");
        forUser = obj.optString("for");

        id = obj.optInt("message_id") != 0 ? obj.optInt("message_id") : obj.optInt("stream_id");
    }

    public abstract PreparedStatement getSqlStatement(Connection conn) throws SQLException;
}
