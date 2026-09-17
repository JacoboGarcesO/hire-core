package com.example.hirecore.memento;

import com.example.hirecore.stages.IStage;

public class StageMemento {
    private final IStage stage;

    StageMemento(IStage stage) {
        this.stage = stage;
    }

    IStage getStage() {
        return stage;
    }
}
