package com.scritorrelo.zello;

import lombok.ToString;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;

@ToString(callSuper = true)
public class Image extends Message{
    String type;
    int height;
    int width;
    String source;

    public Image(JSONObject obj, LocalDateTime timestamp) throws JSONException {
        super(obj, timestamp);
        type = obj.getString("type");
        height = obj.optInt("height");
        width = obj.optInt("width");
        source = obj.getString("source");

    }
}
