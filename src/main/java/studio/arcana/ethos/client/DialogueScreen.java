package studio.arcana.ethos.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import studio.arcana.ethos.EthosCore;
import java.util.List;

public class DialogueScreen extends Screen {
    private static final ResourceLocation DIALOGUE_BG = new ResourceLocation(EthosCore.MODID, "textures/gui/dialogue_text_bg.png");
    
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
        int screenW = this.width;
        int screenH = this.height;

        // Настройки кнопок выбора (слева)
        int btnWidth = 180;
        int btnX = 30; // Отступ от левого края
        int currentY = 40; // Начальная высота первой кнопки
        int spacing = 8;

        for (DialogueOption option : options) {
            // Рассчитываем высоту кнопки в зависимости от длины текста
            List<net.minecraft.util.FormattedCharSequence> lines = this.font.split(Component.literal(option.text), btnWidth - 20);
            int btnHeight = (lines.size() * 10) + 12;

            // Добавляем кнопку
            this.addRenderableWidget(EthosButton.flexibleDialogue(btnX, currentY, btnWidth, btnHeight, 
                Component.literal(option.text), (btn) -> {
                    option.action.run();
                    this.onClose();
            }));

            // Смещаем Y для следующей кнопки
            currentY += btnHeight + spacing;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Фон не рисуем, чтобы видеть мир
        int screenW = this.width;
        int screenH = this.height;

        // --- Отрисовка нижней панели (Фраза NPC) ---
        // Как на скрине с Альфредом: текст внизу по центру, имя над ним
        int barW = 300;
        int barX = (screenW - barW) / 2;
        int barY = screenH - 60;

        // Имя NPC (Альфред) - с маленькими разделителями по бокам
        String nameFormatted = "— " + npcName + " —";
        int nameW = this.font.width(nameFormatted);
        guiGraphics.drawString(this.font, nameFormatted, (screenW - nameW) / 2, barY - 15, 0xFFFFFF);

        // Сама фраза NPC
        int textW = this.font.width(dialogueText);
        guiGraphics.drawString(this.font, dialogueText, (screenW - textW) / 2, barY, 0xFFFFFF);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}