package org.example.webSocketConection;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketClient;
import org.example.loger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WebSocketWorkerVerticle extends AbstractVerticle {

    private final String workerId;
    private final int port;
    private final Vertx vertx;



    public WebSocketWorkerVerticle(String workerId , int port , Vertx vertx) {
        this.workerId = workerId;
        this.port = port;
        this.vertx = vertx;
    }

    @Override
    public  void start() {
        List<WebSocketClient> connectedClients = new ArrayList<>();

        vertx.createHttpServer().webSocketHandler(webSocket -> {
            WebSocketClient client = new WebSocketClient(webSocket);
            connectedClients.add(client);
            String clientAddress = webSocket.remoteAddress().toString();
            System.out.println("New WebSocket connection from " + clientAddress + " with id " + webSocket.textHandlerID());

            webSocket.textMessageHandler(message -> {
                System.out.println("Received message: " + message);
                webSocket.writeTextMessage("Received your message: " + message);
                connectedClients.forEach(connectedClient -> {
                    if (!connectedClient.id.equals(client.id)) {
                        connectedClient.webSocket.writeTextMessage("Client " + client.id + " says: " + message);
                    }
                });
            });
            webSocket.closeHandler(close -> {
                System.out.println("WebSocket connection closed for client " + webSocket.textHandlerID());
                connectedClients.remove(client);
            });
        }).listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("Server is now listening on port " + port);
            } else {
                System.out.println("Failed to bind on port " + port);
            }
        });
    }
}


