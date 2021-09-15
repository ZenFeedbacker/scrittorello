package com.scritorrelo.zello.message.image;

import com.scritorrelo.zello.message.Message;
import com.scritorrelo.zello.message.audio.AudioFrame;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.stream.Collectors;

@ToString(callSuper = true)
public class Image extends Message {

    private static final String SQL_STATEMENT =  "INSERT INTO IMAGE (UUID,ID,CHANNEL,FROM_USER,FOR_USER,TIMESTAMP,TYPE,SOURCE,HEIGHT,WIDTH,THUMBNAIL,FULLSIZE) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

    private final String type;
    private final String source;
    private final int height;
    private final int width;
    @Setter
    private ImagePacket thumbnail;
    @Setter
    private ImagePacket fullsize;

    public Image(JSONObject obj, LocalDateTime timestamp)  {

        super(obj, timestamp);

        type = obj.getString("type");
        height = obj.optInt("height");
        width = obj.optInt("width");
        source = obj.getString("source");
    }

    @Override
    public PreparedStatement getSqlStatement(Connection conn) throws SQLException {

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
        statement.setString(11, Hex.encodeHexString(thumbnail.getData()));
        statement.setString(12, Hex.encodeHexString(fullsize.getData()));

        return statement;
    }

    public boolean isComplete() {

        return thumbnail != null && fullsize != null;
    }
}
