package com.scritorrelo.zello.message.image;

import com.scritorrelo.zello.message.Message;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;

import java.sql.*;
import java.time.LocalDateTime;

@ToString(callSuper = true)
public class Image extends Message {

    private static final String SQL_STATEMENT = "INSERT INTO IMAGE (UUID,ID,CHANNEL,FROM_USER,FOR_USER,TIMESTAMP,TYPE,SOURCE,HEIGHT,WIDTH,THUMBNAIL,FULLSIZE) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

    private final String type;
    private final String source;
    private final int height;
    private final int width;
    @Setter
    private ImagePacket thumbnail;
    @Setter
    private ImagePacket fullsize;

    public Image(JSONObject obj, LocalDateTime timestamp) {

        super(obj, timestamp);

        type = obj.getString("type");
        height = obj.optInt("height");
        width = obj.optInt("width");
        source = obj.getString("source");
    }

    @Override
    public PreparedStatement getSqlStatement(Connection conn) throws SQLException {

        Clob thumbnailClob = conn.createClob();
        thumbnailClob.setString(1, Hex.encodeHexString(thumbnail.getData()));


        Clob fullSizeClob = conn.createClob();
        thumbnailClob.setString(1, Hex.encodeHexString(fullsize.getData()));

        var statement = conn.prepareStatement(SQL_STATEMENT);

        statement.setObject(1, uuid);
        statement.setInt(2, id);
        statement.setString(3, channel);
        statement.setString(4, fromUser);
        statement.setString(5, forUser);
        statement.setTimestamp(6, Timestamp.valueOf(timestamp));
        statement.setString(7, type);
        statement.setString(8, source);
        statement.setInt(9, height);
        statement.setInt(10, width);
        statement.setClob(11, thumbnailClob);
        statement.setClob(12, fullSizeClob);

        return statement;
    }

    public boolean isComplete() {
        return thumbnail != null && fullsize != null;
    }
}
