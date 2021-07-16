package com.scritorrelo.socket;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.scritorrelo.DatabaseManager;
import com.scritorrelo.ogg.OggFile;
import com.scritorrelo.ogg.OggStream;
import com.scritorrelo.zello.Channel;
import com.scritorrelo.zello.Command;
import com.scritorrelo.zello.message.Location;
import com.scritorrelo.zello.message.Message;
import com.scritorrelo.zello.message.Text;
import com.scritorrelo.zello.message.audio.AudioFrame;
import com.scritorrelo.zello.message.audio.Audio;
import com.scritorrelo.zello.error.Error;
import com.scritorrelo.zello.message.image.Image;
import com.scritorrelo.zello.message.image.ImagePacket;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;

import static java.util.Objects.isNull;

@Controller
@Scope("prototype")
@Slf4j
public class SocketAdapter extends WebSocketAdapter {

    @Setter
    private Socket ws;

    @Autowired
    private DatabaseManager dbManager;

    private final HashMap<Integer, Audio> audios = new HashMap<>();
    private final HashMap<Integer, Image> images = new HashMap<>();

    @Override
    public void onTextMessage(WebSocket websocket, String message) throws JSONException, IOException {

        LocalDateTime timestamp = LocalDateTime.now();
        JSONObject obj = new JSONObject(message);
        log.info(ws.toString() + ": " + message);

        if (obj.has("refresh_token")) {
            refreshTokenHandler(obj);
            return;
        }

        if (obj.has("command")) {
            String cmd = obj.getString("command");

            Command command = Command.valueOfLabel(cmd);

            if(isNull(command)){
                log.warn("Received unknown command: " + cmd);
                return;
            }

            switch (command) {
                case STREAM_START:
                    streamStartHandler(obj, timestamp);
                    break;
                case STREAM_STOP:
                    streamStopHandler(obj);
                    break;
                case TEXT:
                    textMessageHandler(obj, timestamp);
                    break;
                case IMAGE:
                    imageMessageHandler(obj, timestamp);
                    break;
                case LOCATION:
                    locationMessageHandler(obj, timestamp);
                    break;
                case CHANNEL_STATUS:
                    channelStatusHandler(obj, timestamp);
                    break;
                case ERROR:
                    errorHandler(obj, timestamp);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onBinaryMessage(WebSocket websocket, byte[] binary) {

        switch (binary[0]) {
            case ((byte) 1):
                audioBinaryHandler(binary);
                break;
            case ((byte) 2):
                imageBinaryHandler(binary);
                break;
            default:
                log.warn("Received unknown binary, type " + binary[0]);
                break;
        }
    }

    private void refreshTokenHandler(JSONObject obj) throws JSONException {

        ws.setRefreshToken(obj.getString("refresh_token"));
    }

    private void imageBinaryHandler(byte[] binary) {

        ImagePacket packet = new ImagePacket(binary);
        log.info("Received" + (packet.isThumbnail() ? "Thumbnail" : "Full Image") + "image binary");

        int id = packet.getId();

        if (images.containsKey(id)) {

            Image image = images.get(id);

            if (packet.isThumbnail()) {
                image.setThumbnail(packet);
            } else {
                image.setFullsize(packet);
            }

            if (image.isComplete()){
                image.saveFiles();
                dbManager.saveMessage(image);
                images.remove(image.getId());
            }
        }
    }

    private void audioBinaryHandler(byte[] binary) {
        log.info("Received audio binary");
        AudioFrame audioFrame = new AudioFrame(binary);
        int id = audioFrame.getStreamId();
        if(audios.containsKey(id)) {
            audios.get(id).addFrame(audioFrame);
        }
    }

    private void channelStatusHandler(JSONObject obj, LocalDateTime timestamp) throws JSONException {
        Channel channel = new Channel(obj, timestamp);
        log.info(channel.toString());
    }

    private void errorHandler(JSONObject obj, LocalDateTime timestamp) throws JSONException {
        Error error = new Error(obj, timestamp);
        log.error(error.toString());
    }

    private void locationMessageHandler(JSONObject obj, LocalDateTime timestamp) throws JSONException {
        Location location = new Location(obj, timestamp);
        dbManager.saveMessage(location);
        log.info(location.toString());
    }

    private void textMessageHandler(JSONObject obj, LocalDateTime timestamp) throws JSONException {
        Text text = new Text(obj, timestamp);
        dbManager.saveMessage(text);
    }

    private void imageMessageHandler(JSONObject obj, LocalDateTime timestamp) throws JSONException {
        Image image = new Image(obj, timestamp);
        images.put(image.getId(), image);
        log.info(image.toString());
    }

    private void streamStartHandler(JSONObject obj, LocalDateTime timestamp) throws JSONException {
        Audio stream = new Audio(obj, timestamp);
        audios.put(obj.getInt("stream_id"), stream);
    }

    private void streamStopHandler(JSONObject obj) throws JSONException, IOException {
        Audio audio = audios.remove(obj.getInt("stream_id"));
        OggStream oggStream = new OggStream(audio.getOpusStream());
        OggFile oggFile = new OggFile(oggStream);
        String path =  System.getProperty("user.dir") + Message.MESSAGE_FOLDER +  "audios\\" + audio.getUuid().toString() + ".ogg";
        oggFile.writeToFile(path);
        log.info("Wrote file " + path);
        dbManager.saveMessage(audio);
    }
}
