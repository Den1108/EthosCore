package studio.arcana.ethos.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import studio.arcana.ethos.data.QuestData;
import studio.arcana.ethos.logic.QuestManager;

public class QuestJournalScreen extends Screen {
    public QuestJournalScreen() {
        super(Component.literal("Журнал заданий"));
    }

    @Override
    protected void init() {
        int y = 40;
        for (QuestData quest : QuestManager.getActiveQuests()) {
            this.addRenderableWidget(Button.builder(Component.literal(quest.title), (button) -> {
                this.minecraft.setScreen(new QuestDetailsScreen(quest));
            }).bounds(this.width / 2 - 100, y, 200, 20).build());
            y += 25;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, "§6§lВАШИ ЗАДАНИЯ", this.width / 2, 20, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}