package org.example.DataBase;

import io.vertx.core.Vertx;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

public class DataBaseVerticle extends AbstractVerticle {

    private final Vertx vertx;

    public DataBaseVerticle(Vertx vertx) {
        this.vertx = vertx;
    }


    @Override
    public void start() {
        String mongoDBUri = "mongodb+srv://stszymek:ADMIN@chatapp.j3wy9.mongodb.net/?retryWrites=true&w=majority&appName=ChatApp";
        JsonObject config = new JsonObject().put("connection_string", mongoDBUri).put("db_name", "chatapp");

        MongoClient mongoClient = MongoClient.createShared(vertx, config);
    }
}
