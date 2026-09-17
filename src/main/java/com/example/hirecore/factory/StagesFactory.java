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
import com.example.hirecore.stages.Rejected;
import com.example.hirecore.stages.TechTest;
import com.example.hirecore.supervisors.Accountant;
import com.example.hirecore.supervisors.Manager;
import com.example.hirecore.supervisors.Recruiter;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class StagesFactory {

    private static final int CANDIDATE_COUNT = 10;
    private static final Random RANDOM = new Random();

    private static final String[] LATIN_NAMES = {
            "Juan Pérez", "María González", "Carlos Rodríguez", "Ana Martínez", "Luis Hernández",
            "Sofía López", "Diego Sánchez", "Valentina Ramírez", "Andrés Torres", "Camila Flores",
            "Miguel Rivera", "Isabella Gómez", "José Díaz", "Lucía Vargas", "Javier Castro",
            "Daniela Ortiz", "Fernando Ruiz", "Gabriela Morales", "Ricardo Jiménez", "Paula Álvarez",
            "Alejandro Romero", "Valeria Suárez", "Sebastián Medina", "Renata Herrera", "Mateo Guerrero",
            "Antonella Rojas", "Emilio Mendoza", "Martina Delgado", "Gonzalo Castillo", "Ximena Ortega",
            "Nicolás Vega", "Fernanda Cruz", "Rodrigo Reyes", "Catalina Molina", "Tomás Aguilar",
            "Julieta Campos", "Santiago Vásquez", "Regina Contreras", "Emiliano Silva", "Antonia Navarro",
            "Maximiliano Peña", "Constanza Fuentes", "Ignacio Cabrera", "Josefina Domínguez", "Joaquín Espinoza",
            "Amparo Salazar", "Pablo Cordero", "Pilar Bravo", "Cristian Miranda", "Rocío Paredes",
            "Iván Cortés", "Adriana Sepúlveda", "Marcos Figueroa", "Verónica Cisneros", "Esteban Carrasco",
            "Beatriz Escobar", "Rafael Lara", "Alejandra Ponce", "Enrique Zúñiga", "Mónica Rincón",
            "Guillermo Pacheco", "Carolina Vera", "Hugo Meza", "Teresa Godoy", "Óscar Salinas",
            "Marisol Quintero", "Vicente Osorio", "Dolores Palacios", "Álvaro Villalobos", "Esperanza Roldán",
            "Manuel Chávez", "Consuelo Bernal", "Leonardo Serrano", "Guadalupe Montes", "Federico Solano",
            "Milagros Acosta", "Arturo Barrios", "Soledad Nieves", "Raúl Cordero", "Yolanda Prieto",
            "Salvador Aranda", "Inés Camacho", "Domingo Lozano", "Remedios Franco", "Gerardo Ibarra",
            "Encarnación Andrade", "Bernardo Cuevas", "Asunción Vidal", "Ramiro Estrada", "Concepción Galindo",
            "Wilfredo Ochoa", "Perla Benítez", "Norberto Arroyo", "Aurora Villanueva", "Eduardo Cárdenas",
            "Leticia Marín", "Rogelio Calderón", "Mireya Zamora", "Anselmo Colón", "Esmeralda Robledo",
    };

    public static IStage createStages() {
        IStage contractedStage = new Contracted(null);
        IStage referencesVerifyStage = new ReferencesVerify(contractedStage);
        IStage offerStage = new Offer(referencesVerifyStage);
        IStage techTestStage = new TechTest(offerStage);
        IStage interviewStage = new Interview(techTestStage);
        return new Applied(interviewStage);
    }

    public static HiringPipeline createHiringPipeline() {
        List<IStage> stages = collectStages(createStages());
        IStage firstStage = stages.get(0);
        IStage rejectedStage = new Rejected(null);

        Recruiter recruiter = new Recruiter();
        recruiter.setName("Laura Gómez");
        recruiter.setEmail("laura.gomez@hire-core.com");

        Manager manager = new Manager();
        manager.setName("Carlos Pérez");
        manager.setEmail("carlos.perez@hire-core.com");

        Accountant accountant = new Accountant();
        accountant.setName("Marta Ruiz");
        accountant.setEmail("marta.ruiz@hire-core.com");

        List<Candidate> candidates = createCandidates(CANDIDATE_COUNT, firstStage);

        registerObservers(stages, recruiter, manager, accountant);
        ((IObservable) rejectedStage).addObserver(recruiter); // el reclutador también se entera de los rechazos

        return new HiringPipeline(stages, candidates, recruiter, manager, accountant,
                new CandidateManager(rejectedStage));
    }

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

    private static List<Candidate> createCandidates(int count, IStage firstStage) {
        List<Candidate> candidates = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String name = LATIN_NAMES[RANDOM.nextInt(LATIN_NAMES.length)];
            candidates.add(new Candidate(name, toEmail(name), firstStage));
        }
        return candidates;
    }

    private static String toEmail(String name) {
        String withoutAccents = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        String localPart = withoutAccents.toLowerCase(Locale.ROOT).replace(' ', '.');
        return localPart + "@example.com";
    }

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
