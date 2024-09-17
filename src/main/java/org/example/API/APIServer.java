package org.example.API;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.Credentials;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;
import io.vertx.ext.auth.oauth2.OAuth2Options;
import io.vertx.ext.auth.oauth2.Oauth2Credentials;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.example.loger.Logger;


public class APIServer extends AbstractVerticle {

    OAuth2Auth googleAuth = OAuth2Auth.create(vertx,
            new OAuth2Options()
                    .setFlow(OAuth2FlowType.AUTH_CODE)
                    .setClientID("your-client-id")
    );

    OAuth2Auth facabookAuth = OAuth2Auth.create(vertx,
            new OAuth2Options()
                    .setFlow(OAuth2FlowType.AUTH_CODE)
                    .setClientID("your-client-id")
    );


    @Override
    public void start() {
        Router router = Router.router(vertx);
        Logger.info(getClass() + " started...");
        
        router.route().handler(BodyHandler.create());
        router.post("/api/register").handler(this::registerUser);
        router.post("/api/login").handler(this::loginUser);
        router.post("/callback/google").handler(this::googleAuth);
        router.post("/callback/facebook").handler(this::facebookAuth);

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

    private void googleAuth(RoutingContext routingContext){
        String code = routingContext.request().getParam("code");

        Oauth2Credentials credentials = new Oauth2Credentials().setCode(code);

        googleAuth.authenticate(credentials, res -> {
            if (res.succeeded()) {
                Logger.info("User authenticated with google");

                //TODO decide what should be returned and how identify user

                JsonObject user = (JsonObject) res.result();
                routingContext.response().setStatusCode(200).end(user.encode());
            } else {
                Logger.error("Failed to authenticate user with google", res.cause());
                routingContext.response().setStatusCode(500).end("Internal server error");
            }

        });
    };

    private void facebookAuth(RoutingContext routingContext){
        String code = routingContext.request().getParam("code");

        Oauth2Credentials credentials = new Oauth2Credentials().setCode(code);

        facabookAuth.authenticate(credentials, res -> {
            if (res.succeeded()) {
                Logger.info("User authenticated with facebook");

                //TODO decide what should be returned and how identify user

                JsonObject user = (JsonObject) res.result();
                routingContext.response().setStatusCode(200).end(user.encode());
            } else {
                Logger.error("Failed to authenticate user with facebook", res.cause());
                routingContext.response().setStatusCode(500).end("Internal server error");
            }

        });
    }

}

