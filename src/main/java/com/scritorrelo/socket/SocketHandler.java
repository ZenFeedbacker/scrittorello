package com.scritorrelo.socket;

import com.scritorrelo.DatabaseManager;
import com.scritorrelo.zello.ChannelStatus;
import com.scritorrelo.zello.Command;
import com.scritorrelo.zello.error.Error;
import com.scritorrelo.zello.message.Location;
import com.scritorrelo.zello.message.Text;
import com.scritorrelo.zello.message.audio.Audio;
import com.scritorrelo.zello.message.audio.AudioFrame;
import com.scritorrelo.zello.message.image.Image;
import com.scritorrelo.zello.message.image.ImagePacket;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.time.LocalDateTime;
import java.util.HashMap;

import static com.scritorrelo.socket.SocketManager.KEY_COMMAND;
import static java.util.Objects.isNull;

@Slf4j
@Service
public class SocketHandler extends AbstractWebSocketHandler  {

    private static final int BINARY_BUFFER_SIZE = 2000000;

    @Autowired
    private DatabaseManager dbManager;

    @Autowired
    private SocketManager socketManager;

    private final HashMap<Integer, Audio> audios = new HashMap<>();
    private final HashMap<Integer, Image> images = new HashMap<>();

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {

        super.afterConnectionEstablished(session);
        session.setBinaryMessageSizeLimit(BINARY_BUFFER_SIZE);
        socketManager.setWsSession(session);
        socketManager.login();
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {

        super.handleTextMessage(session, message);
        var timestamp = LocalDateTime.now();

        log.trace(message.getPayload());

        var obj = new JSONObject(message.getPayload());

        if (obj.has("refresh_token")) {
            refreshTokenHandler(obj);
            return;
        }

        if (obj.has("error")) {
            errorMessageHandler(obj);
            return;
        }

        if (obj.has(KEY_COMMAND)) {
            var cmd = obj.getString(KEY_COMMAND);

            var command = Command.valueOfLabel(cmd);

            if (isNull(command)) {
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
                    channelStatusHandler(obj);
                    break;
                case ERROR:
                    errorHandler(obj);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void handleBinaryMessage(@NonNull WebSocketSession session, @NonNull BinaryMessage message) throws Exception {

        super.handleBinaryMessage(session, message);

        var binary = message.getPayload().array();

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

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus){

        try {
            super.afterConnectionClosed(session, closeStatus);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.error("Channel was disconnected, close code is {}, reason is {}.", closeStatus.getCode(), closeStatus.getReason());
        socketManager.reconnect();
    }

    private void refreshTokenHandler(JSONObject obj) {

        socketManager.setRefreshToken(obj.optString("refresh_token"));
    }

    private void errorMessageHandler(JSONObject obj) {

        log.error("Got error message: {} ", obj.getString("error"));
    }

    private void imageBinaryHandler(byte[] binary) {

        var packet = new ImagePacket(binary);
        log.info("Channel received " + (packet.isThumbnail() ? "Thumbnail" : "Full Image") + " binary");

        var id = packet.getId();

        if (images.containsKey(id)) {

            var image = images.get(id);

            if (packet.isThumbnail()) {
                image.setThumbnail(packet);
            } else {
                image.setFullsize(packet);
            }

            if (image.isComplete()) {
                dbManager.saveMessage(image);
                images.remove(image.getId());
            }
        }
    }

    private void audioBinaryHandler(byte[] binary) {

        var audioFrame = new AudioFrame(binary);
        var id = audioFrame.getStreamId();

        log.trace("Channel received audio binary for stream {}", id);

        if (audios.containsKey(id)) {
            audios.get(id).addFrame(audioFrame);
        }
    }

    private void channelStatusHandler(JSONObject obj) {

        var channelStatus = new ChannelStatus(obj);
        log.info(channelStatus.toString());
    }

    private void errorHandler(JSONObject obj) {

        var error = new Error(obj);
        log.error(error.toString());
        log.error("Channel got error command: {}", error.getCode());
    }

    private void locationMessageHandler(JSONObject obj, LocalDateTime timestamp) {

        var location = new Location(obj, timestamp);
        dbManager.saveMessage(location);
        log.info(location.toString());
    }

    private void textMessageHandler(JSONObject obj, LocalDateTime timestamp) {

        var text = new Text(obj, timestamp);
        log.info("Channel received text: {}", text.getTxt());
        dbManager.saveMessage(text);
    }

    private void imageMessageHandler(JSONObject obj, LocalDateTime timestamp) {

        var image = new Image(obj, timestamp);
        images.put(image.getId(), image);
        log.info(image.toString());
    }

    private void streamStartHandler(JSONObject obj, LocalDateTime timestamp) {

        var stream = new Audio(obj, timestamp);
        audios.put(obj.getInt("stream_id"), stream);
    }

    private void streamStopHandler(JSONObject obj) {

        var audio = audios.remove(obj.getInt("stream_id"));

        dbManager.saveMessage(audio);
    }
}
