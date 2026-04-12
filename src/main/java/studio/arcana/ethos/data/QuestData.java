package studio.arcana.ethos.data;

import java.util.List;

public class QuestData {
    public String id;
    public String title;
    public String description;
    public List<Objective> objectives;

    public static class Objective {
        public String type; // Например: "ITEM"
        public String target_id; // "minecraft:iron_ingot"
        public int amount_required;
        public int current_progress;
        public String description; // "Собрать железные слитки"
    }
}