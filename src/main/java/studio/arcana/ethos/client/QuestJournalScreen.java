package studio.arcana.ethos.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import studio.arcana.ethos.EthosCore;
import studio.arcana.ethos.data.QuestData;
import studio.arcana.ethos.logic.QuestManager;

public class QuestJournalScreen extends Screen {
    // Используем правильный конструктор ResourceLocation, чтобы не было warnings
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
        
        int listX = x + 15;
        int listY = y + 40;
        int buttonWidth = 100;

        for (QuestData quest : QuestManager.getActiveQuests()) {
            this.addRenderableWidget(new EthosButton(
                listX, 
                listY, 
                buttonWidth, 
                20, 
                Component.literal(quest.title), 
                (button) -> {
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
        
        // Отрисовка фона
        guiGraphics.blit(BG_TEXTURE, x, y, 0, 0, bgWidth, bgHeight, bgWidth, bgHeight);
        
        // Заголовок списка
        guiGraphics.drawCenteredString(this.font, "§6§lЗАДАНИЯ", x + 65, y + 15, 0xFFFFFF);

        if (selectedQuest != null) {
            renderQuestDetails(guiGraphics, x + 130, y + 20);
        } else {
            guiGraphics.drawCenteredString(this.font, "§8Выберите квест", x + 200, y + 80, 0xFFFFFF);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderQuestDetails(GuiGraphics guiGraphics, int x, int y) {
        // Название
        guiGraphics.drawString(this.font, "§e" + selectedQuest.title, x + 10, y + 5, 0xFFFFFF);
        
        // Описание (уменьшил ширину, чтобы влезло в правую панель)
        guiGraphics.drawWordWrap(this.font, Component.literal("§f" + selectedQuest.description), 
            x + 10, y + 25, 120, 0xFFFFFF);
        
        int taskY = y + 85;
        guiGraphics.drawString(this.font, "§6Задачи:", x + 10, taskY, 0xFFFFFF);
        
        // ИСПРАВЛЕНИЕ: Используем objectives вместо tasks и QuestData.Objective вместо QuestTask
        if (selectedQuest.objectives != null) {
            for (QuestData.Objective obj : selectedQuest.objectives) {
                taskY += 12;
                boolean isDone = obj.current_progress >= obj.amount_required;
                String status = isDone ? "§a✔ " : "§7- ";
                String text = status + obj.description + " (" + obj.current_progress + "/" + obj.amount_required + ")";
                
                guiGraphics.drawString(this.font, text, x + 10, taskY, 0xFFFFFF);
            }
        }
    }
}