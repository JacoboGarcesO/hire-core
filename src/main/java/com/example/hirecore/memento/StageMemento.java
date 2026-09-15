package com.example.hirecore.memento;

import com.example.hirecore.stages.IStage;

/**
 * Memento: instantánea opaca de una etapa anterior de un candidato.
 *
 * <p>Es de paquete (no público) a propósito: solo {@link StageMementoCaretaker},
 * en este mismo paquete, puede crearla o leer su contenido. Nadie fuera del
 * paquete del patrón Memento toca esta clase.
 */
public class StageMemento {
    private final IStage stage;

    StageMemento(IStage stage) {
        this.stage = stage;
    }

    IStage getStage() {
        return stage;
    }
}
