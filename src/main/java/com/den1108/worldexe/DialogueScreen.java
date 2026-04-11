package com.den1108.worldexe;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import java.util.List;
import java.util.ArrayList;

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
        int startY = this.height / 2 + 10;

        // Создаем кнопки на основе переданных вариантов ответа
        for (int i = 0; i < options.size(); i++) {
            DialogueOption option = options.get(i);
            this.addRenderableWidget(Button.builder(Component.literal(option.text), (button) -> {
                option.action.run();
                this.onClose();
            }).bounds(this.width / 2 - buttonWidth / 2, startY + (i * 25), buttonWidth, 20).build());
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics); // Затемнение фона игры
        
        // Рисуем плашку под текст
        int bgX = this.width / 2 - 150;
        int bgY = this.height / 2 - 70;
        guiGraphics.fill(bgX, bgY, bgX + 300, bgY + 150, 0xAA000000);
        
        // Имя NPC и основной текст
        guiGraphics.drawString(this.font, "§e" + npcName, bgX + 10, bgY + 10, 0xFFFFFF);
        guiGraphics.drawWordWrap(this.font, Component.literal(dialogueText), bgX + 10, bgY + 30, 280, 0xFFFFFF);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    // Вспомогательный класс для вариантов ответа
    public static class DialogueOption {
        String text;
        Runnable action;

        public DialogueOption(String text, Runnable action) {
            this.text = text;
            this.action = action;
        }
    }
}