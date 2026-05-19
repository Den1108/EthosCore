package studio.arcana.echostories.data;

import java.util.List;

public class DialogueData {
    public String start_node;
    public List<Node> nodes;

    public static class Node {
        public String id;
        public String sender_name;
        public String dialogue_text;
        public int display_ticks;
        public String next_node; // Поле для автоматического перехода к следующему шагу оверлея
        public List<Option> options;
    }

    public static class Option {
        public String text;
        public String action_type;  // NEXT_NODE, ACCEPT_QUEST, COMPLETE_QUEST, MESSAGE, CLOSE
        public String action_value; // ID следующей ноды, ID квеста, или текст сообщения
    }

    public Node getNode(String id) {
        if (nodes == null) return null;
        for (Node node : nodes) {
            if (node.id.equals(id)) {
                return node;
            }
        }
        return null;
    }
}