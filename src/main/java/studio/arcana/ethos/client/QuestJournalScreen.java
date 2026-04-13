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
        
        this.clearWidgets();

        // 1. Кнопки списка квестов (слева)
        int buttonX = x + 6; 
        int buttonY = y + 35;
        for (QuestData quest : QuestManager.getActiveQuests()) {
            this.addRenderableWidget(new JournalButton(buttonX, buttonY, 135, 20, Component.literal(quest.title), (button) -> {
                this.selectedQuest = quest;
                this.init(); // Переинициализируем, чтобы появилась кнопка отслеживания для выбранного квеста
            }));
            buttonY += 24;
        }

        // 2. Кнопка "Скрыть/Отслеживать" (справа)
        if (selectedQuest != null) {
            int rightX = x + 166;
            // Можно добавить цвета прямо в текст кнопки
            String btnText = selectedQuest.is_tracked ? "§cСкрыть" : "§aОтслеживать";
    
            // Используем твой кастомный класс вместо ванильного билдера
            this.addRenderableWidget(new TrackButton(
                rightX + 20, y + 160, 80, 20, 
                Component.literal(btnText), 
                (btn) -> {
                    selectedQuest.is_tracked = !selectedQuest.is_tracked;
                    QuestManager.saveProgress();
                    this.init(); // Перерисовываем экран, чтобы кнопка обновила текст
                }
            ));
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        int x = (this.width - bgWidth) / 2;
        int y = (this.height - bgHeight) / 2;
        
        guiGraphics.blit(BG_TEXTURE, x, y, 0, 0, bgWidth, bgHeight, 300, 200);
        guiGraphics.drawCenteredString(this.font, "§6§lЗАДАНИЯ", x + 75, y + 15, 0xFFFFFF);

        // ВАЖНО: Рисуем виджеты (кнопки)
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Отрисовка текста описания (без создания кнопок!)
        if (selectedQuest != null) {
            int rightX = x + 166;
            int rightY = y + 15;
            guiGraphics.drawCenteredString(this.font, "§e" + selectedQuest.title, rightX + 60, rightY, 0xFFFFFF);
            guiGraphics.drawWordWrap(this.font, Component.literal("§7" + selectedQuest.description), rightX, rightY + 22, 120, 0xDDDDDD);
            
            int taskY = rightY + 80;
            guiGraphics.drawString(this.font, "§6Задачи:", rightX, taskY, 0xFFFFFF);
            for (QuestData.Objective obj : selectedQuest.objectives) {
                taskY += 12;
                String status = (obj.current_progress >= obj.amount_required) ? "§a✔ " : "§8- ";
                guiGraphics.drawString(this.font, status + "§f" + obj.description, rightX, taskY, 0xFFFFFF);
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false; 
    }
}