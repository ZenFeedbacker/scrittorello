package com.scritorrelo;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.scritorrelo.ogg.OggFile;
import com.scritorrelo.ogg.Stream;
import com.scritorrelo.zello.Channel;
import com.scritorrelo.zello.Command;
import com.scritorrelo.zello.message.Location;
import com.scritorrelo.zello.message.Text;
import com.scritorrelo.zello.message.audio.AudioFrame;
import com.scritorrelo.zello.message.audio.AudioStream;
import com.scritorrelo.zello.message.error.Error;
import com.scritorrelo.zello.message.image.Image;
import com.scritorrelo.zello.message.image.ImagePacket;
import lombok.Setter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;

public class ZelloWebSocketAdapter extends WebSocketAdapter {

    @Setter
    public ZelloWebSocket ws;

    final HashMap<Integer, AudioStream> streams = new HashMap<>();

    public void onTextMessage(WebSocket websocket, String message) throws JSONException, IOException {

        LocalDateTime timestamp = LocalDateTime.now();
        JSONObject obj = new JSONObject(message);
        System.out.println(ws.toString() + ": " + message);

        if (obj.has("refresh_token")) {
            refreshTokenHandler(obj, timestamp);
            return;
        }

        if (obj.has("command")) {
            String cmnd = obj.getString("command");

            Command command = Command.valueOf(cmnd);

            switch (command) {
                case on_stream_start:
                    streamStartHandler(obj, timestamp);
                    break;
                case on_stream_stop:
                    streamStopHandler(obj, timestamp);
                    break;
                case on_text_message:
                    textMessageHandler(obj, timestamp);
                    break;
                case on_image:
                    imageMessageHandler(obj, timestamp);
                    break;
                case on_location:
                    locationMessageHandler(obj, timestamp);
                    break;
                case on_channel_status:
                    channelStatusHandler(obj, timestamp);
                    break;
                case on_error:
                    errorHandler(obj, timestamp);
                    break;
                default:
                    break;
            }
        }
    }

    private void refreshTokenHandler(JSONObject obj, LocalDateTime timestamp) throws JSONException {

        ws.setRefreshToken(obj.getString("refresh_token"));
    }

    public void onBinaryMessage(WebSocket websocket, byte[] binary) {

        switch (binary[0]) {
            case ((byte) 1):
                audioBinaryHandler(binary);
                break;
            case ((byte) 2):
                imageBinaryHandler(binary);
                break;
            default:
                System.out.println("Received unknown binary, type " + binary[0]);
                break;
        }
    }

    public void onBinaryFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        System.out.println("binary frame");
    }
    private void imageBinaryHandler(byte[] binary) {
        System.out.println("Received image binary");
        ImagePacket image = new ImagePacket(binary);
        System.out.println(image.isThumbnail() ? "Thumbnail" : "Full Image");
    }

    private void audioBinaryHandler(byte[] binary) {
        System.out.println("Received audio binary");
        AudioFrame audioFrame = new AudioFrame(binary);
        streams.get(audioFrame.getStream_id()).addFrame(audioFrame);
    }

    public void channelStatusHandler(JSONObject obj, LocalDateTime timestamp) throws JSONException {

        Channel channel = new Channel(obj, timestamp);
        System.out.println(channel);
    }

    public void errorHandler(JSONObject obj, LocalDateTime timestamp) throws JSONException {
        Error error = new Error(obj, timestamp);
        System.out.println(error);
    }

    public void locationMessageHandler(JSONObject obj, LocalDateTime timestamp) throws JSONException {
        Location location = new Location(obj, timestamp);
        System.out.println(location);
    }

    public void textMessageHandler(JSONObject obj, LocalDateTime timestamp) throws JSONException {
        Text text = new Text(obj, timestamp);
        //Database.addMessage(text);
        //System.out.println(text);
    }

    public void imageMessageHandler(JSONObject obj, LocalDateTime timestamp) throws JSONException {
        Image image = new Image(obj, timestamp);
        //System.out.println(image);
    }

    public void streamStartHandler(JSONObject obj, LocalDateTime timestamp) throws JSONException {
        AudioStream stream = new AudioStream(obj, timestamp);
        streams.put(obj.getInt("stream_id"), stream);
        //System.out.println(stream);
    }

    public void streamStopHandler(JSONObject obj, LocalDateTime timestamp) throws JSONException, IOException {
        AudioStream audioStream = streams.get(obj.getInt("stream_id"));
        //  audioStream.toFile();
        //  System.out.println(audioStream);
        Stream oggStream = new Stream(audioStream.getOpusStream());
        OggFile oggFile = new OggFile(oggStream);
        oggFile.writeToFile(Client.outputFile);
        System.out.println("Wrote file " + Client.outputFile);
        //Client.ws.disconnect();
    }
}
