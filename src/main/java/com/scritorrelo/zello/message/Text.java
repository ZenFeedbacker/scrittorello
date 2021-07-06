package com.scritorrelo.zello.message;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;

public class Text extends Message {

    String text;

    public Text(JSONObject obj, LocalDateTime timestamp) throws JSONException {
        super(obj, timestamp);
        text = obj.getString("text");
    }

    @Override
    public PreparedStatement getPreparedStatement(Connection conn) throws SQLException {
        PreparedStatement st = conn.prepareStatement("insert into TEXTS (uid, id, ts, channel, fromUser, forUser, text) values (?, ?, ?, ?, ?, ?, ?);");

        st.setObject(1, uuid);
        st.setInt(2, id);
        st.setTimestamp(3, Timestamp.valueOf(timestamp));
        st.setString(4, channel);
        st.setString(5, fromUser);
        st.setString(6, forUser);
        st.setString(7, text);

        return st;    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Timestamp: ").append(timestamp.toString()).append("\n");
        stringBuilder.append("Channel: ").append(channel).append("\n");
        stringBuilder.append("From: ").append(fromUser).append("\n");
        stringBuilder.append("Message ID: ").append(id).append("\n");
        stringBuilder.append("Text: ").append(text).append("\n");

        if (!isNull(forUser)) {
            stringBuilder.append("For:").append(forUser).append("\n");
        }

        return stringBuilder.toString();
    }
}
