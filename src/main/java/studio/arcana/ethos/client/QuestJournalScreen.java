package studio.arcana.ethos.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import studio.arcana.ethos.EthosCore;
import studio.arcana.ethos.data.QuestData;
import studio.arcana.ethos.logic.QuestManager;

public class QuestJournalScreen extends Screen {
    // Путь к текстуре фона журнала (проверь, чтобы был без : после MODID, если используешь 2 аргумента)
    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(EthosCore.MODID, "textures/gui/journal_bg.png");
    
    // Логический размер интерфейса на экране (подгоняем под стандартный размер GUI)
    private final int guiWidth = 300; 
    private final int guiHeight = 200;
    
    // Реальный размер твоего файла картинки, чтобы Minecraft правильно её сжал
    private final int texWidth = 1280;
    private final int texHeight = 576;

    // Переменная для хранения выбранного квеста
    private QuestData selectedQuest = null;

    public QuestJournalScreen() {
        super(Component.literal("Журнал заданий"));
    }

    @Override
    protected void init() {
        int x = (this.width - guiWidth) / 2;
        int y = (this.height - guiHeight) / 2;
        
        // Координаты для левой колонки (список квестов)
        int buttonX = x + 10;
        int buttonY = y + 30;
        int buttonWidth = 120; // Кнопки делаем по ширине левой панели

        this.clearWidgets(); // Защита от дублирования кнопок при ресайзе окна

        for (QuestData quest : QuestManager.getActiveQuests()) {
            this.addRenderableWidget(new EthosButton(
                buttonX, 
                buttonY, 
                buttonWidth, 
                20, 
                Component.literal(quest.title), 
                (button) -> {
                    // Теперь мы не открываем новый экран, а просто обновляем правую панель
                    this.selectedQuest = quest;
                }
            ));
            buttonY += 24; // Шаг между кнопками
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 1. Затемнение заднего плана
        this.renderBackground(guiGraphics);
        
        int x = (this.width - guiWidth) / 2;
        int y = (this.height - guiHeight) / 2;
        
        // 2. Отрисовка фона с правильным масштабированием
        guiGraphics.blit(BG_TEXTURE, x, y, 0, 0, guiWidth, guiHeight, texWidth, texHeight);
        
        // 3. Левая панель: Заголовок и проверка на пустоту
        guiGraphics.drawCenteredString(this.font, "§6§lЗАДАНИЯ", x + 70, y + 10, 0xFFFFFF);
        
        if (QuestManager.getActiveQuests().isEmpty()) {
            guiGraphics.drawCenteredString(this.font, "§8Список пуст...", x + 70, y + 80, 0xFFFFFF);
        }

        // 4. Отрисовка кнопок (вызываем ДО текста правой панели, чтобы всё было ровно)
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // 5. Правая панель: Детали квеста (замена QuestDetailsScreen)
        int rightPaneX = x + 140; // Отступ для правой половины интерфейса
        int rightPaneY = y + 10;
        
        if (selectedQuest != null) {
            // Заголовок квеста
            guiGraphics.drawCenteredString(this.font, "§e" + selectedQuest.title, rightPaneX + 75, rightPaneY, 0xFFFFFF);
            
            // Описание квеста с переносом строк (макс ширина 140)
            guiGraphics.drawWordWrap(this.font, Component.literal("§7" + selectedQuest.description), rightPaneX, rightPaneY + 20, 140, 0xDDDDDD);

            // Отрисовка задач
            int taskY = rightPaneY + 80;
            guiGraphics.drawString(this.font, "§6Задачи:", rightPaneX, taskY, 0xFFFFFF);
            
            if (selectedQuest.objectives != null) {
                for (QuestData.Objective obj : selectedQuest.objectives) {
                    taskY += 15;
                    boolean isDone = obj.current_progress >= obj.amount_required;
                    String status = isDone ? "§a✔ " : "§8- ";
                    String progress = " §8(" + obj.current_progress + "/" + obj.amount_required + ")";
                    
                    guiGraphics.drawString(this.font, status + "§f" + obj.description + progress, rightPaneX, taskY, 0xFFFFFF);
                }
            }
        } else {
            // Если квест ещё не выбран кликом
            if (!QuestManager.getActiveQuests().isEmpty()) {
                guiGraphics.drawCenteredString(this.font, "§8Выберите квест слева", rightPaneX + 75, y + 90, 0xFFFFFF);
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false; 
    }
}