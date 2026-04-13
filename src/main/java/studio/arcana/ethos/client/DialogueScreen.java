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

        // Настройки кнопок выбора (прижимаем почти в самый левый верхний угол)
        int btnWidth = 240; // Делаем кнопки шире
        int btnX = 10;      // Минимальный отступ слева
        int currentY = 10;  // Минимальный отступ сверху
        int spacing = 5;    // Расстояние между кнопками

        for (DialogueOption option : options) {
            // Добавляем стилизованные скобки вокруг текста выбора
            String formattedOption = "[" + option.text + "]";
            
            // Рассчитываем высоту кнопки в зависимости от длины текста
            List<net.minecraft.util.FormattedCharSequence> lines = this.font.split(Component.literal(formattedOption), btnWidth - 20);
            int btnHeight = (lines.size() * 10) + 12;

            // Добавляем кнопку
            this.addRenderableWidget(EthosButton.flexibleDialogue(btnX, currentY, btnWidth, btnHeight, 
                Component.literal(formattedOption), (btn) -> {
                    option.action.run();
                    this.onClose();
            }));

            // Смещаем Y для следующей кнопки
            currentY += btnHeight + spacing;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int screenW = this.width;
        int screenH = this.height;

        // --- Отрисовка нижней панели (Фраза NPC) с увеличением масштаба ---
        guiGraphics.pose().pushPose();
        
        float scale = 1.5f; // Увеличиваем размер текста в 1.5 раза
        guiGraphics.pose().scale(scale, scale, 1.0f);

        // Базовая высота (нижняя часть экрана). Делим на scale, чтобы координаты не улетели за экран
        float scaledBarY = (screenH - 50) / scale; 

        // Имя NPC (Альфред) - поднимаем выше
        String nameFormatted = "— " + npcName + " —";
        int nameW = this.font.width(nameFormatted);
        float nameX = ((screenW / scale) - nameW) / 2.0f;
        float nameY = scaledBarY - 25; // Подняли имя над основным текстом
        
        guiGraphics.drawString(this.font, nameFormatted, (int)nameX, (int)nameY, 0xFFFFFF);

        // Сама фраза NPC
        int textW = this.font.width(dialogueText);
        float textX = ((screenW / scale) - textW) / 2.0f;
        
        guiGraphics.drawString(this.font, dialogueText, (int)textX, (int)scaledBarY, 0xFFFFFF);

        guiGraphics.pose().popPose(); // Возвращаем обычный масштаб для остального интерфейса

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}