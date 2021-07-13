package com.scritorrelo.zello.message;

import lombok.ToString;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@ToString(callSuper = true)
public class Text extends Message {

    private final String text;

    public Text(JSONObject obj, LocalDateTime timestamp) throws JSONException {
        super(obj, timestamp);
        text = obj.getString("text");
    }

    @Override
    public PreparedStatement getSqlStatement(Connection conn) throws SQLException {

        String  sqlStatement = "INSERT INTO TEXT (UUID,ID,CHANNEL,FROM_USER,FOR_USER,TIMESTAMP,TEXT) VALUES (?,?,?,?,?,?,?)";

        PreparedStatement statement = conn.prepareStatement(sqlStatement);

        statement.setObject(1, uuid);
        statement.setInt(2, id);
        statement.setString(3, channel);
        statement.setString(4, fromUser);
        statement.setString(5, forUser);
        statement.setTimestamp(6, Timestamp.valueOf(timestamp));
        statement.setString(7, text);

        return statement;
    }
}
