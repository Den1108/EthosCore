package studio.arcana.ethos.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import studio.arcana.ethos.EthosCore;

public class DialogueButton extends Button {
    // Текстуры 180x20
    private static final ResourceLocation IDLE = new ResourceLocation(EthosCore.MODID, "textures/gui/dialogue_btn_idle.png");
    private static final ResourceLocation HOVER = new ResourceLocation(EthosCore.MODID, "textures/gui/dialogue_btn_hover.png");

    public DialogueButton(int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation texture = this.isHoveredOrFocused() ? HOVER : IDLE;
        
        // Рисуем текстуру 1:1. 180, 20 — размер области, которую берем из файла.
        guiGraphics.blit(texture, this.getX(), this.getY(), 0, 0, this.width, this.height, 180, 20);
        
        int color = this.isHoveredOrFocused() ? 0xFFFFA0 : 0xFFFFFF;
        guiGraphics.drawCenteredString(net.minecraft.client.Minecraft.getInstance().font, this.getMessage(), 
            this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, color);
    }
}