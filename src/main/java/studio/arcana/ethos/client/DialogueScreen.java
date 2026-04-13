package studio.arcana.ethos.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import studio.arcana.ethos.EthosCore;
import java.util.List;

public class DialogueScreen extends Screen {
    // Текстура плашки для текста (внизу)
    private static final ResourceLocation TEXT_BG = new ResourceLocation(EthosCore.MODID, "textures/gui/dialogue_text_bg.png");
    // Текстура для портрета (опционально)
    private static final ResourceLocation AVATAR_FRAME = new ResourceLocation(EthosCore.MODID, "textures/gui/avatar_frame.png");
    
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
        // Располагаем кнопки слева или справа (как на фото с Надимом)
        int btnX = 20; // Отступ слева
        int startY = this.height / 4; // Начинаем с верхней четверти экрана

        for (int i = 0; i < options.size(); i++) {
            DialogueOption option = options.get(i);
            // Используем твою "золотую середину" EthosButton.dialogue
            // Но ширина кнопок в стиле Надима обычно чуть меньше или адаптивная
            this.addRenderableWidget(EthosButton.dialogue(btnX, startY + (i * 25), 
                Component.literal(option.text), (btn) -> {
                    option.action.run();
                    this.onClose();
            }));
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // МЫ НЕ ВЫЗЫВАЕМ renderBackground, чтобы видеть мир и NPC!
        
        int screenW = this.width;
        int screenH = this.height;

        // --- 1. Отрисовка нижней панели текста ---
        int barW = 320;
        int barH = 60;
        int barX = (screenW - barW) / 2;
        int barY = screenH - barH - 20; // Чуть выше хотбара

        // Рисуем подложку текста (сделай её полупрозрачной в .png)
        guiGraphics.blit(TEXT_BG, barX, barY, 0, 0, barW, barH, barW, barH);
        
        // Имя NPC (Золотистым)
        guiGraphics.drawString(this.font, "§6" + npcName, barX + 15, barY + 10, 0xFFFFFF);
        // Текст диалога (Белым с автопереносом)
        guiGraphics.drawWordWrap(this.font, Component.literal("§f" + dialogueText), barX + 15, barY + 22, barW - 30, 0xFFFFFF);

        // --- 2. Отрисовка рамки аватара (справа внизу) ---
        // Как у Надима: портрет стоит справа от текста
        int portraitSize = 64;
        int portraitX = barX + barW + 5;
        int portraitY = barY - (portraitSize / 2) + 20;
        guiGraphics.blit(AVATAR_FRAME, portraitX, portraitY, 0, 0, portraitSize, portraitSize, portraitSize, portraitSize);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // Чтобы мир жил во время диалога!
    }
}