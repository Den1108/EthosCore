package studio.arcana.ethos.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import studio.arcana.ethos.EthosCore;
import studio.arcana.ethos.data.QuestData;
import studio.arcana.ethos.logic.QuestManager;

public class QuestJournalScreen extends Screen {
    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(EthosCore.MODID + ":textures/gui/journal_bg.png");
    
    private final int bgWidth = 280;
    private final int bgHeight = 180;
    
    // Храним выбранный квест здесь
    private QuestData selectedQuest = null;

    public QuestJournalScreen() {
        super(Component.literal("Журнал заданий"));
    }

    @Override
    protected void init() {
        int x = (this.width - bgWidth) / 2;
        int y = (this.height - bgHeight) / 2;
        
        int listX = x + 15; // Отступ кнопок от левого края фона
        int listY = y + 40;
        int buttonWidth = 100; // Кнопки теперь уже, чтобы влезть в левую колонку

        for (QuestData quest : QuestManager.getActiveQuests()) {
            this.addRenderableWidget(new EthosButton(
                listX, 
                listY, 
                buttonWidth, 
                20, 
                Component.literal(quest.title), 
                (button) -> {
                    // Вместо открытия нового экрана просто меняем выбранный квест
                    this.selectedQuest = quest;
                }
            ));
            listY += 22;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        
        int x = (this.width - bgWidth) / 2;
        int y = (this.height - bgHeight) / 2;
        
        // 1. Рисуем фон журнала
        guiGraphics.blit(BG_TEXTURE, x, y, 0, 0, bgWidth, bgHeight, bgWidth, bgHeight);
        
        // 2. Заголовок
        guiGraphics.drawCenteredString(this.font, "§6§lСПИСОК ЗАДАНИЙ", x + 65, y + 15, 0xFFFFFF);

        // 3. Если квест выбран — рисуем детали в правой части
        if (selectedQuest != null) {
            renderQuestDetails(guiGraphics, x + 130, y + 20);
        } else {
            guiGraphics.drawString(this.font, "§8Выберите квест...", x + 140, y + 80, 0xFFFFFF);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderQuestDetails(GuiGraphics guiGraphics, int x, int y) {
        // Название выбранного квеста
        guiGraphics.drawString(this.font, "§e" + selectedQuest.title, x + 10, y + 10, 0xFFFFFF);
        
        // Описание (с переносом строк)
        guiGraphics.drawWordWrap(this.font, Component.literal("§f" + selectedQuest.description), 
            x + 10, y + 30, bgWidth / 2 - 20, 0xFFFFFF);
        
        // Задачи
        int taskY = y + 80;
        guiGraphics.drawString(this.font, "§6Задачи:", x + 10, taskY, 0xFFFFFF);
        
        for (QuestData.QuestTask task : selectedQuest.tasks) {
            taskY += 12;
            String status = task.current_amount >= task.required_amount ? "§a✔ " : "§7- ";
            guiGraphics.drawString(this.font, status + task.description + " (" + task.current_amount + "/" + task.required_amount + ")", 
                x + 10, taskY, 0xFFFFFF);
        }
    }
}