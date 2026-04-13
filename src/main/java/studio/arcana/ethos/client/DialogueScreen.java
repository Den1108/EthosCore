package studio.arcana.ethos.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import studio.arcana.ethos.EthosCore;
import java.util.List;

public class DialogueScreen extends Screen {
    private static final ResourceLocation TEXT_BG = ResourceLocation.fromNamespaceAndPath(EthosCore.MODID, "textures/gui/dialogue_text_bg.png");
    private static final ResourceLocation AVATAR_FRAME = ResourceLocation.fromNamespaceAndPath(EthosCore.MODID, "textures/gui/avatar_frame.png");
    
    private final String npcName;
    private final String dialogueText;
    private final List<DialogueOption> options;

    // Параметры текстовой панели
    private final int barW = 320; 
    private final int barH = 60;

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

        // --- ЛОГИКА КНОПОК (Вертикально справа) ---
        int btnWidth = 120; // Сделаем их чуть шире для удобства клика
        int btnHeight = 20;
        int spacing = 10;   // Расстояние между кнопками
        
        // Координата X: прижимаем к правому краю с отступом 20
        int btnX = screenW - btnWidth - 20; 
        
        // Координата Y: начинаем чуть выше середины экрана
        int totalHeight = (options.size() * (btnHeight + spacing)) - spacing;
        int startY = (screenH / 2) - (totalHeight / 2);

        for (int i = 0; i < options.size(); i++) {
            DialogueOption option = options.get(i);
            
            // Используем стандартную кнопку диалога, но с нашими координатами
            this.addRenderableWidget(EthosButton.dialogue(btnX, startY + (i * (btnHeight + spacing)), 
                Component.literal(option.text), (btn) -> {
                    option.action.run();
                    this.onClose();
            }));
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int screenW = this.width;
        int screenH = this.height;

        // Координаты панели (ровно над хотбаром)
        int barX = (screenW - barW) / 2;
        int barY = screenH - 75; 

        // 1. Отрисовка подложки текста
        guiGraphics.blit(TEXT_BG, barX, barY, 0, 0, barW, barH, 512, 512);
        
        // 2. Имя и текст
        guiGraphics.drawString(this.font, "§6" + npcName, barX + 15, barY + 10, 0xFFFFFF);
        guiGraphics.drawWordWrap(this.font, Component.literal("§f" + dialogueText), 
            barX + 15, barY + 22, barW - 30, 0xFFFFFF);

        // 3. Аватар (ставим его слева от текстовой панели, чтобы не мешал кнопкам справа)
        int portraitSize = 50;
        int portraitX = barX - portraitSize - 5;
        int portraitY = barY + (barH - portraitSize) / 2;
        
        guiGraphics.blit(AVATAR_FRAME, portraitX, portraitY, 0, 0, portraitSize, portraitSize, 512, 512);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}