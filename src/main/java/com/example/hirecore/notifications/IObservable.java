package com.example.hirecore.notifications;

public interface IObservable {
    void notifyObservers(String subject, String content);
    void addObserver(IObserver observer);
    void removeObserver(IObserver observer);
}
