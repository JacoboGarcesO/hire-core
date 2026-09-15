package com.example.hirecore.supervisors;

import com.example.hirecore.notifications.IObserver;

import java.util.UUID;

public abstract class Supervisor implements IObserver {
    private final String id = UUID.randomUUID().toString();
    private String name;
    private String email;
    private String password;

    /** Cómo se identifica este supervisor en el correo simulado, p. ej. "Reclutador". */
    @Override
    public abstract String getRole();

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
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
