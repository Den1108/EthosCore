package studio.arcana.ethos.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import studio.arcana.ethos.EthosCore;
import java.util.List;

public class DialogueScreen extends Screen {
    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(EthosCore.MODID, "textures/gui/dialogue_bg.png");
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
        int buttonWidth = 180;
        int startY = this.height - 80; // Сдвигаем кнопки вниз экрана

        for (int i = 0; i < options.size(); i++) {
            DialogueOption option = options.get(i);
            // Используем наш новый класс EthosButton
            this.addRenderableWidget(new EthosButton(this.width / 2 - buttonWidth / 2, startY + (i * 22), buttonWidth, 20, 
                Component.literal(option.text), (btn) -> {
                    option.action.run();
                    this.onClose();
            }));
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        
        // Рисуем фон диалога (внизу экрана, как в RPG)
        int bgW = 350;
        int bgH = 120;
        int x = this.width / 2 - bgW / 2;
        int y = this.height - bgH - 10;
        
        guiGraphics.blit(BG_TEXTURE, x, y, 0, 0, bgW, bgH, bgW, bgH);
        
        // Имя NPC
        guiGraphics.drawString(this.font, "§6" + npcName, x + 20, y + 15, 0xFFFFFF);
        // Текст диалога
        guiGraphics.drawWordWrap(this.font, Component.literal(dialogueText), x + 20, y + 35, bgW - 40, 0xEEEEEE);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    public record DialogueOption(String text, Runnable action) {}
}