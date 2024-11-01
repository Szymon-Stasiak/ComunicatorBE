package org.example.webSocketConection;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import org.example.loger.Logger;
import io.vertx.core.http.ServerWebSocket;



import java.util.ArrayList;
import java.util.List;

public class WebSocketDistributor extends AbstractVerticle {

    private final int port = 9000;
    private final List<String> workerIDs = new ArrayList<>();
    private int workerIndex = 0;



    @Override
    public  void start() {
        //for future use
//        List<WebSocketWorkerVerticle.WebSocketClient> connectedClients = new ArrayList<>();
//
//        vertx.createHttpServer().webSocketHandler(webSocket -> {
//            WebSocketWorkerVerticle.WebSocketClient client = new WebSocketWorkerVerticle.WebSocketClient(webSocket);
//            connectedClients.add(client);
//            String clientAddress = webSocket.remoteAddress().toString();
//            System.out.println("New WebSocket connection from " + clientAddress + " with id " + webSocket.textHandlerID());
//
//            webSocket.textMessageHandler(message -> {
//                System.out.println("Received message: " + message);
//                webSocket.writeTextMessage("Received your message: " + message);
//                connectedClients.forEach(connectedClient -> {
//                    if (!connectedClient.id.equals(client.id)) {
//                        connectedClient.webSocket.writeTextMessage("Client " + client.id + " says: " + message);
//                    }
//                });
//            });
//            webSocket.closeHandler(close -> {
//                System.out.println("WebSocket connection closed for client " + webSocket.textHandlerID());
//                connectedClients.remove(client);
//            });
//        }).listen(port, result -> {
//            if (result.succeeded()) {
//                System.out.println("Server is now listening on port " + port);
//            } else {
//                System.out.println("Failed to bind on port " + port);
//            }
//        });
    }
}
