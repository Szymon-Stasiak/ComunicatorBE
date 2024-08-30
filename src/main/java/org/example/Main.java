package org.example;

import org.example.loger.Logger;
import org.example.webSocketConection.WebSocketDistributor;

public class Main {

    public static void main(String[] args) {

        Logger.info("App starts...");
        WebSocketDistributor webSocketDistributor = new WebSocketDistributor();
        webSocketDistributor.start();
    }


}