package com.example.hirecore;

import com.example.hirecore.stages.IStage;

public class Candidate {
    private String name;
    private String email;
    private IStage stage;

    public Candidate(String name, String email, IStage stage) {
        this.name = name;
        this.email = email;
        this.stage = stage;
    }

    public void advanceStage() {
        stage = stage.ahead();
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
        this.stage = stage;
    }
}
