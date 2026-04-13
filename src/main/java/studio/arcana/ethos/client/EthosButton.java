package studio.arcana.ethos.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import studio.arcana.ethos.EthosCore;

public class EthosButton extends Button {
    private final ResourceLocation idle;
    private final ResourceLocation hover;
    private final int texW;
    private final int texH;

    // Приватный конструктор, чтобы использовать методы ниже
    private EthosButton(int x, int y, int width, int height, Component msg, ResourceLocation idle, ResourceLocation hover, int texW, int texH, OnPress onPress) {
        super(x, y, width, height, msg, onPress, DEFAULT_NARRATION);
        this.idle = idle;
        this.hover = hover;
        this.texW = texW;
        this.texH = texH;
    }

    // --- СТАТИЧЕСКИЕ МЕТОДЫ (ТВОЯ ЗОЛОТАЯ СЕРЕДИНА) ---

    public static EthosButton dialogue(int x, int y, Component msg, OnPress onPress) {
        return new EthosButton(x, y, 180, 20, msg, 
            ResourceLocation.fromNamespaceAndPath(EthosCore.MODID, "textures/gui/dialogue_btn_idle.png"),
            ResourceLocation.fromNamespaceAndPath(EthosCore.MODID, "textures/gui/dialogue_btn_hover.png"),
            180, 20, onPress);
    }

    public static EthosButton journal(int x, int y, Component msg, OnPress onPress) {
        return new EthosButton(x, y, 135, 20, msg, 
            ResourceLocation.fromNamespaceAndPath(EthosCore.MODID, "textures/gui/journal_btn_idle.png"),
            ResourceLocation.fromNamespaceAndPath(EthosCore.MODID, "textures/gui/journal_btn_hover.png"),
            135, 20, onPress);
    }

    public static EthosButton track(int x, int y, Component msg, OnPress onPress) {
        return new EthosButton(x, y, 80, 20, msg, 
            ResourceLocation.fromNamespaceAndPath(EthosCore.MODID, "textures/gui/track_btn_idle.png"),
            ResourceLocation.fromNamespaceAndPath(EthosCore.MODID, "textures/gui/track_btn_hover.png"),
            80, 20, onPress);
    }

    // --- ЛОГИКА ОТРИСОВКИ ---

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation texture = this.isHoveredOrFocused() ? hover : idle;
        
        // Отрисовка без растягивания (используем переданные размеры текстуры)
        guiGraphics.blit(texture, this.getX(), this.getY(), 0, 0, this.width, this.height, this.texW, this.texH);
        
        int color = this.isHoveredOrFocused() ? 0xFFFFA0 : 0xFFFFFF;
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, this.getMessage(), 
            this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, color);
    }
}