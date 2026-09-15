package com.example.hirecore.notifications;

public interface IObserver {
    /** Identidad estable del observador: la usan addObserver/removeObserver para no duplicar ni comparar por referencia. */
    String getId();

    void notify(String message);
}
