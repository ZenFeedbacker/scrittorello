package com.scritorrelo.zello.message.image;

import com.scritorrelo.zello.message.Message;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
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
}
