package studio.arcana.ethos.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import studio.arcana.ethos.EthosCore;
import studio.arcana.ethos.data.QuestData;
import studio.arcana.ethos.logic.QuestManager;

public class QuestJournalScreen extends Screen {
    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(EthosCore.MODID, "textures/gui/journal_bg.png");
    
    // Теперь размеры GUI и Текстуры СОВПАДАЮТ
    private final int bgWidth = 600; 
    private final int bgHeight = 400;

    private QuestData selectedQuest = null;

    public QuestJournalScreen() {
        super(Component.literal("Журнал заданий"));
    }

    @Override
    protected void init() {
        // Вычисляем центр экрана для нашего окна 600x400
        int x = (this.width - bgWidth) / 2;
        int y = (this.height - bgHeight) / 2;
        
        // Позиции кнопок (подгоняем под твои 600x400)
        int buttonX = x + 35; // 35px от края книги
        int buttonY = y + 60; // Чуть ниже заголовка
        int buttonWidth = 220; // Кнопки пошире для левой страницы

        this.clearWidgets();

        for (QuestData quest : QuestManager.getActiveQuests()) {
            this.addRenderableWidget(new EthosButton(
                buttonX, 
                buttonY, 
                buttonWidth, 
                20, 
                Component.literal(quest.title), 
                (button) -> {
                    this.selectedQuest = quest;
                }
            ));
            buttonY += 24;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        
        int x = (this.width - bgWidth) / 2;
        int y = (this.height - bgHeight) / 2;
        
        // ГЛАВНОЕ ИСПРАВЛЕНИЕ ТУТ:
        // Используем blit, где последние два параметра — это ПОЛНЫЙ размер твоего .png файла.
        // Если файл 600x400, то и в конце должно быть 600, 400.
        guiGraphics.blit(BG_TEXTURE, x, y, 0, 0, bgWidth, bgHeight, 600, 400);
        
        // Текст для левой панели (Заголовок)
        guiGraphics.drawCenteredString(this.font, "§6§lЗАДАНИЯ", x + 145, y + 35, 0xFFFFFF);

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Текст для правой панели
        int rightPageX = x + 330; 
        int rightPageY = y + 35;
        
        if (selectedQuest != null) {
            guiGraphics.drawCenteredString(this.font, "§e" + selectedQuest.title, rightPageX + 125, rightPageY, 0xFFFFFF);
            guiGraphics.drawWordWrap(this.font, Component.literal("§7" + selectedQuest.description), rightPageX, rightPageY + 30, 250, 0xDDDDDD);
            
            int taskY = rightPageY + 120;
            guiGraphics.drawString(this.font, "§6Задачи:", rightPageX, taskY, 0xFFFFFF);
            
            if (selectedQuest.objectives != null) {
                for (QuestData.Objective obj : selectedQuest.objectives) {
                    taskY += 15;
                    String status = (obj.current_progress >= obj.amount_required) ? "§a✔ " : "§8- ";
                    guiGraphics.drawString(this.font, status + "§f" + obj.description + " §8(" + obj.current_progress + "/" + obj.amount_required + ")", rightPageX, taskY, 0xFFFFFF);
                }
            }
        }
    }
}