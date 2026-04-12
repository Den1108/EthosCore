package studio.arcana.ethos.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import java.util.List;

public class DialogueScreen extends Screen {
    private final String npcName;
    private final String dialogueText;
    private final List<DialogueOption> options;

    public DialogueScreen(String npcName, String dialogueText, List<DialogueOption> options) {
        super(Component.literal(npcName));
        this.npcName = npcName;
        this.dialogueText = dialogueText;
        this.options = options;
    }

    @Override
    protected void init() {
        int buttonWidth = 200;
        int startY = this.height / 2 + 20;

        for (int i = 0; i < options.size(); i++) {
            DialogueOption option = options.get(i);
            this.addRenderableWidget(Button.builder(Component.literal(option.text), (button) -> {
                option.action.run();
                this.onClose();
            }).bounds(this.width / 2 - buttonWidth / 2, startY + (i * 24), buttonWidth, 20).build());
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        
        int bgX = this.width / 2 - 160;
        int bgY = this.height / 2 - 80;
        // Отрисовка основного окна диалога
        guiGraphics.fill(bgX, bgY, bgX + 320, bgY + 160, 0xCC000000);
        
        // Текст
        guiGraphics.drawString(this.font, "§b" + npcName, bgX + 15, bgY + 15, 0xFFFFFF);
        guiGraphics.drawWordWrap(this.font, Component.literal(dialogueText), bgX + 15, bgY + 40, 290, 0xDDDDDD);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // Игра не ставится на паузу в диалоге (как в сюжетных сборках)
    }

    public static class DialogueOption {
        String text;
        Runnable action;

        public DialogueOption(String text, Runnable action) {
            this.text = text;
            this.action = action;
        }
    }
}