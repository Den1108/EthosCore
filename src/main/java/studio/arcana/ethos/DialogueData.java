package studio.arcana.ethos;

import java.util.List;

public class DialogueData {
    public String npc_name;
    public String dialogue_text;
    public List<Option> options;

    public static class Option {
        public String text;
        public String action_type;
        public String action_value;
    }
}