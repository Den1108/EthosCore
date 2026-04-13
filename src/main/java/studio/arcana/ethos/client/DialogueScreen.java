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

    // Параметры панели выносим в константы для удобства расчетов
    private final int barW = 400; 
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

        // 1. Рассчитываем позицию панели (поднимаем чуть выше, чтобы влезли кнопки)
        int barX = (screenW - barW) / 2;
        int barY = screenH - 100; // Подняли (было -20)

        // 2. Рассчитываем кнопки в ряд под панелью
        int buttonWidth = 80; // Используем размер track кнопки (80), так как они короче и влезут в ряд
        int spacing = 5;      // Расстояние между кнопками
        int totalButtonsWidth = (options.size() * buttonWidth) + ((options.size() - 1) * spacing);
        
        // Начальная точка X, чтобы ряд кнопок был по центру под панелью
        int startBtnX = (screenW - totalButtonsWidth) / 2;
        int btnY = barY + barH + 5; // Сразу под панелью с небольшим отступом

        for (int i = 0; i < options.size(); i++) {
            DialogueOption option = options.get(i);
            
            // Используем EthosButton.track, так как она 80x20 и лучше всего подходит для ряда
            this.addRenderableWidget(EthosButton.track(startBtnX + (i * (buttonWidth + spacing)), btnY, 
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

        // Те же координаты, что и в init
        int barX = (screenW - barW) / 2;
        int barY = screenH - 100; 

        // 1. Отрисовка подложки текста (400x60)
        guiGraphics.blit(TEXT_BG, barX, barY, 0, 0, barW, barH, barW, barH);
        
        // 2. Имя NPC и текст
        guiGraphics.drawString(this.font, "§6" + npcName, barX + 15, barY + 10, 0xFFFFFF);
        guiGraphics.drawWordWrap(this.font, Component.literal("§f" + dialogueText), barX + 15, barY + 22, barW - 30, 0xFFFFFF);

        // 3. Аватар (теперь он тоже чуть выше вместе с панелью)
        int portraitSize = 64;
        int portraitX = barX + barW + 5;
        int portraitY = barY + (barH - portraitSize) / 2; // Центрируем аватар по высоте панели
        
        guiGraphics.blit(AVATAR_FRAME, portraitX, portraitY, 0, 0, portraitSize, portraitSize, portraitSize, portraitSize);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}