package com.example.hirecore;

import com.example.hirecore.supervisors.Supervisor;

import java.time.LocalDateTime;

/**
 * Una entrada del historial de auditoría de un candidato: una copia del
 * candidato (misma clase {@link Candidate}) en ese momento, quién hizo el
 * cambio y a qué hora.
 */
public class CandidateChangeRecord {
    private final Candidate candidateCopy;
    private final Supervisor changedBy;
    private final LocalDateTime changedAt;

    public CandidateChangeRecord(Candidate candidateCopy, Supervisor changedBy, LocalDateTime changedAt) {
        this.candidateCopy = candidateCopy;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
    }

    public Candidate getCandidateCopy() {
        return candidateCopy;
    }

    public Supervisor getChangedBy() {
        return changedBy;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }
}
