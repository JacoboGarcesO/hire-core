package com.example.hirecore;

import com.example.hirecore.memento.StageMementoCaretaker;
import com.example.hirecore.notifications.IObserver;
import com.example.hirecore.stages.IStage;
import com.example.hirecore.supervisors.Supervisor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Candidate implements IObserver {
    private final String id = UUID.randomUUID().toString();
    private String name;
    private String email;
    private IStage stage;

    private final List<CandidateChangeRecord> history = new ArrayList<>();
    private final StageMementoCaretaker stageMementoCaretaker = new StageMementoCaretaker();

    public Candidate(String name, String email, IStage stage) {
        this.name = name;
        this.email = email;
        this.stage = stage;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getRole() {
        return "Candidato";
    }

    public void recordChange(Supervisor changedBy) {
        Candidate copy = new Candidate(name, email, stage);
        history.add(new CandidateChangeRecord(copy, changedBy, LocalDateTime.now()));
    }

    public List<CandidateChangeRecord> getHistory() {
        return List.copyOf(history);
    }

    public boolean undoStageChange() {
        return stageMementoCaretaker.undo()
                .map(previousStage -> {
                    this.stage = previousStage;
                    return true;
                })
                .orElse(false);
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public IStage getStage() {
        return stage;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setStage(IStage stage) {
        stageMementoCaretaker.save(this.stage);
        this.stage = stage;
    }
}
