package com.scritorrelo;

import com.scritorrelo.db.DatabaseManager;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.HashMap;

import static java.util.Objects.isNull;

@Slf4j
@Service
public class SocketHandler extends AbstractWebSocketHandler  {

    private static final String KEY_COMMAND = "command";

    @Value("${:classpath:auth_token}")
    private Resource authTokenFile;

    @Value("${scrittorello.timeout}")
    private int timeout;

    @Value("${scrittorello.channelName}")
    private String channelName;

    @Value("${scrittorello.username}")
    private String username;

    @Value("${scrittorello.password}")
    private String password;

    @Value("${scrittorello.userAccount}")
    private String userAccount;

    @Autowired
    private DatabaseManager dbManager;

    private WebSocketSession wsSession;

    WebSocketConnectionManager wsConnManager;

    private final HashMap<Integer, Audio> audios = new HashMap<>();
    private final HashMap<Integer, Image> images = new HashMap<>();

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        wsSession = session;

        login();
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {

        log.info(message.getPayload());
        super.handleTextMessage(session, message);

        var timestamp = LocalDateTime.now();
        var obj = new JSONObject(message.getPayload());

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

    private void sendMessage(String message) {

        if (wsSession != null && message != null) {
            try {
                log.debug("Send message: " + message);
                wsSession.sendMessage(new TextMessage(message));
            } catch (Throwable e) {
                log.error("Send message failed: ", e);
                closeWsSession(wsSession);
            }
        }
    }

    private void login() {

        var loginJson = new JSONObject()
                .put(KEY_COMMAND, "logon")
                .put("seq", 0)
                .put("auth_token", getAuthToken())
                .put("channel", channelName)
                .put("listen_only", "true");

        if ("true".equals(userAccount)) {
            loginJson.put("username", username);
            loginJson.put("password", password);
        }

        log.info("Send message: {}", loginJson);

        sendMessage(loginJson.toString());
    }

    private void closeWsSession(WebSocketSession session) {

        try {
            session.close();
        } catch (Throwable t) {
            log.warn("Error stopping webSocket session");
        }

        if (wsSession == session) {
            clearWsSession();
        }
    }

    private void clearWsSession() {

        if (wsConnManager != null) {
            try {
                wsConnManager.stop();
            } catch (Throwable t) {
                log.warn("Problem stopping connection manager");
            }

            wsConnManager = null;
        }
        wsSession = null;
    }

    private String getAuthToken() {

        if (authTokenFile == null) {
            log.warn("AuthToken field is null");
            return "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJXa002ZW1WdVptVmxaRG96Lk8zY1B3c2NScnZrXC9ob2pOQnhEQlRlMjRibGZOM3FremFRazdkRFBlQnRBPSIsImV4cCI6MTYzMjA1ODYzMSwiYXpwIjoiZGV2In0=.KkSNnmGTjK2nEGIUzKJm+7IpWallpkdc6k8jG/UG+uR4ONvv4Jezab2q066IBuUNzHLtuxrk7lA5EP/Kgrv80/ZsW1TBdyevWx6WYTIF666yOUsUr8iLkrRL+2+lcPx1j8G0OPp5vEGKqm8YJfTm0kqHu07BuOlgK2yRudB+IFFSJRGTEKT1b3lHBTyKO+fExPIMy7B7Y9GsyU7OtlXohb0FZS632UncdE0RvZHTaT25PXy1HspbVnBqvA9oSBaUC52Q/eqd2DY27TBRme8eAbN5/L/GTzwAdTpOqL8WSdAGIelxe7bj4x4GE++WWK1h8E5+5zT9+hpxRf7QifNklg==";
        }

        try {
            return StreamUtils.copyToString(authTokenFile.getInputStream(), Charset.defaultCharset());
        } catch (IOException e) {
            log.warn("IOException while opening AuthToken file : {}", e.getMessage());
            return null;
        }
    }

    private void errorMessageHandler(JSONObject obj) {

        log.error("Got error message: {} ", obj.getString("error"));
    }

    private void imageBinaryHandler(byte[] binary) {

        var packet = new ImagePacket(binary);
        log.info("Channel received" + (packet.isThumbnail() ? "Thumbnail" : "Full Image") + "image binary");

        var id = packet.getId();

        if (images.containsKey(id)) {

            var image = images.get(id);

            if (packet.isThumbnail()) {
                image.setThumbnail(packet);
            } else {
                image.setFullsize(packet);
            }

            if (image.isComplete()) {
                image.saveFiles();
                dbManager.saveMessage(image);
                images.remove(image.getId());
            }
        }
    }

    private void audioBinaryHandler(byte[] binary) {

        var audioFrame = new AudioFrame(binary);
        var id = audioFrame.getStreamId();

        log.info("Channel received audio binary for stream {}", id);

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

        audio.write();

        dbManager.saveMessage(audio);
    }
}
