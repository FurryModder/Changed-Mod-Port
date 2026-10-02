package net.furrymodder.changed.client;

import net.furrymodder.changed.tutorial.ChangedTutorialSteps;

public interface ChangedOptions {
    ChangedTutorialSteps getChangedTutorialStep();
    void setChangedTutorialStep(ChangedTutorialSteps step);
}
