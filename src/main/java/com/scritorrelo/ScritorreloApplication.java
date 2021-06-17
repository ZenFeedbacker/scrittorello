package com.scritorrelo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URI;
import java.net.URISyntaxException;

@SpringBootApplication
public class ScritorreloApplication {

    static String auth_token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJXa002ZW1WdVptVmxaRG94LjNYWXdFaDZoeUlNMk9xR2lBcDB0RjFQWXZIblVJZVBCdWhrNWFpYnZrOGs9IiwiZXhwIjoxNjIwNzIxMjgwLCJhenAiOiJkZXYifQ==.rkiIoMd+fjuYNsj9EuAt5Xd6AedVnXApSgF0lYAITOpZwT9YaLg19ODm96IH1hAi8A4p8RE/iTYessPnWxcX06F4Nkh6W7svUrLVD1dfJXRPMynPYUzUpFdVpbDv7ILlJiym8/fpJtSGRpb/TTFLrt2pjaz2lrs6cXkfWfoJRQ1FQn8Ko726eDBRY+hrhtXQFaff+EZWZc9maBV6lAWDg3IdfZhQjcY9443TQdg2SWI25bm0CIjMU/nF9xHF7Bg5XcuQz3r98AXFTUbB+XIjHjlQqM2lEfVy5OlyL4rODsms9qPBUzmAV6LfhwBSBd4TuuKCvxNOQYAFzls7HwXMFg==";


    public static void main(String[] args) throws URISyntaxException {
        SpringApplication.run(ScritorreloApplication.class, args);
        try {
            // open websocket
            final WebsocketClientEndpoint clientEndPoint = new WebsocketClientEndpoint(new URI("wss://zello.io/ws"));

            // add listener
            clientEndPoint.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
                public void handleMessage(String message) {
                    System.out.println(message);
                }
            });

            // send message to websocket
            clientEndPoint.sendMessage("{'command':'logon','seq':1,'auth_token':" +auth_token +", 'channel':'echo'}");

            // wait 5 seconds for messages from websocket
            Thread.sleep(5000);

        } catch (InterruptedException ex) {
            System.err.println("InterruptedException exception: " + ex.getMessage());
        } catch (URISyntaxException ex) {
            System.err.println("URISyntaxException exception: " + ex.getMessage());
        }
    }

}
