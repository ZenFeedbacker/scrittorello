package com.scritorrelo.zello.message.image;

import com.scritorrelo.zello.message.Message;
import lombok.ToString;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;

@ToString(callSuper = true)
public class Image extends Message {

    private final String type;
    private final String source;
    private final int height;
    private final int width;

    public Image(JSONObject obj, LocalDateTime timestamp) throws JSONException {

        super(obj, timestamp);

        type = obj.getString("type");
        height = obj.optInt("height");
        width = obj.optInt("width");
        source = obj.getString("source");
    }

    @Override
    public PreparedStatement getSqlStatement(Connection conn) {
        return null;
    }
}
