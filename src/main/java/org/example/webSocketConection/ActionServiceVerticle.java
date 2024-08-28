package org.example.webSocketConection;

import io.vertx.core.Vertx;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.Router;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ActionServiceVerticle {

    private static class WebSocketClient {
        final UUID id;
        final ServerWebSocket webSocket;

        WebSocketClient(ServerWebSocket webSocket) {
            this.id = UUID.randomUUID();
            this.webSocket = webSocket;
        }
    }

    public static void start() {
        System.out.println("Hello, World!");
        Vertx vertx = Vertx.vertx();
        Router router = Router.router(vertx);
        List<WebSocketClient> connectedClients = new ArrayList<>();

        router.get("/chat").handler(routingContext -> {
            routingContext.request().toWebSocket().onSuccess(webSocket -> {
                WebSocketClient client = new WebSocketClient(webSocket);
                connectedClients.add(client);
                String clientAddress = webSocket.remoteAddress().toString();
                System.out.println("New WebSocket connection from " + clientAddress + " with id " + client.id);

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
                    System.out.println("WebSocket connection closed for client " + client.id);
                    connectedClients.remove(client);
                });
            }).onFailure(throwable -> {
                System.err.println("Failed to upgrade to WebSocket: " + throwable.getMessage());
            });
        });

        vertx.createHttpServer().requestHandler(router).listen(8080);
    }
}
