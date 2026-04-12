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
    
    // Логический размер интерфейса на экране (соотношение 3:2)
    private final int guiWidth = 300; 
    private final int guiHeight = 200;
    
    // РЕАЛЬНЫЙ размер твоего файла картинки в пикселях. 
    // Сделай саму картинку journal_bg.png ровно 600x400 пикселей!
    private final int texWidth = 600;
    private final int texHeight = 400;

    private QuestData selectedQuest = null;

    public QuestJournalScreen() {
        super(Component.literal("Журнал заданий"));
    }

    @Override
    protected void init() {
        int x = (this.width - guiWidth) / 2;
        int y = (this.height - guiHeight) / 2;
        
        // Координаты для левой колонки (список квестов)
        int buttonX = x + 15; // Чуть отодвинул от левого края
        int buttonY = y + 35;
        int buttonWidth = 110; 

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
        
        int x = (this.width - guiWidth) / 2;
        int y = (this.height - guiHeight) / 2;
        
        // Отрисовка фона (теперь пропорции gui (300x200) и tex (600x400) совпадают)
        guiGraphics.blit(BG_TEXTURE, x, y, 0, 0, guiWidth, guiHeight, texWidth, texHeight);
        
        // Левая панель: Заголовок
        guiGraphics.drawCenteredString(this.font, "§6§lЗАДАНИЯ", x + 70, y + 15, 0xFFFFFF);
        
        if (QuestManager.getActiveQuests().isEmpty()) {
            guiGraphics.drawCenteredString(this.font, "§8Список пуст...", x + 70, y + 80, 0xFFFFFF);
        }

        // Отрисовка кнопок
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Правая панель: Детали квеста
        int rightPaneX = x + 150; // Центр правой страницы
        int rightPaneY = y + 15;
        
        if (selectedQuest != null) {
            // Заголовок квеста
            guiGraphics.drawCenteredString(this.font, "§e" + selectedQuest.title, rightPaneX + 65, rightPaneY, 0xFFFFFF);
            
            // Описание квеста с переносом строк
            guiGraphics.drawWordWrap(this.font, Component.literal("§7" + selectedQuest.description), rightPaneX, rightPaneY + 20, 130, 0xDDDDDD);

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
            if (!QuestManager.getActiveQuests().isEmpty()) {
                guiGraphics.drawCenteredString(this.font, "§8Выберите квест слева", rightPaneX + 65, y + 90, 0xFFFFFF);
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false; 
    }
}