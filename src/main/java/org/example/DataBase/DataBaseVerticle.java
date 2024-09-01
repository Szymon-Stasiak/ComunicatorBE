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
                } else if (res.failed()) {
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

        eventBus.consumer("findByEmail", message -> {
            String email = (String) message.body();
            findUserByEmail(email).onComplete(res -> {
                if (res.succeeded()) {
                    message.reply(res.result());
                } else {
                    message.reply(null);
                }
            });
        });


    }

    private Future<String> save(JsonObject obj) {
        Promise<String> promise = Promise.promise();
        findUserByEmail(obj.getString("email")).onComplete(res -> {
            if (res.succeeded()) {
                if (res.result() != null) {
                    Logger.debug("User with email: " + obj.getString("email") + " already exists");
                    promise.fail("User with email: " + obj.getString("email") + " already exists");
                } else {
                    mongoClient.insert(DBName, obj, res2 -> {
                        if (res2.succeeded()) {
                            Logger.debug("User saved with id: " + obj.getString("_id"));
                            promise.complete(obj.getString("_id"));
                        } else {
                            Logger.error("Failed to save user", res2.cause());
                            promise.fail(res2.cause());
                        }
                    });
                }
            } else {
                Logger.error("Failed to save user", res.cause());
                promise.fail(res.cause());
            }
        });
        return promise.future();
    }

    private Future<JsonObject> delete(String id) {
        Promise<JsonObject> promise = Promise.promise();
        mongoClient.findOneAndDelete(DBName, new JsonObject().put("_id", id), res -> {
            if (res.succeeded()) {
                if (res.result() == null) {
                    Logger.debug("User not found");
                    promise.fail("User not found");
                } else {
                    JsonObject user = res.result();
                    Logger.debug("User deleted with id: " + id);
                    promise.complete(user);
                }
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
                promise.complete(user);
            } else {
                promise.fail(res.cause());
            }
        });
        return promise.future();
    }

    private Future<JsonObject> findUserByEmail(String email) {
        Promise<JsonObject> promise = Promise.promise();
        Logger.debug("Searching for user with email: " + email);
           mongoClient.findOne(DBName, new JsonObject().put("email", email), new JsonObject(), res -> {
                if (res.succeeded()) {
                    JsonObject user = res.result();
                    promise.complete(user);
                } else {
                    promise.fail(res.cause());
                }
            });

        return promise.future();

    }

    private Future<JsonObject> update(String id, JsonObject obj) {
        Promise<JsonObject> promise = Promise.promise();
        if (find(id).result() == null) {
            promise.fail("User not found");
            return promise.future();
        }
        mongoClient.updateCollection(DBName, new JsonObject().put("_id", id), new JsonObject().put("$set", obj), res -> {
            if (res.succeeded()) {
                Logger.debug("User updated with id: " + id);
                promise.complete(obj);
            } else {
                Logger.error("Failed to update user", res.cause());
                promise.fail(res.cause());
            }
        });
        return promise.future();
    }
}
