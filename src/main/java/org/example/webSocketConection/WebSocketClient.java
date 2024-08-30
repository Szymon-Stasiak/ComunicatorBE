package org.example.webSocketConection;

import io.vertx.core.http.ServerWebSocket;

import java.util.UUID;


public class WebSocketClient {


    final UUID id;

    WebSocketClient(ServerWebSocket webSocket) {
        this.id = UUID.randomUUID();
    }
}

