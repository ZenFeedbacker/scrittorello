package com.scritorrelo.zello.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
enum ErrorCode {

    UNKNOWN_COMMAND("unknown command", "Server didn't recognize the command received from the client."),
    INTERNAL_SERVER_ERROR("internal server error", "An internal error occurred within the server. If the error persists please contact us at support@zello.com"),
    INVALID_JSON("invalid json", "The command received included malformed JSON"),
    INVALID_REQUEST("invalid request", "The server couldn't recognize command format."),
    NOT_AUTHORIZED("not authorized", "Username, password or token are not valid."),
    NOT_LOGGED_ON("not logged in", "Server received a command before successful logon."),
    NOT_ENOUGH_PARAMS("not enough params", "The command doesn't include some of the required attributes."),
    SERVER_CLOSED_CONNECTION("server closed connection", "The connection to Zello network was closed. You can try re-connecting."),
    CHANNEL_IS_NOT_READY("channel is not ready", "Channel you are trying to talk to is not yet connected. Wait for channel online status before sending a message"),
    LISTEN_ONLY_CONNECTION("listen only connection", "The client tried to send a message over listen-only connection."),
    FAILED_TO_START_STREAM("failed to start stream", "Unable to start the stream for unknown reason. You can try again later."),
    FAILED_TO_STOP_STREAM("failed to stop stream", "Unable to stop the stream for unknown reason. This error is safe to ignore."),
    FAILED_TO_SEND_DATA("failed to send data", "An error occurred while trying to send stream data packet."),
    INVALID_AUDIO_PACKET("invalid audio packet", "Malformed audio packet is received.");

    private final String code;
    private final String description;

    public static ErrorCode valueOfCode(String code) {

        return Arrays.stream(ErrorCode.values()).filter(e -> e.code.equals(code)).findFirst().orElse(null);
    }
}
