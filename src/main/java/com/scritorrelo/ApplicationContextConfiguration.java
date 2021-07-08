package com.scritorrelo;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.IOException;

import static com.scritorrelo.Client.*;

@Configuration
@ComponentScan({"com.scritorrelo"})
public class ApplicationContextConfiguration {

    @Bean
    @Scope("prototype")
    public WebSocket webSocket() throws IOException, WebSocketException {

        return new WebSocketFactory()
                .setConnectionTimeout(TIMEOUT)
                .createSocket(SERVER)
                .addListener(adapter())
                .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
                .connect();
    }

    @Bean
    public ZelloWebSocketAdapter adapter(){
        return new ZelloWebSocketAdapter();
    }
}
