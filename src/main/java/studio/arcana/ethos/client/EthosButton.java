package studio.arcana.ethos.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import studio.arcana.ethos.EthosCore;
import java.util.List;

public class EthosButton extends Button {
    private final ResourceLocation idle;
    private final ResourceLocation hover;
    private final int texW;
    private final int texH;

    private EthosButton(int x, int y, int width, int height, Component msg, ResourceLocation idle, ResourceLocation hover, int texW, int texH, OnPress onPress) {
        super(x, y, width, height, msg, onPress, DEFAULT_NARRATION);
        this.idle = idle;
        this.hover = hover;
        this.texW = texW;
        this.texH = texH;
    }

    // --- НОВЫЙ МЕТОД ДЛЯ ГИБКИХ КНОПОК ---
    public static EthosButton flexibleDialogue(int x, int y, int width, int height, Component msg, OnPress onPress) {
        return new EthosButton(x, y, width, height, msg, 
            ResourceLocation.fromNamespaceAndPath(EthosCore.MODID, "textures/gui/dialogue_btn_idle.png"),
            ResourceLocation.fromNamespaceAndPath(EthosCore.MODID, "textures/gui/dialogue_btn_hover.png"),
            256, 256, // Размер файла текстуры (рекомендую 256x256)
            onPress);
    }

    public static EthosButton dialogue(int x, int y, Component msg, OnPress onPress) {
        return flexibleDialogue(x, y, 180, 20, msg, onPress);
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

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation texture = this.isHoveredOrFocused() ? hover : idle;
        Minecraft mc = Minecraft.getInstance();
        
        // 1. Отрисовка подложки
        guiGraphics.blit(texture, this.getX(), this.getY(), 0, 0, this.width, this.height, this.texW, this.texH);
        
        // 2. Отрисовка многострочного текста
        int color = this.isHoveredOrFocused() ? 0xFFFFA0 : 0xFFFFFF; // Желтоватый цвет при наведении
        
        List<FormattedCharSequence> lines = mc.font.split(this.getMessage(), this.width - 20);
        
        int textHeight = lines.size() * 10;
        int startY = this.getY() + (this.height - textHeight + 2) / 2;

        for (FormattedCharSequence line : lines) {
            // Изменено: рисуем текст с левого края (отступ 8 пикселей), а не по центру
            guiGraphics.drawString(mc.font, line, this.getX() + 8, startY, color, true);
            startY += 10;
        }
    }
}