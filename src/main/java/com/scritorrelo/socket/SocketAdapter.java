package com.scritorrelo.socket;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.scritorrelo.DatabaseManager;
import com.scritorrelo.zello.ChannelStatus;
import com.scritorrelo.zello.Command;
import com.scritorrelo.zello.message.Location;
import com.scritorrelo.zello.message.Text;
import com.scritorrelo.zello.message.audio.AudioFrame;
import com.scritorrelo.zello.message.audio.Audio;
import com.scritorrelo.zello.error.Error;
import com.scritorrelo.zello.message.image.Image;
import com.scritorrelo.zello.message.image.ImagePacket;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.HashMap;

import static java.util.Objects.isNull;

@Slf4j
@Controller
@Scope("prototype")
public class SocketAdapter extends WebSocketAdapter {

    @Setter
    private Socket ws;

    @Autowired
    private DatabaseManager dbManager;

    private final HashMap<Integer, Audio> audios = new HashMap<>();
    private final HashMap<Integer, Image> images = new HashMap<>();

    @Override
    public void onTextMessage(WebSocket websocket, String message) {

        var timestamp = LocalDateTime.now();
        var obj = new JSONObject(message);

        if (obj.has("refresh_token")) {
            refreshTokenHandler(obj);
            return;
        }

        if (obj.has("error")) {
            errorMessageHandler(obj);
            return;
        }

        if (obj.has("command")) {
            var cmd = obj.getString("command");

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

    @Override
    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
        super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
        log.error("Channel {} was disconnected" + (closedByServer ? " by server" : "") + ", current status is {}", ws.getChannelName(), ws.getState().toString());
        log.error("Server close frame: {}", serverCloseFrame.getPayloadText());
        log.error("Client close frame: {}", clientCloseFrame.getPayloadText());
        ws.recreate();
    }

    private void errorMessageHandler(JSONObject obj) {

        log.error("Channel {} got error message: {} ", ws.getChannelName(), obj.getString("error"));
    }

    private void refreshTokenHandler(JSONObject obj) {

        ws.setRefreshToken(obj.optString("refresh_token"));
    }

    private void imageBinaryHandler(byte[] binary) {

        var packet = new ImagePacket(binary);
        log.info("Channel {} received" + (packet.isThumbnail() ? "Thumbnail" : "Full Image") + "image binary", ws.getChannelName());

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

        log.trace("Channel {} received audio binary for stream {}", ws.getChannelName(), id);

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
        log.error("Channel {} got error command: {}", ws.getChannelName(), error.getCode());
    }

    private void locationMessageHandler(JSONObject obj, LocalDateTime timestamp) {

        var location = new Location(obj, timestamp);
        dbManager.saveMessage(location);
        log.info(location.toString());
    }

    private void textMessageHandler(JSONObject obj, LocalDateTime timestamp) {

        var text = new Text(obj, timestamp);
        log.info("Channel {} received text: {}", ws.getChannelName(), text.getTxt());
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
