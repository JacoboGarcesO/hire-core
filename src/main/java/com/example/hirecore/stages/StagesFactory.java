package com.example.hirecore.stages;

public class StagesFactory {
    public static IStage createStages() {
        IStage contractedStage = new Contracted(null);
        IStage referencesVerifyStage = new ReferencesVerify(contractedStage);
        IStage offerStage = new Offer(referencesVerifyStage);
        IStage techTestStage = new TechTest(offerStage);
        IStage interviewStage = new Interview(techTestStage);
        return new Applied(interviewStage);
    }
}