package com.example.hirecore.supervisors;

import com.example.hirecore.notifications.IObserver;

public class Recruiter extends Supervisor implements IObserver {
    @Override
    public void notify(String message) {
        System.out.println(message);
    }
}