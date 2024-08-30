package org.example;

import io.vertx.core.Vertx;
import org.example.webSocketConection.ActionServiceVerticle;
import org.example.loger.Logger;
import org.example.webSocketConection.WebSocketDistributor;

public class Main {

    public static void main(String[] args) {

        Logger.info("App starts...");
        WebSocketDistributor webSocketDistributor = new WebSocketDistributor();
        webSocketDistributor.start();
        Vertx vertx = Vertx.vertx();

        ActionServiceVerticle actionServiceVerticle = new ActionServiceVerticle();
        actionServiceVerticle.start();
    }


}