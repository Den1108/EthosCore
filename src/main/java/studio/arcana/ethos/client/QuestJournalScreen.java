package studio.arcana.ethos.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import studio.arcana.ethos.EthosCore;
import studio.arcana.ethos.data.QuestData;
import studio.arcana.ethos.logic.QuestManager;

public class QuestJournalScreen extends Screen {
    // Путь к текстуре фона журнала
    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(EthosCore.MODID + ":" + "textures/gui/journal_bg.png");
    
    // Размеры текстуры (те, что мы обсуждали)
    private final int bgWidth = 1280 ;
    private final int bgHeight = 576;

    public QuestJournalScreen() {
        super(Component.literal("Журнал заданий"));
    }

    @Override
    protected void init() {
        int x = (this.width - bgWidth) / 2;
        int y = (this.height - bgHeight) / 2;
        
        int buttonY = y + 40;
        int buttonWidth = 160;

        // Создаем кнопки для каждого активного квеста
        for (QuestData quest : QuestManager.getActiveQuests()) {
            this.addRenderableWidget(new EthosButton(
                this.width / 2 - buttonWidth / 2, 
                buttonY, 
                buttonWidth, 
                20, 
                Component.literal(quest.title), 
                (button) -> {
                    this.minecraft.setScreen(new QuestDetailsScreen(quest));
                }
            ));
            buttonY += 25;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 1. Затемнение заднего плана (ванильное)
        this.renderBackground(guiGraphics);
        
        // 2. Отрисовка нашей кастомной текстуры журнала
        int x = (this.width - bgWidth) / 2;
        int y = (this.height - bgHeight) / 2;
        
        // Рисуем фон (аргументы: текстура, x, y, u, v, ширина_отрисовки, высота_отрисовки, ширина_файла, высота_файла)
        guiGraphics.blit(BG_TEXTURE, x, y, 0, 0, bgWidth, bgHeight, bgWidth, bgHeight);
        
        // 3. Отрисовка заголовка
        guiGraphics.drawCenteredString(this.font, "§6§lЖУРНАЛ ЗАДАНИЙ", this.width / 2, y + 15, 0xFFFFFF);
        
        // Если квестов нет, пишем об этом
        if (QuestManager.getActiveQuests().isEmpty()) {
            guiGraphics.drawCenteredString(this.font, "§8Список пуст...", this.width / 2, y + 80, 0xFFFFFF);
        }

        // 4. Отрисовка кнопок и остального интерфейса
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // Игра не будет ставиться на паузу при открытии журнала
    }
}