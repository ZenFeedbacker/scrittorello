package com.scritorrelo.zello.message;

import com.healthmarketscience.sqlbuilder.InsertQuery;
import lombok.ToString;
import org.json.JSONException;
import org.json.JSONObject;

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
    public String getSqlStatement(){

        return new InsertQuery(schema.TEXT_TABLE)
                .addColumn(schema.UUID_TEXT, uuid)
                .addColumn(schema.ID_TEXT, 1)
                .addColumn(schema.CHANNEL_TEXT, channel)
                .addColumn(schema.FOR_USER_TEXT, forUser)
                .addColumn(schema.FROM_USER_TEXT, fromUser)
                .addColumn(schema.TIMESTAMP_TEXT, Timestamp.valueOf(timestamp))
                .addColumn(schema.TEXT_TEXT, text)
                .validate()
                .toString();
    }
}
