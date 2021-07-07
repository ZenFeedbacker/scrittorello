package com.scritorrelo;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

import static com.scritorrelo.Client.*;

@Configuration
@ComponentScan
public class ApplicationContextConfiguration {

    public WebSocket webSocket() throws IOException, WebSocketException {

        return  new WebSocketFactory()
                .setConnectionTimeout(TIMEOUT)
                .createSocket(SERVER)
                .addListener(adapter)
                .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
                .connect();
    }
}
