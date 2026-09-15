package com.example.hirecore.ui;

import com.example.hirecore.Candidate;
import com.example.hirecore.CandidateChangeRecord;
import com.example.hirecore.factory.HiringPipeline;
import com.example.hirecore.stages.IStage;
import com.example.hirecore.supervisors.Supervisor;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Dimension;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Menú interactivo con JOptionPane: la sesión arranca automáticamente como
 * la reclutadora (no hay selección de supervisor) y luego se muestra un
 * menú con las funcionalidades pedidas: avanzar de etapa, deshacer el
 * último cambio, rechazar y ver el historial de cambios de un candidato.
 */
public class HireCoreMenu {

    private static final String[] MAIN_OPTIONS = {
            "Avanzar de etapa", "Deshacer cambio", "Rechazar candidato", "Ver historial de cambios", "Salir"
    };
    private static final DateTimeFormatter HISTORY_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final HiringPipeline pipeline;

    public HireCoreMenu(HiringPipeline pipeline) {
        this.pipeline = pipeline;
    }

    public void run() {
        // La sesión siempre entra como la reclutadora: no hay selección de supervisor.
        Supervisor supervisor = pipeline.getRecruiter();

        JOptionPane.showMessageDialog(null,
                "Bienvenido/a, " + describe(supervisor) + ".",
                "Sesión iniciada", JOptionPane.INFORMATION_MESSAGE);

        mainMenu(supervisor);
    }

    // ------------------------------------------------------------------
    // Menú principal
    // ------------------------------------------------------------------

    private void mainMenu(Supervisor supervisor) {
        while (true) {
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Sesión activa: " + describe(supervisor),
                    "Hire Core",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    MAIN_OPTIONS,
                    MAIN_OPTIONS[0]);

            if (choice == JOptionPane.CLOSED_OPTION || choice == 4) {
                return;
            }
            switch (choice) {
                case 0 -> advanceStage(supervisor);
                case 1 -> undoStageChange(supervisor);
                case 2 -> rejectCandidate(supervisor);
                case 3 -> viewHistory();
            }
        }
    }

    private void advanceStage(Supervisor supervisor) {
        Candidate candidate = pickCandidate("Avanzar de etapa");
        if (candidate == null) {
            return;
        }

        String from = candidate.getStage().getClass().getSimpleName();
        try {
            pipeline.getCandidateManager().advance(candidate, supervisor);
            String to = candidate.getStage().getClass().getSimpleName();
            JOptionPane.showMessageDialog(null,
                    candidate.getName() + " avanzó de " + from + " a " + to + ".",
                    "Avance registrado", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalStateException noNextStage) {
            JOptionPane.showMessageDialog(null,
                    candidate.getName() + " ya está en la última etapa (" + from + "), no se puede avanzar más.",
                    "No se pudo avanzar", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void undoStageChange(Supervisor supervisor) {
        Candidate candidate = pickCandidate("Deshacer cambio de etapa");
        if (candidate == null) {
            return;
        }

        IStage before = candidate.getStage();
        pipeline.getCandidateManager().undoLastStageChange(candidate, supervisor);
        IStage after = candidate.getStage();

        if (before == after) {
            JOptionPane.showMessageDialog(null,
                    candidate.getName() + " no tiene cambios de etapa para deshacer.",
                    "Nada que deshacer", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null,
                    candidate.getName() + " volvió de " + before.getClass().getSimpleName()
                            + " a " + after.getClass().getSimpleName() + ".",
                    "Cambio deshecho", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void rejectCandidate(Supervisor supervisor) {
        Candidate candidate = pickCandidate("Rechazar candidato");
        if (candidate == null) {
            return;
        }

        String from = candidate.getStage().getClass().getSimpleName();
        pipeline.getCandidateManager().reject(candidate, supervisor);
        JOptionPane.showMessageDialog(null,
                candidate.getName() + " fue rechazado (estaba en " + from + ").",
                "Candidato rechazado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void viewHistory() {
        Candidate candidate = pickCandidate("Ver historial de cambios");
        if (candidate == null) {
            return;
        }

        List<CandidateChangeRecord> history = candidate.getHistory();
        if (history.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    candidate.getName() + " todavía no tiene cambios registrados.",
                    "Historial vacío", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(null, buildHistoryView(history),
                "Historial de " + candidate.getName(), JOptionPane.PLAIN_MESSAGE);
    }

    /** Arma un panel desplazable con el historial numerado, uno debajo del otro, fácil de leer. */
    private JScrollPane buildHistoryView(List<CandidateChangeRecord> history) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < history.size(); i++) {
            CandidateChangeRecord entry = history.get(i);
            text.append(i + 1).append(". ").append(entry.getChangedAt().format(HISTORY_DATE_FORMAT)).append('\n')
                    .append("     Etapa:  ").append(entry.getCandidateCopy().getStage().getClass().getSimpleName()).append('\n')
                    .append("     Por:    ").append(describe(entry.getChangedBy())).append('\n');
            if (i < history.size() - 1) {
                text.append('\n');
            }
        }

        JTextArea textArea = new JTextArea(text.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        textArea.setCaretPosition(0);
        textArea.setMargin(new java.awt.Insets(8, 10, 8, 10));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(420, Math.min(400, 40 + history.size() * 60)));
        return scrollPane;
    }

    // ------------------------------------------------------------------
    // Utilidades de UI
    // ------------------------------------------------------------------

    private Candidate pickCandidate(String title) {
        List<Candidate> candidates = pipeline.getCandidates();
        String[] options = candidates.stream()
                .map(candidate -> candidate.getName() + " (" + candidate.getStage().getClass().getSimpleName() + ")")
                .toArray(String[]::new);

        String selection = (String) JOptionPane.showInputDialog(
                null,
                "Selecciona un candidato:",
                title,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (selection == null) {
            return null;
        }
        return candidates.get(List.of(options).indexOf(selection));
    }

    private String describe(Supervisor supervisor) {
        return supervisor.getClass().getSimpleName() + " - " + supervisor.getName();
    }
}
