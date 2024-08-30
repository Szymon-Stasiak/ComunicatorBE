package org.example.DataBase;

import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.core.json.JsonObject;
import org.example.loger.Logger;

public class DataBaseVerticle extends AbstractVerticle {

    private final Vertx vertx;
    private final String DBName;
    private final MongoClient mongoClient;


    public DataBaseVerticle(Vertx vertx, String DBName, String mongoDBUri) {
        this.vertx = vertx;
        this.DBName = DBName;
        JsonObject config = new JsonObject().put("connection_string", mongoDBUri).put("db_name", "chatapp");
        mongoClient = MongoClient.createShared(vertx, config);
    }


    @Override
    public void start() {
        Logger.info(getClass() + " started...");

        EventBus eventBus = vertx.eventBus();
        Logger.info("Event bus consumer created for " + DBName + " database");

        eventBus.consumer("saveUser", message -> {
            JsonObject obj = (JsonObject) message.body();
            save(obj).onComplete(res -> {
                if (res.succeeded()) {
                    message.reply(res.result());
                } else {
                    message.fail(500, res.cause().getMessage());
                }
            });
        });

        eventBus.consumer("deleteUser", message -> {
            String id = (String) message.body();
            delete(id).onComplete(res -> {
                if (res.succeeded()) {
                    message.reply(res.result());
                } else {
                    message.fail(500, res.cause().getMessage());
                }
            });
        });

        eventBus.consumer("findUser", message -> {
            String id = (String) message.body();
            find(id).onComplete(res -> {
                if (res.succeeded()) {
                    message.reply(res.result());
                } else {
                    message.fail(500, res.cause().getMessage());
                }
            });
        });

        eventBus.consumer("updateUser", message -> {
            JsonObject obj = (JsonObject) message.body();
            String id = obj.getString("_id");
            update(id, obj).onComplete(res -> {
                if (res.succeeded()) {
                    message.reply(res.result());
                } else {
                    message.fail(500, res.cause().getMessage());
                }
            });
        });


    }

    private Future<String> save(JsonObject obj) {
        Promise<String> promise = Promise.promise();
        mongoClient.save(DBName, obj, res -> {
            if (res.succeeded()) {
                String id = res.result();
                Logger.info("User saved with id: " + id);
                promise.complete(id);
            } else {
                Logger.error("Failed to save user", res.cause());
                promise.fail(res.cause());
            }
        });
        return promise.future();
    }

    private Future<JsonObject> delete(String id) {
        Promise<JsonObject> promise = Promise.promise();
        mongoClient.removeDocument(DBName, new JsonObject().put("_id", id), res -> {
            if (res.succeeded()) {
                Logger.info("User deleted with id: " + id);
                promise.complete();
            } else {
                Logger.error("Failed to delete user", res.cause());
                promise.fail(res.cause());
            }
        });
        return promise.future();
    }

    private Future<JsonObject> find(String id) {
        Promise<JsonObject> promise = Promise.promise();
        mongoClient.findOne(DBName, new JsonObject().put("_id", id), new JsonObject(), res -> {
            if (res.succeeded()) {
                JsonObject user = res.result();
                Logger.info("User found with id: " + id);
                promise.complete(user);
            } else {
                Logger.error("Failed to find user", res.cause());
                promise.fail(res.cause());
            }
        });
        return promise.future();
    }

    private Future<JsonObject> update(String id, JsonObject obj) {
        Promise<JsonObject> promise = Promise.promise();
        mongoClient.updateCollection(DBName, new JsonObject().put("_id", id), new JsonObject().put("$set", obj), res -> {
            if (res.succeeded()) {
                Logger.info("User updated with id: " + id);
                promise.complete(obj);
            } else {
                Logger.error("Failed to update user", res.cause());
                promise.fail(res.cause());
            }
        });
        return promise.future();
    }
}
