package studio.arcana.ethos.data;

import java.util.List;

public class DialogueData {
    public String npc_name;
    public String dialogue_text;
    public int display_ticks; // Новое поле для времени (если 0, поставим стандарт)
    public List<Option> options;

    public static class Option {
        public String text;
        public String action_type;
        public String action_value;
    }
}