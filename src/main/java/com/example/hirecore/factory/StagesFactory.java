package com.example.hirecore.factory;

import com.example.hirecore.Candidate;
import com.example.hirecore.manager.CandidateManager;
import com.example.hirecore.notifications.IObservable;
import com.example.hirecore.stages.Applied;
import com.example.hirecore.stages.Contracted;
import com.example.hirecore.stages.IStage;
import com.example.hirecore.stages.Interview;
import com.example.hirecore.stages.Offer;
import com.example.hirecore.stages.ReferencesVerify;
import com.example.hirecore.stages.TechTest;
import com.example.hirecore.supervisors.Accountant;
import com.example.hirecore.supervisors.Manager;
import com.example.hirecore.supervisors.Recruiter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StagesFactory {

    private static final int CANDIDATE_COUNT = 10;
    private static final Random RANDOM = new Random();

    public static IStage createStages() {
        IStage contractedStage = new Contracted(null);
        IStage referencesVerifyStage = new ReferencesVerify(contractedStage);
        IStage offerStage = new Offer(referencesVerifyStage);
        IStage techTestStage = new TechTest(offerStage);
        IStage interviewStage = new Interview(techTestStage);
        return new Applied(interviewStage);
    }

    /**
     * Construye el pipeline completo: la cadena de etapas, los roles
     * (reclutador, gerente de contratación, nómina), 10 candidatos asignados
     * aleatoriamente a cualquier etapa salvo la última, y las suscripciones
     * de notificación diferenciadas entre todos ellos.
     */
    public static HiringPipeline createHiringPipeline() {
        List<IStage> stages = collectStages(createStages());
        List<IStage> assignableStages = stages.subList(0, stages.size() - 1); // todas menos la última (Contracted)

        Recruiter recruiter = new Recruiter();
        recruiter.setName("Laura Gómez");
        recruiter.setEmail("laura.gomez@hire-core.com");

        Manager manager = new Manager();
        manager.setName("Carlos Pérez");
        manager.setEmail("carlos.perez@hire-core.com");

        Accountant accountant = new Accountant();
        accountant.setName("Marta Ruiz");
        accountant.setEmail("marta.ruiz@hire-core.com");

        List<Candidate> candidates = createCandidates(CANDIDATE_COUNT, assignableStages);

        registerObservers(stages, recruiter, manager, accountant);

        return new HiringPipeline(stages, candidates, recruiter, manager, accountant, new CandidateManager());
    }

    /** Recorre la cadena de etapas desde la cabeza hasta la última (nextStage == null). */
    private static List<IStage> collectStages(IStage head) {
        List<IStage> stages = new ArrayList<>();
        IStage current = head;
        while (true) {
            stages.add(current);
            try {
                current = current.ahead();
            } catch (IllegalStateException noMoreStages) {
                break;
            }
        }
        return stages;
    }

    private static List<Candidate> createCandidates(int count, List<IStage> assignableStages) {
        List<Candidate> candidates = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            IStage stage = assignableStages.get(RANDOM.nextInt(assignableStages.size()));
            candidates.add(new Candidate("Candidato " + i, "candidato" + i + "@example.com", stage));
        }
        return candidates;
    }

    /**
     * Notificaciones diferenciadas:
     * <ul>
     *   <li>Reclutador: todas las etapas (de cualquier candidato).</li>
     *   <li>Gerente de contratación: solo Oferta y Contratado.</li>
     *   <li>Nómina (accountant): solo Contratado.</li>
     * </ul>
     *
     * <p>El candidato NO se registra aquí de forma permanente: las etapas son
     * instancias compartidas por todos los candidatos que pasan por ellas, así
     * que dejarlo suscrito haría que el movimiento de uno notificara también a
     * los demás que están en esa misma etapa. En cambio,
     * {@link CandidateManager#advance} lo suscribe solo durante cada
     * transición y lo retira de inmediato.
     */
    private static void registerObservers(List<IStage> stages, Recruiter recruiter, Manager manager,
                                          Accountant accountant) {
        for (IStage stage : stages) {
            IObservable observable = (IObservable) stage;
            observable.addObserver(recruiter);

            if (stage instanceof Offer || stage instanceof Contracted) {
                observable.addObserver(manager);
            }
            if (stage instanceof Contracted) {
                observable.addObserver(accountant);
            }
        }
    }

}
