package com.scritorrelo.zello.message;

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
public class Text extends Message {

    private String text;
    private String test;

    public Text(JSONObject obj, LocalDateTime timestamp) throws JSONException {
        super(obj, timestamp);
        text = obj.getString("text");
    }
}
