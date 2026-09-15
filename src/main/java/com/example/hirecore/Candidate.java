package com.example.hirecore;

import com.example.hirecore.memento.StageMementoCaretaker;
import com.example.hirecore.notifications.IObserver;
import com.example.hirecore.stages.IStage;
import com.example.hirecore.supervisors.Supervisor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * El candidato también es observador: cuando {@code CandidateManager} lo
 * suscribe temporalmente a una etapa para avisarle de un cambio, recibe el
 * mensaje a través de {@link #notify(String)}.
 *
 * <p>Además mantiene, de forma completamente independiente entre sí, dos
 * mecanismos distintos:
 * <ul>
 *   <li>Un <b>historial de auditoría</b> ({@link #recordChange}): quién hizo
 *       el cambio, cuándo, y una copia del candidato en ese momento. Es de
 *       solo lectura, pensado para consultar qué pasó.</li>
 *   <li>El <b>patrón Memento</b> sobre la etapa ({@link #undoStageChange}),
 *       delegado por completo a {@link StageMementoCaretaker} (paquete
 *       {@code com.example.hirecore.memento}): no tiene relación con el
 *       historial de auditoría, deshacer un cambio de etapa no borra ni
 *       modifica ninguna entrada del historial.</li>
 * </ul>
 */
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
    public void notify(String message) {
        System.out.println("[Candidato] " + message);
    }

    // ------------------------------------------------------------------
    // Historial de auditoría (independiente del memento de más abajo).
    // ------------------------------------------------------------------

    /** Deja constancia de un cambio: quién lo hizo y una copia del candidato en este momento. */
    public void recordChange(Supervisor changedBy) {
        Candidate copy = new Candidate(name, email, stage);
        history.add(new CandidateChangeRecord(copy, changedBy, LocalDateTime.now()));
    }

    /** Historial completo, de solo lectura. */
    public List<CandidateChangeRecord> getHistory() {
        return List.copyOf(history);
    }

    // ------------------------------------------------------------------
    // Patrón Memento: deshacer cambios de etapa (ver paquete memento).
    // ------------------------------------------------------------------

    /** Deshace el último cambio de etapa aplicado. {@code false} si no había nada que deshacer. */
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

    /** Cambia la etapa guardando primero un memento de la anterior, para poder deshacer. */
    public void setStage(IStage stage) {
        stageMementoCaretaker.save(this.stage);
        this.stage = stage;
    }
}
