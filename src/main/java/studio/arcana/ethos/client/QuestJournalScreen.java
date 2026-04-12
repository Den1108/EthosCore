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
    
    private final int bgWidth = 300; 
    private final int bgHeight = 200;

    private QuestData selectedQuest = null;

    public QuestJournalScreen() {
        super(Component.literal("Журнал заданий"));
    }

    @Override
    protected void init() {
        int x = (this.width - bgWidth) / 2;
        int y = (this.height - bgHeight) / 2;
        
        // Кнопки: отступаем 8 пикселей от края (3 рамка + 5 запас)
        int buttonX = x + 8; 
        int buttonY = y + 35;
        // Ширина кнопки 135, чтобы она заканчивалась на 143 (не доходя до сгиба)
        int buttonWidth = 135; 

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
        
        // Отрисовка фона 300x200
        guiGraphics.blit(BG_TEXTURE, x, y, 0, 0, bgWidth, bgHeight, 300, 200);
        
        // Заголовок левой страницы
        guiGraphics.drawCenteredString(this.font, "§6§lЗАДАНИЯ", x + 75, y + 15, 0xFFFFFF);

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Правая страница (начало текста с 158 пикселя)
        int rightX = x + 158; 
        int rightY = y + 15;
        
        if (selectedQuest != null) {
            // Название квеста
            guiGraphics.drawCenteredString(this.font, "§e" + selectedQuest.title, rightX + 65, rightY, 0xFFFFFF);
            
            // Описание (ширина 132, чтобы не наехать на правую рамку 297)
            guiGraphics.drawWordWrap(this.font, Component.literal("§7" + selectedQuest.description), rightX, rightY + 22, 132, 0xDDDDDD);
            
            int taskY = rightY + 80;
            guiGraphics.drawString(this.font, "§6Задачи:", rightX, taskY, 0xFFFFFF);
            
            if (selectedQuest.objectives != null) {
                for (QuestData.Objective obj : selectedQuest.objectives) {
                    taskY += 12;
                    String status = (obj.current_progress >= obj.amount_required) ? "§a✔ " : "§8- ";
                    guiGraphics.drawString(this.font, status + "§f" + obj.description, rightX, taskY, 0xFFFFFF);
                }
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false; 
    }
}