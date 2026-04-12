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
    
    private final int bgWidth = 280;
    private final int bgHeight = 180;
    
    private QuestData selectedQuest = null;

    public QuestJournalScreen() {
        super(Component.literal("Журнал заданий"));
    }

    @Override
    protected void init() {
        int x = (this.width - bgWidth) / 2;
        int y = (this.height - bgHeight) / 2;
        
        // Сдвигаем кнопки так, чтобы они четко попадали в левое поле (как на скрине 2)
        int listX = x + 10; 
        int listY = y + 35;
        int buttonWidth = 110; // Немного увеличим для удобства клика

        this.clearWidgets(); // Очищаем старые виджеты при изменении размера окна

        for (QuestData quest : QuestManager.getActiveQuests()) {
            this.addRenderableWidget(new EthosButton(
                listX, 
                listY, 
                buttonWidth, 
                18, // Уменьшил высоту, чтобы список был компактнее
                Component.literal(quest.title), 
                (button) -> {
                    this.selectedQuest = quest;
                }
            ));
            listY += 20; // Расстояние между кнопками
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 1. Сначала фон затемнения игры
        this.renderBackground(guiGraphics);
        
        int x = (this.width - bgWidth) / 2;
        int y = (this.height - bgHeight) / 2;
        
        // 2. Отрисовка основной текстуры блокнота
        guiGraphics.blit(BG_TEXTURE, x, y, 0, 0, bgWidth, bgHeight, bgWidth, bgHeight);
        
        // 3. Заголовок "ЗАДАНИЯ" (левая страница)
        guiGraphics.drawCenteredString(this.font, "§6§lЗАДАНИЯ", x + 65, y + 15, 0xFFFFFF);

        // 4. Отрисовка кнопок (виджетов)
        // ВАЖНО: super.render рисует кнопки. Рисуем их ДО текста квеста, 
        // чтобы текст правой страницы не перекрывался кнопками, если они широкие.
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // 5. Отрисовка деталей квеста (правая страница)
        if (selectedQuest != null) {
            renderQuestDetails(guiGraphics, x + 135, y + 20);
        } else {
            // Текст-заглушка
            guiGraphics.drawCenteredString(this.font, "§8Выберите квест", x + 205, y + 80, 0xFFFFFF);
        }
    }

    private void renderQuestDetails(GuiGraphics guiGraphics, int x, int y) {
        // Название квеста
        guiGraphics.drawString(this.font, "§e" + selectedQuest.title, x + 5, y + 5, 0xFFFFFF);
        
        // Линия-разделитель (опционально, можно убрать)
        // guiGraphics.fill(x + 5, y + 18, x + 130, y + 19, 0x44FFFFFF);

        // Описание
        guiGraphics.drawWordWrap(this.font, Component.literal("§f" + selectedQuest.description), 
            x + 5, y + 25, 125, 0xFFFFFF);
        
        int taskY = y + 85;
        guiGraphics.drawString(this.font, "§6Задачи:", x + 5, taskY, 0xFFFFFF);
        
        if (selectedQuest.objectives != null) {
            for (QuestData.Objective obj : selectedQuest.objectives) {
                taskY += 12;
                boolean isDone = obj.current_progress >= obj.amount_required;
                String status = isDone ? "§a✔ " : "§7- ";
                String text = status + obj.description + " (" + obj.current_progress + "/" + obj.amount_required + ")";
                
                // Чтобы текст задач не вылезал за поля
                guiGraphics.drawString(this.font, text, x + 5, taskY, 0xFFFFFF);
            }
        }
    }
}