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
        
        // Кнопки: x + 8 (запас от рамки 3px). 
        // При ширине 135 они закончатся на 143 пикселе (аккурат перед твоей рамкой сгиба 144)
        int buttonX = x + 8; 
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
        
        // Отрисовка фона 300x200 из файла 300x200
        guiGraphics.blit(BG_TEXTURE, x, y, 0, 0, bgWidth, bgHeight, 300, 200);
        
        // Заголовок левой страницы (центр страницы — 75 пиксель)
        guiGraphics.drawCenteredString(this.font, "§6§lЗАДАНИЯ", x + 75, y + 15, 0xFFFFFF);

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Правая страница (начало текста с 158 пикселя, сразу после рамки сгиба)
        int rightX = x + 158; 
        int rightY = y + 15;
        
        if (selectedQuest != null) {
            // Название квеста по центру правой страницы (158 + 68 = ~226 пиксель холста)
            guiGraphics.drawCenteredString(this.font, "§e" + selectedQuest.title, rightX + 68, rightY, 0xFFFFFF);
            
            // Описание. Ширина 130, чтобы точно не упереться в правую рамку (297)
            guiGraphics.drawWordWrap(this.font, Component.literal("§7" + selectedQuest.description), rightX, rightY + 22, 130, 0xDDDDDD);
            
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
            // Если квесты есть, но ни один не выбран
            guiGraphics.drawCenteredString(this.font, "§8Выберите задание", rightX + 68, y + 90, 0x888888);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false; 
    }
}