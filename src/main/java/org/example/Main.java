package org.example;

import io.vertx.core.Vertx;
import org.example.webSocketConection.ActionServiceVerticle;

public class Main {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        ActionServiceVerticle actionServiceVerticle = new ActionServiceVerticle();
        actionServiceVerticle.start();
    }


}