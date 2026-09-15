package com.example.hirecore.notifications;

public interface IObserver {
    /** Identidad estable del observador: la usan addObserver/removeObserver para no duplicar ni comparar por referencia. */
    String getId();

    String getName();

    String getEmail();

    /** Cómo se identifica este destinatario en el correo simulado, p. ej. "Reclutador" o "Candidato". */
    String getRole();

    /** Recibe la notificación como si fuera un correo: destinatario, asunto y contenido. */
    default void notify(String subject, String content) {
        System.out.println("================ NUEVO CORREO ================");
        System.out.println("De: Hire Core <notificaciones@hire-core.com>");
        System.out.println("Para: " + getRole() + " " + getName() + " <" + getEmail() + ">");
        System.out.println("Asunto: " + subject);
        System.out.println("------------------------------------------------");
        System.out.println(content);
        System.out.println("================================================");
    }
}
