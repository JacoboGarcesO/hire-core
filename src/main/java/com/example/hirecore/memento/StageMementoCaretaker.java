package com.example.hirecore.memento;

import com.example.hirecore.stages.IStage;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

/**
 * Caretaker del patrón Memento: guarda las instantáneas de etapa de un
 * candidato y permite deshacer, sin que quien lo use (el candidato,
 * Originator) tenga que conocer cómo se guardan ni qué contienen.
 */
public class StageMementoCaretaker {
    private final Deque<StageMemento> mementos = new ArrayDeque<>();

    /** Guarda la etapa actual antes de que cambie. */
    public void save(IStage stage) {
        mementos.push(new StageMemento(stage));
    }

    /** Deshace el último cambio guardado. Vacío si no había nada que deshacer. */
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
