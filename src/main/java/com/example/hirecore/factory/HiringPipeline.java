package com.example.hirecore.factory;

import com.example.hirecore.Candidate;
import com.example.hirecore.manager.CandidateManager;
import com.example.hirecore.stages.IStage;
import com.example.hirecore.supervisors.Accountant;
import com.example.hirecore.supervisors.Manager;
import com.example.hirecore.supervisors.Recruiter;

import java.util.List;

public class HiringPipeline {
    private final List<IStage> stages;
    private final List<Candidate> candidates;
    private final Recruiter recruiter;
    private final Manager manager;
    private final Accountant accountant;
    private final CandidateManager candidateManager;

    public HiringPipeline(List<IStage> stages, List<Candidate> candidates, Recruiter recruiter,
                          Manager manager, Accountant accountant, CandidateManager candidateManager) {
        this.stages = stages;
        this.candidates = candidates;
        this.recruiter = recruiter;
        this.manager = manager;
        this.accountant = accountant;
        this.candidateManager = candidateManager;
    }

    public List<IStage> getStages() {
        return stages;
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public Recruiter getRecruiter() {
        return recruiter;
    }

    public Manager getManager() {
        return manager;
    }

    public Accountant getAccountant() {
        return accountant;
    }

    public CandidateManager getCandidateManager() {
        return candidateManager;
    }
}
