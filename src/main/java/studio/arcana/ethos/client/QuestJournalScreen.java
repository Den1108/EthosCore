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
        
        int buttonX = x + 6; 
        int buttonY = y + 35;
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
        
        guiGraphics.blit(BG_TEXTURE, x, y, 0, 0, bgWidth, bgHeight, 300, 200);
        
        guiGraphics.drawCenteredString(this.font, "§6§lЗАДАНИЯ", x + 75, y + 15, 0xFFFFFF);

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // --- ПРАВАЯ СТРАНИЦА ---
        // Смещаем rightX чуть вправо (на 166), чтобы текст не лип к сгибу
        int rightX = x + 166; 
        int rightY = y + 15;
        
        if (selectedQuest != null) {
            // 1. НАЗВАНИЕ: Центрируем относительно правой страницы. 
            // Ширина правой страницы ~130 пикселей, значит центр на +65 от rightX.
            guiGraphics.drawCenteredString(this.font, "§e" + selectedQuest.title, rightX + 60, rightY, 0xFFFFFF);
            
            // 2. ОПИСАНИЕ: Теперь рисуется ровно от rightX
            // Ширину делаем 120, чтобы текст не вылез за правую рамку (166 + 120 = 286 пиксель)
            guiGraphics.drawWordWrap(this.font, Component.literal("§7" + selectedQuest.description), rightX, rightY + 22, 120, 0xDDDDDD);
            
            // 3. ЗАДАЧИ: Тоже от rightX
            int taskY = rightY + 80;
            guiGraphics.drawString(this.font, "§6Задачи:", rightX, taskY, 0xFFFFFF);
            
            if (selectedQuest.objectives != null) {
                for (QuestData.Objective obj : selectedQuest.objectives) {
                    taskY += 12;
                    String status = (obj.current_progress >= obj.amount_required) ? "§a✔ " : "§8- ";
                    guiGraphics.drawString(this.font, status + "§f" + obj.description, rightX, taskY, 0xFFFFFF);
                }
            }
        } else if (!QuestManager.getActiveQuests().isEmpty()) {
            guiGraphics.drawCenteredString(this.font, "§8Выберите задание", rightX + 60, y + 90, 0x888888);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false; 
    }
}