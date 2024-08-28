package org.example;

import org.example.webSocketConection.ActionServiceVerticle;

public class Main {

    public static void main(String[] args) {
        ActionServiceVerticle actionServiceVerticle = new ActionServiceVerticle();
        actionServiceVerticle.start();
    }


}