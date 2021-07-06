package com.scritorrelo.zello.message;

import lombok.Getter;
import lombok.ToString;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.UUID.randomUUID;

@ToString
@Getter
public abstract class Message {

    protected UUID uuid;
    protected int id;
    protected String channel;
    protected String fromUser;
    protected String forUser;
    protected LocalDateTime timestamp;

    public Message(JSONObject obj, LocalDateTime timestamp) throws JSONException {

        uuid = randomUUID();

        this.timestamp = timestamp;

        channel = obj.getString("channel");
        fromUser = obj.getString("from");
        forUser = obj.optString("for");

        try {
            id = obj.getInt("message_id");
        } catch (JSONException e1) {
            id = obj.optInt("stream_id");
        }
    }

    abstract public PreparedStatement getPreparedStatement(Connection conn) throws SQLException;
}
