package com.scritorrelo.zello.message;

import com.scritorrelo.DatabaseSchema;
import lombok.Getter;
import lombok.ToString;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@ToString
public abstract class Message {

    protected DatabaseSchema schema;

    protected final UUID uuid;
    @Getter
    protected final int id;
    protected final String channel;
    protected final String fromUser;
    protected final String forUser;
    protected final LocalDateTime timestamp;

    public Message(JSONObject obj, LocalDateTime timestamp) throws JSONException {

        this.schema = DatabaseSchema.getINSTANCE();

        this.uuid = UUID.randomUUID();

        this.timestamp = timestamp;

        channel = obj.getString("channel");
        fromUser = obj.getString("from");
        forUser = obj.optString("for");

        id = obj.optInt("message_id") != 0 ? obj.optInt("message_id") : obj.optInt("stream_id");
    }

    public abstract String getSqlStatement();

    protected byte[] uuidToByteArray(){

        byte[] uuidBytes = new byte[16];
        ByteBuffer.wrap(uuidBytes)
                .order(ByteOrder.BIG_ENDIAN)
                .putLong(uuid.getMostSignificantBits())
                .putLong(uuid.getLeastSignificantBits());

        return  uuidBytes;
    }

    protected List<Field> getFieldList(){
        Class<?> clazz = this.getClass().getSuperclass();
        List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));

        clazz = this.getClass();
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

        return  fields;
    }
}
