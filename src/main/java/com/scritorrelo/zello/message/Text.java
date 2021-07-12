package com.scritorrelo.zello.message;

import lombok.ToString;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.TimeZone;

@ToString(callSuper = true)
public class Text extends Message {

    private final String text;

    public Text(JSONObject obj, LocalDateTime timestamp) throws JSONException {
        super(obj, timestamp);
        text = obj.getString("text");
    }

    @Override
    public PreparedStatement getSqlStatement(Connection conn) throws SQLException {
        byte[] uuidBytes = new byte[16];
        ByteBuffer.wrap(uuidBytes)
                .order(ByteOrder.BIG_ENDIAN)
                .putLong(uuid.getMostSignificantBits())
                .putLong(uuid.getLeastSignificantBits());

        PreparedStatement statement = conn.prepareStatement("insert into text (uuid, channel,for_User, from_User, id, timestamp, text, test) values (?,?,?,?, ?,?,?,?)");
        statement.setBytes(1, uuidBytes);
        statement.setString(2, "John");
        statement.setString(3, "Rambo");
        statement.setInt(5, 1);
        statement.setString(8, "");
        statement.setString(4, "");
        statement.setString(7, "");
        statement.setTimestamp(6, new Timestamp(System.currentTimeMillis()), Calendar.getInstance(TimeZone.getTimeZone("UTC")));

        return statement;
    }
}
