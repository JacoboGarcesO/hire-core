package com.example.hirecore;

import com.example.hirecore.factory.HiringPipeline;
import com.example.hirecore.factory.StagesFactory;
import com.example.hirecore.ui.HireCoreMenu;

public class HireCoreApplication {

    public static void main(String[] args) {
        HiringPipeline pipeline = StagesFactory.createHiringPipeline();
        new HireCoreMenu(pipeline).run();
    }
}
