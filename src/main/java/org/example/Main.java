package org.example;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import org.example.API.APIServer;
import org.example.loger.Logger;
import org.example.webSocketConection.WebSocketDistributor;
import org.example.DataBase.DataBaseVerticle;

public class Main {

    public static void main(String[] args) {

        Logger.info("App starts...");
        WebSocketDistributor webSocketDistributor = new WebSocketDistributor();
        webSocketDistributor.start();
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new DataBaseVerticle(vertx,"TESTDB", "mongodb://stszymek:ADMIN@chatapp-shard-00-00.j3wy9.mongodb.net:27017,chatapp-shard-00-01.j3wy9.mongodb.net:27017,chatapp-shard-00-02.j3wy9.mongodb.net:27017/?ssl=true&replicaSet=atlas-mwj2tr-shard-0&authSource=admin&retryWrites=true&w=majority&appName=ChatApp"));
        vertx.deployVerticle(new APIServer());
    }
}