package com.example.hirecore.stages;

import com.example.hirecore.notifications.IObservable;
import com.example.hirecore.notifications.IObserver;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractStage implements IStage, IObservable {
    private final List<IObserver> observers = new ArrayList<>();
    private IStage nextStage;

    protected AbstractStage(IStage nextStage) {
        this.nextStage = nextStage;
    }

    @Override
    public IStage ahead() {
        if (nextStage == null) {
            throw new IllegalStateException("No next stage available.");
        }
        return nextStage;
    }

    @Override
    public void notifyObservers(String subject, String content) {
        for (IObserver observer : observers) {
            observer.notify(subject, content);
        }
    }

    @Override
    public void addObserver(IObserver observer) {
        boolean alreadyRegistered = observers.stream()
                .anyMatch(existing -> existing.getId().equals(observer.getId()));
        if (!alreadyRegistered) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(IObserver observer) {
        observers.removeIf(existing -> existing.getId().equals(observer.getId()));
    }

    public IStage getNextStage() {
        return nextStage;
    }

    public void setNextStage(IStage nextStage) {
        this.nextStage = nextStage;
    }
}
