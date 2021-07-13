package com.scritorrelo.zello.message.image;

import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.scritorrelo.zello.message.Message;
import lombok.Setter;
import lombok.ToString;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@ToString(callSuper = true)
public class Image extends Message {

    private final String type;
    private final String source;
    private final int height;
    private final int width;
    @Setter
    private ImagePacket thumbnail;
    @Setter
    private ImagePacket fullsize;

    public Image(JSONObject obj, LocalDateTime timestamp) throws JSONException {

        super(obj, timestamp);

        type = obj.getString("type");
        height = obj.optInt("height");
        width = obj.optInt("width");
        source = obj.getString("source");
    }

    @Override
    public String getSqlStatement() {
        return new InsertQuery(schema.IMAGE_TABLE)
                .addColumn(schema.UUID_IMAGE, uuid)
                .addColumn(schema.ID_IMAGE, 1)
                .addColumn(schema.CHANNEL_IMAGE, channel)
                .addColumn(schema.FOR_USER_IMAGE, forUser)
                .addColumn(schema.FROM_USER_IMAGE, fromUser)
                .addColumn(schema.TIMESTAMP_IMAGE, Timestamp.valueOf(timestamp))
                .addColumn(schema.TYPE_IMAGE, type)
                .addColumn(schema.SOURCE_IMAGE, source)
                .addColumn(schema.HEIGHT_IMAGE, height)
                .addColumn(schema.WIDTH_IMAGE, width)
                .validate()
                .toString();
    }

    public boolean isComplete() {

        return thumbnail != null && fullsize != null;
    }
}
