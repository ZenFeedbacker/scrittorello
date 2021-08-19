package com.scritorrelo.zello.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.json.JSONObject;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

@ToString
@SuperBuilder
@NoArgsConstructor
public abstract class Message implements Serializable {

    public static final String MESSAGE_FOLDER = "\\data\\messages\\";
    private static final long serialVersionUID = -365386493668373640L;

    @Getter
    public UUID uuid;
    @Getter
    public int id;
    public String channel;
    public String fromUser;
    public String forUser;
    public LocalDateTime timestamp;

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
