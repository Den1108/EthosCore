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
    
    // ЛОГИЧЕСКИЙ РАЗМЕР (размер на экране игрока)
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
        
        // Координаты кнопок (теперь они в логических единицах)
        int buttonX = x + 15; 
        int buttonY = y + 30;
        int buttonWidth = 115; 

        this.clearWidgets();

        for (QuestData quest : QuestManager.getActiveQuests()) {
            this.addRenderableWidget(new EthosButton(
                buttonX, 
                buttonY, 
                buttonWidth, 
                18, // Чуть тоньше кнопки
                Component.literal(quest.title), 
                (button) -> {
                    this.selectedQuest = quest;
                }
            ));
            buttonY += 22;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        
        int x = (this.width - bgWidth) / 2;
        int y = (this.height - bgHeight) / 2;
        
        // ВАЖНЫЙ МОМЕНТ:
        // bgWidth/bgHeight (300x200) — сколько места займет на экране.
        // 600, 400 — реальный размер твоего PNG файла.
        // Minecraft сам аккуратно сожмет картинку, и она будет выглядеть четко.
        guiGraphics.blit(BG_TEXTURE, x, y, 0, 0, bgWidth, bgHeight, 600, 400);
        
        // Текст левой страницы
        guiGraphics.drawCenteredString(this.font, "§6§lЗАДАНИЯ", x + 72, y + 15, 0xFFFFFF);

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Текст правой страницы
        int rightPageX = x + 155; 
        int rightPageY = y + 15;
        
        if (selectedQuest != null) {
            // Название
            guiGraphics.drawCenteredString(this.font, "§e" + selectedQuest.title, rightPageX + 65, rightPageY, 0xFFFFFF);
            
            // Описание (ширина 130 логических единиц)
            guiGraphics.drawWordWrap(this.font, Component.literal("§7" + selectedQuest.description), rightPageX, rightPageY + 20, 130, 0xDDDDDD);
            
            int taskY = rightPageY + 75;
            guiGraphics.drawString(this.font, "§6Задачи:", rightPageX, taskY, 0xFFFFFF);
            
            if (selectedQuest.objectives != null) {
                for (QuestData.Objective obj : selectedQuest.objectives) {
                    taskY += 12;
                    String status = (obj.current_progress >= obj.amount_required) ? "§a✔ " : "§8- ";
                    guiGraphics.drawString(this.font, status + "§f" + obj.description, rightPageX, taskY, 0xFFFFFF);
                    
                    // Прогресс отдельной строкой или в той же
                    String progress = " §8(" + obj.current_progress + "/" + obj.amount_required + ")";
                    guiGraphics.drawString(this.font, progress, rightPageX + 100, taskY, 0xFFFFFF);
                }
            }
        }
    }
}