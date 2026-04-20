package studio.arcana.echostories.client;

public class DialogueOption {
    public final String text;
    public final Runnable action;

    public DialogueOption(String text, Runnable action) {
        this.text = text;
        this.action = action;
    }
}