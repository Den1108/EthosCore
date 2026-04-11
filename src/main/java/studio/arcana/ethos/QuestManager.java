package studio.arcana.ethos;

import java.util.ArrayList;
import java.util.List;

public class QuestManager {
    private static final List<String> activeQuests = new ArrayList<>();
    private static final List<String> completedQuests = new ArrayList<>();

    public static void acceptQuest(String name) {
        if (!activeQuests.contains(name) && !completedQuests.contains(name)) {
            activeQuests.add(name);
        }
    }

    public static void completeQuest(String name) {
        if (activeQuests.remove(name)) {
            completedQuests.add(name);
        }
    }

    public static List<String> getActiveQuests() { return activeQuests; }
    public static List<String> getCompletedQuests() { return completedQuests; }
}