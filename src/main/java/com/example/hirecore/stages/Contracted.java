package com.example.hirecore.stages;

import com.example.hirecore.notifications.IObservable;
import com.example.hirecore.notifications.IObserver;

import java.util.ArrayList;
import java.util.List;

public class Contracted implements IStage, IObservable {
    private List<IObserver> observers = new ArrayList<>();
    private IStage nextStage;

    public Contracted(IStage nextStage) {
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
    public void notifyObservers(String message) {
        for (IObserver observer : observers) {
            observer.notify(message);
        }
    }

    @Override
    public void addObserver(IObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(IObserver observer) {
        observers.remove(observer);
    }

    public IStage getNextStage() {
        return nextStage;
    }

    public void setNextStage(IStage nextStage) {
        this.nextStage = nextStage;
    }
}
