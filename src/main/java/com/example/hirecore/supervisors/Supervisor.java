package com.example.hirecore.supervisors;

import com.example.hirecore.notifications.IObserver;

import java.util.UUID;

public abstract class Supervisor implements IObserver {
    private final String id = UUID.randomUUID().toString();
    private String name;
    private String email;
    private String password;

    /** Etiqueta del rol, para que el mensaje deje claro quién lo recibió. */
    protected abstract String getRole();

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void notify(String message) {
        System.out.println("[" + getRole() + "] " + message);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
