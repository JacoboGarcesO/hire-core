package com.example.hirecore.notifications;

public interface IObserver {
    String getId();

    String getName();

    String getEmail();

    String getRole();

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
