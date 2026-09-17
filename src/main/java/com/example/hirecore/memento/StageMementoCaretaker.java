package com.example.hirecore.memento;

import com.example.hirecore.stages.IStage;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

public class StageMementoCaretaker {
    private final Deque<StageMemento> mementos = new ArrayDeque<>();

    public void save(IStage stage) {
        mementos.push(new StageMemento(stage));
    }

    public Optional<IStage> undo() {
        if (mementos.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(mementos.pop().getStage());
    }

    public boolean hasHistory() {
        return !mementos.isEmpty();
    }
}
