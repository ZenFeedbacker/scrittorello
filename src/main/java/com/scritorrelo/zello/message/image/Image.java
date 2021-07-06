package com.scritorrelo.zello.message.image;

import com.scritorrelo.zello.message.Message;
import lombok.ToString;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

@ToString(callSuper = true)
public class Image extends Message {

    String type;
    String source;
    int height;
    int width;

    public Image(JSONObject obj, LocalDateTime timestamp) throws JSONException {

        super(obj, timestamp);

        type = obj.getString("type");
        height = obj.optInt("height");
        width = obj.optInt("width");
        source = obj.getString("source");
    }

    @Override
    public PreparedStatement getPreparedStatement(Connection conn) throws SQLException {
        return null;
    }
}
