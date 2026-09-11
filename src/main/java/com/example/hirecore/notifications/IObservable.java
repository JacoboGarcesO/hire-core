package com.example.hirecore.notifications;

public interface IObservable {
    void notifyObservers(String message);
    void addObserver(IObserver observer);
    void removeObserver(IObserver observer);
}
