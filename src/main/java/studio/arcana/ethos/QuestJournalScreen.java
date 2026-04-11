package studio.arcana.ethos;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class QuestJournalScreen extends Screen {
    public QuestJournalScreen() {
        super(Component.literal("Журнал заданий"));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        int x = this.width / 2 - 100;
        int y = 40;

        guiGraphics.drawCenteredString(this.font, "§6§lЖУРНАЛ ЗАДАНИЙ", this.width / 2, 20, 0xFFFFFF);

        // Активные квесты
        guiGraphics.drawString(this.font, "§eАктивные:", x, y, 0xFFFFFF);
        int currentY = y + 15;
        for (String q : QuestManager.getActiveQuests()) {
            guiGraphics.drawString(this.font, "§7- " + q, x + 5, currentY, 0xFFFFFF);
            currentY += 12;
        }

        // Завершенные квесты
        currentY += 10;
        guiGraphics.drawString(this.font, "§aЗавершено:", x, currentY, 0xFFFFFF);
        currentY += 15;
        for (String q : QuestManager.getCompletedQuests()) {
            guiGraphics.drawString(this.font, "§8- " + q, x + 5, currentY, 0xFFFFFF);
            currentY += 12;
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}