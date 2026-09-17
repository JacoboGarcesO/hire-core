package com.example.hirecore.manager;

import com.example.hirecore.Candidate;
import com.example.hirecore.notifications.IObservable;
import com.example.hirecore.stages.IStage;
import com.example.hirecore.supervisors.Supervisor;

public class CandidateManager {

    private final IStage rejectedStage;

    public CandidateManager(IStage rejectedStage) {
        this.rejectedStage = rejectedStage;
    }

    public void advance(Candidate candidate, Supervisor performedBy) {
        IStage previousStage = candidate.getStage();
        IStage nextStage = previousStage.ahead();

        String subject = "Cambio de etapa: " + candidate.getName();
        String content = candidate.getName() + " avanzó de " + previousStage.getClass().getSimpleName()
                + " a " + nextStage.getClass().getSimpleName();

        notifyTransition(candidate, previousStage, nextStage, subject, content);

        candidate.setStage(nextStage);
        candidate.recordChange(performedBy);
    }

    public void undoLastStageChange(Candidate candidate, Supervisor performedBy) {
        IStage stageBeforeUndo = candidate.getStage();
        if (!candidate.undoStageChange()) {
            return;
        }
        IStage stageAfterUndo = candidate.getStage();

        String subject = "Regresión de etapa: " + candidate.getName();
        String content = candidate.getName() + " deshizo su cambio de etapa: volvió de "
                + stageBeforeUndo.getClass().getSimpleName() + " a " + stageAfterUndo.getClass().getSimpleName();

        notifyTransition(candidate, stageBeforeUndo, stageAfterUndo, subject, content);
        candidate.recordChange(performedBy);
    }

    public void reject(Candidate candidate, Supervisor performedBy) {
        IStage previousStage = candidate.getStage();

        String subject = "Candidato rechazado: " + candidate.getName();
        String content = candidate.getName() + " fue rechazado (estaba en "
                + previousStage.getClass().getSimpleName() + ").";

        notifyTransition(candidate, previousStage, rejectedStage, subject, content);

        candidate.setStage(rejectedStage);
        candidate.recordChange(performedBy);
    }

    private void notifyTransition(Candidate candidate, IStage fromStage, IStage toStage, String subject, String content) {
        IObservable fromObservable = asObservable(fromStage);
        IObservable toObservable = asObservable(toStage);

        fromObservable.addObserver(candidate);
        toObservable.addObserver(candidate);

        fromObservable.notifyObservers(subject, "(" + fromStage.getClass().getSimpleName() + ") " + content);
        toObservable.notifyObservers(subject, "(" + toStage.getClass().getSimpleName() + ") " + content);

        fromObservable.removeObserver(candidate);
        toObservable.removeObserver(candidate);
    }



    private static IObservable asObservable(IStage stage) {
        if (!(stage instanceof IObservable observable)) {
            throw new IllegalStateException("La etapa no es observable: " + stage.getClass().getSimpleName());
        }
        return observable;
    }
}
