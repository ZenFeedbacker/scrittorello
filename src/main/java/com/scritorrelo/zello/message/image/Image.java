package com.scritorrelo.zello.message.image;

import com.scritorrelo.zello.message.Message;
import lombok.ToString;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@ToString(callSuper = true)
public class Image extends Message {

    private String type;
    private String source;
    private  int height;
    private int width;

    public Image(JSONObject obj, LocalDateTime timestamp) throws JSONException {

        super(obj, timestamp);

        type = obj.getString("type");
        height = obj.optInt("height");
        width = obj.optInt("width");
        source = obj.getString("source");
    }

    @Override
    public PreparedStatement getPreparedStatement(Connection conn) throws SQLException {
        PreparedStatement st = conn.prepareStatement("insert into IMAGES (uid, id, ts, channel, fromUser, forUser, type, source, height, width) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

        st.setObject(1, uuid);
        st.setInt(2, id);
        st.setTimestamp(3, Timestamp.valueOf(timestamp));
        st.setString(4, channel);
        st.setString(5, fromUser);
        st.setString(6, forUser);
        st.setString(7, type);
        st.setString(8, source);
        st.setInt(9, height);
        st.setInt(10, width);

        return st;
    }
}
