package org.example.API;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.example.loger.Logger;

public class APIServer extends AbstractVerticle {



    @Override
    public void start() {
        Router router = Router.router(vertx);
        Logger.info(getClass() + " started...");

        EventBus eventBus = vertx.eventBus();

        router.post("api/register").handler(ctx -> {
            Logger.info("Register request");

            JsonObject obj = ctx.getBodyAsJson();
            if (obj.getString("name") == null || obj.getString("surname") == null || obj.getString("email") == null || obj.getString("password") == null) {
                ctx.response().setStatusCode(400).end("Bad request");
            }
            eventBus.request("findByEmail", obj.getString("email")).onComplete(res -> {
                if (res.succeeded()) {
                    if (res.result().body() == null) {
                        eventBus.request("saveUser", obj).onComplete(res2 -> {
                            if (res2.succeeded()) {
                                ctx.response().setStatusCode(200).end("User created");
                            } else {
                                ctx.response().setStatusCode(500).end("Internal server error");
                            }
                        });
                    } else {
                        ctx.response().setStatusCode(409).end("User with this email already exists");
                    }
                } else {
                    ctx.response().setStatusCode(500).end("Internal server error");
                }
            });
        });

        router.post("api/login").handler(ctx -> {
            Logger.info("Login request");
            JsonObject obj = ctx.getBodyAsJson();
            if (obj.getString("email") == null || obj.getString("password") == null) {
                ctx.response().setStatusCode(400).end("Bad request");
            }
            eventBus.request("findByEmail", obj.getString("email")).onComplete(res -> {
                if (res.succeeded()) {
                    if (res.result().body() == null) {
                        ctx.response().setStatusCode(404).end("User not found");
                    } else {
                        JsonObject user = (JsonObject) res.result().body();
                        if (user.getString("password").equals(obj.getString("password"))) {
                            ctx.response().setStatusCode(200).end("User logged in");
                        } else {
                            ctx.response().setStatusCode(401).end("Unauthorized");
                        }
                    }
                } else {
                    ctx.response().setStatusCode(500).end("Internal server error");
                }
            });

        });

        vertx.createHttpServer().requestHandler(router).listen(8080, result -> {
            if (result.succeeded()) {
                Logger.info("Server is now listening on port 8080");
            } else {
                Logger.error("Failed to bind on port 8080");
            }
        });

    }


}

