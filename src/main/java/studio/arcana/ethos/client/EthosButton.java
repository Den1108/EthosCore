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

    // Конструктор остается приватным для использования статических фабричных методов
    private EthosButton(int x, int y, int width, int height, Component msg, ResourceLocation idle, ResourceLocation hover, int texW, int texH, OnPress onPress) {
        super(x, y, width, height, msg, onPress, DEFAULT_NARRATION);
        this.idle = idle;
        this.hover = hover;
        this.texW = texW;
        this.texH = texH;
    }

    // --- СТАТИЧЕСКИЕ МЕТОДЫ ---

    /**
     * Кнопка для диалогов с динамической высотой и крупным шрифтом
     */
    public static EthosButton flexibleDialogue(int x, int y, int width, int height, Component msg, OnPress onPress) {
        return new EthosButton(x, y, width, height, msg, 
            ResourceLocation.fromNamespaceAndPath(EthosCore.MODID, "textures/gui/dialogue_btn_idle.png"),
            ResourceLocation.fromNamespaceAndPath(EthosCore.MODID, "textures/gui/dialogue_btn_hover.png"),
            256, 256, 
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

    // --- ЛОГИКА ОТРИСОВКИ ---

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation texture = this.isHoveredOrFocused() ? hover : idle;
        Minecraft mc = Minecraft.getInstance();
        
        // 1. Отрисовка подложки (текстура растягивается под размер кнопки)
        guiGraphics.blit(texture, this.getX(), this.getY(), 0, 0, this.width, this.height, this.texW, this.texH);
        
        // 2. Отрисовка текста с масштабированием
        guiGraphics.pose().pushPose();
        
        // Коэффициент масштаба для текста внутри кнопки (1.2 = на 20% больше)
        float textScale = 1.2f; 
        guiGraphics.pose().scale(textScale, textScale, 1.0f);

        // Цвет: Желтоватый при наведении, белый в покое
        int color = this.isHoveredOrFocused() ? 0xFFFFA0 : 0xFFFFFF;
        
        // Расчет ширины для переноса с учетом масштаба
        int wrapWidth = (int) ((this.width - 20) / textScale);
        List<FormattedCharSequence> lines = mc.font.split(this.getMessage(), wrapWidth);
        
        // Общая высота текстового блока
        int totalTextHeight = lines.size() * 10;
        
        // Координаты отрисовки (пересчитаны под масштаб)
        // Делим координаты X и Y на масштаб, так как pose().scale влияет на всё положение
        float renderX = (this.getX() + 10) / textScale;
        float renderY = (this.getY() + (this.height - (totalTextHeight * textScale)) / 2 + 2) / textScale;

        for (FormattedCharSequence line : lines) {
            // Рисуем с тенью (true) для лучшей читаемости
            guiGraphics.drawString(mc.font, line, (int) renderX, (int) renderY, color, true);
            renderY += 10; // Смещение вниз для следующей строки
        }
        
        guiGraphics.pose().popPose();
    }
}