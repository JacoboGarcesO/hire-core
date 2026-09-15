package com.example.hirecore.manager;

import com.example.hirecore.Candidate;
import com.example.hirecore.notifications.IObservable;
import com.example.hirecore.stages.IStage;
import com.example.hirecore.supervisors.Supervisor;

/**
 * Gestiona el avance (y el deshacer) de los candidatos por el pipeline de etapas.
 *
 * <p>En cada transición, el candidato se suscribe de forma TEMPORAL —solo
 * mientras dura esa transición— a la etapa de la que sale y a la que entra:
 * se notifica en ambas y de inmediato se le retira de las dos. Así nunca
 * queda como observador permanente de una etapa, que es compartida por todos
 * los candidatos que pasan por ella; si persistiera, el movimiento de uno
 * notificaría también a los demás que están parados en esa misma etapa.
 */
public class CandidateManager {

    /**
     * Avanza al candidato a la siguiente etapa.
     *
     * @param candidate  el candidato que avanza
     * @param performedBy el supervisor que hace el cambio; queda registrado en
     *                    {@link Candidate#getHistory()} junto con una copia del
     *                    candidato tras el cambio.
     */
    public void advance(Candidate candidate, Supervisor performedBy) {
        IStage previousStage = candidate.getStage();
        IStage nextStage = previousStage.ahead();

        String transition = candidate.getName() + " avanzó de " + previousStage.getClass().getSimpleName()
                + " a " + nextStage.getClass().getSimpleName();

        notifyTransition(candidate, previousStage, nextStage, transition);

        candidate.setStage(nextStage);        // guarda un memento de la etapa anterior y aplica el cambio
        candidate.recordChange(performedBy);  // deja constancia en el historial de auditoría
    }

    /**
     * Deshace el último cambio de etapa del candidato (patrón Memento, ver
     * {@link Candidate#undoStageChange()}), manteniendo la misma lógica de
     * notificación que {@link #advance}: se avisa tanto en la etapa en la
     * que estaba como en la etapa a la que vuelve.
     *
     * <p>A diferencia de las notificaciones (sin cambios), la regresión sí
     * queda registrada en {@link Candidate#getHistory()}, igual que un avance.
     *
     * @param performedBy el supervisor que hace la regresión; queda registrado
     *                    en el historial junto con una copia del candidato
     *                    tras deshacer el cambio.
     */
    public void undoLastStageChange(Candidate candidate, Supervisor performedBy) {
        IStage stageBeforeUndo = candidate.getStage();
        if (!candidate.undoStageChange()) {
            return;
        }
        IStage stageAfterUndo = candidate.getStage();

        String transition = candidate.getName() + " deshizo su cambio de etapa: volvió de "
                + stageBeforeUndo.getClass().getSimpleName() + " a " + stageAfterUndo.getClass().getSimpleName();

        notifyTransition(candidate, stageBeforeUndo, stageAfterUndo, transition);
        candidate.recordChange(performedBy); // la regresión también queda en el historial
    }

    /**
     * Suscribe temporalmente al candidato a ambas etapas, notifica en cada
     * una (con el nombre de la etapa que emite, para distinguir el aviso de
     * salida del de entrada aunque el resto del mensaje sea igual) y lo
     * retira de las dos.
     */
    private void notifyTransition(Candidate candidate, IStage fromStage, IStage toStage, String message) {
        IObservable fromObservable = asObservable(fromStage);
        IObservable toObservable = asObservable(toStage);

        fromObservable.addObserver(candidate);
        toObservable.addObserver(candidate);

        fromObservable.notifyObservers("(" + fromStage.getClass().getSimpleName() + ") " + message);
        toObservable.notifyObservers("(" + toStage.getClass().getSimpleName() + ") " + message);

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
