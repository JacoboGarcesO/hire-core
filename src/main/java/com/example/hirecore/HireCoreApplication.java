package com.example.hirecore;

import com.example.hirecore.factory.HiringPipeline;
import com.example.hirecore.factory.StagesFactory;

import java.util.List;
import java.util.Optional;

public class HireCoreApplication {

    public static void main(String[] args) {
        HiringPipeline pipeline = StagesFactory.createHiringPipeline();
        List<Candidate> candidates = pipeline.getCandidates();

        candidates.forEach(candidate -> System.out.println(
                candidate.getName() + " fue asignado a la etapa " + candidate.getStage().getClass().getSimpleName()));

        // Demostración: si dos candidatos comparten etapa, avanzar a uno no debe notificar al otro.
        findCandidateSharingStageWithSomeoneElse(candidates).ifPresent(mover -> {
            System.out.println();
            System.out.println("--- " + mover.getName() + " avanza de etapa"
                    + " (otros candidatos en su misma etapa no deben imprimir nada) ---");
            pipeline.getCandidateManager().advance(mover, pipeline.getRecruiter());

            System.out.println();
            System.out.println("--- Historial de auditoría de " + mover.getName() + " ---");
            for (CandidateChangeRecord entry : mover.getHistory()) {
                System.out.println(entry.getChangedAt()
                        + " | etapa: " + entry.getCandidateCopy().getStage().getClass().getSimpleName()
                        + " | cambiado por: " + entry.getChangedBy().getClass().getSimpleName());
            }

            System.out.println();
            System.out.println("--- Deshaciendo el último cambio de etapa (Memento, vía CandidateManager) ---");
            System.out.println("Antes de deshacer: " + mover.getStage().getClass().getSimpleName());
            pipeline.getCandidateManager().undoLastStageChange(mover, pipeline.getRecruiter());
            System.out.println("Después de deshacer: " + mover.getStage().getClass().getSimpleName());

            System.out.println();
            System.out.println("--- Historial de auditoría de " + mover.getName() + " (con la regresión incluida) ---");
            for (CandidateChangeRecord entry : mover.getHistory()) {
                System.out.println(entry.getChangedAt()
                        + " | etapa: " + entry.getCandidateCopy().getStage().getClass().getSimpleName()
                        + " | cambiado por: " + entry.getChangedBy().getClass().getSimpleName());
            }
        });
    }

    private static Optional<Candidate> findCandidateSharingStageWithSomeoneElse(List<Candidate> candidates) {
        return candidates.stream()
                .filter(candidate -> candidates.stream()
                        .anyMatch(other -> other != candidate && other.getStage() == candidate.getStage()))
                .findFirst();
    }
}
