package org.example.user;

import io.vertx.core.json.JsonObject;

import java.security.Timestamp;
import java.util.Objects;

public class User {
    private String id;
    private String name;
    private String surname;
    private String email;
    private String password;
    private Timestamp createdAt;
    private Timestamp lastLogin;

    public User(String id, String name, String surname, String email, String password, Timestamp createdAt, Timestamp lastLogin) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
    }

    public User(JsonObject json) {
        this.id = json.getString("id");
        this.name = json.getString("name");
        this.surname = json.getString("surname");
        this.email = json.getString("email");
        this.password = json.getString("password");
        //TO DO: add createdAt and lastLogin
    }


    public String getID() {
        return id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }


    public JsonObject toJson() {
        return new JsonObject()
                .put("id", id)
                .put("name", name)
                .put("surname", surname)
                .put("email", email)
                .put("password", password)
                .put("createdAt", createdAt)
                .put("lastLogin", lastLogin);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(name, user.name) && Objects.equals(surname, user.surname) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, email);
    }
}
