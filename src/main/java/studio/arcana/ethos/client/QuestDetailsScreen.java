package studio.arcana.ethos.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import studio.arcana.ethos.data.QuestData;

public class QuestDetailsScreen extends Screen {
    private final QuestData quest;

    public QuestDetailsScreen(QuestData quest) {
        super(Component.literal(quest.title));
        this.quest = quest;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        int x = this.width / 2 - 120;
        
        guiGraphics.drawCenteredString(this.font, "§e" + quest.title, this.width / 2, 30, 0xFFFFFF);
        guiGraphics.drawWordWrap(this.font, Component.literal("§7" + quest.description), x, 50, 240, 0xDDDDDD);

        int y = 90;
        guiGraphics.drawString(this.font, "Задачи:", x, y, 0xFFAA00);
        
        for (QuestData.Objective obj : quest.objectives) {
            y += 15;
            String progress = " §8(" + obj.current_progress + "/" + obj.amount_required + ")";
            guiGraphics.drawString(this.font, "§f- " + obj.description + progress, x + 5, y, 0xFFFFFF);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}