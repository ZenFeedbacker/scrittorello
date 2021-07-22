package com.scritorrelo.zello.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

@ToString
@NoArgsConstructor
public abstract class Message implements Serializable {

    public static final String MESSAGE_FOLDER = "\\data\\messages\\";
    private static final long serialVersionUID = -365386493668373640L;

    @Getter
    protected UUID uuid;
    @Getter
    protected int id;
    protected String channel;
    protected String fromUser;
    protected String forUser;
    protected LocalDateTime timestamp;

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
