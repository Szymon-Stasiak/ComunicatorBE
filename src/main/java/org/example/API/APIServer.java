package org.example.API;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.example.loger.Logger;


public class APIServer extends AbstractVerticle {



    @Override
    public void start() {
        Router router = Router.router(vertx);
        Logger.info(getClass() + " started...");
        
        router.route().handler(BodyHandler.create());
        router.post("/api/register").handler(this::registerUser);
        router.post("/api/login").handler(this::loginUser);

        vertx.createHttpServer().requestHandler(router).listen(8080, result -> {
            if (result.succeeded()) {
                Logger.info("Api Server is now listening on port 8080");
            } else {
                Logger.error("Failed to bind Api Server on port 8080");
            }
        });

    }

    private void loginUser(RoutingContext routingContext) {
        Logger.info("Login request");

        JsonObject obj = routingContext.body().asJsonObject();

        if (obj.getString("email") == null || obj.getString("password") == null) {
            routingContext.response().setStatusCode(400).end("Bad request");
        }
        vertx.eventBus().request("findByEmail", obj.getString("email")).onComplete(res -> {
            if (res.succeeded()) {
                JsonObject user = (JsonObject) res.result().body();
                if (user == null) {
                    routingContext.response().setStatusCode(404).end("User not found");
                } else if (user.getString("password").equals(obj.getString("password"))) {
                    routingContext.response().setStatusCode(200).end("User logged in");
                } else {
                    routingContext.response().setStatusCode(401).end("Unauthorized");
                }
            } else {
                routingContext.response().setStatusCode(500).end("Internal server error");
            }
        });
    }

    private void registerUser(RoutingContext routingContext) {
        Logger.info("Register request");

        JsonObject obj = routingContext.body().asJsonObject();

        if (obj.getString("name") == null || obj.getString("surname") == null || obj.getString("email") == null || obj.getString("password") == null) {
            routingContext.response().setStatusCode(400).end("Bad request");
        }
        vertx.eventBus().request("saveUser", obj).onComplete(res -> {
            if (res.succeeded()) {
                routingContext.response().setStatusCode(200).end("User created");
            } else if (res.cause().getMessage().contains("already exists")) {
                routingContext.response().setStatusCode(400).end("User already exists");
            } else {
                routingContext.response().setStatusCode(500).end("Internal server error");
            }
        });
    }


}

