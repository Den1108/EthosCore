package studio.arcana.ethos.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import studio.arcana.ethos.EthosCore;
import java.util.List;

public class DialogueButton extends Button {

    private final ResourceLocation idle  = ResourceLocation.fromNamespaceAndPath(EthosCore.MODID, "textures/gui/dialogue_btn_idle.png");
    private final ResourceLocation hover = ResourceLocation.fromNamespaceAndPath(EthosCore.MODID, "textures/gui/dialogue_btn_hover.png");

    private final float textScale; // Теперь масштаб хранится для каждой кнопки
    private static final int TEXT_PADDING_LEFT = 12;

    private static final int TEX_W = 256;
    private static final int TEX_H = 64;
    private static final int CORNER = 16;

    // ОБНОВЛЕННЫЙ КОНСТРУКТОР (добавлен float textScale)
    public DialogueButton(int x, int y, int width, int height, Component msg, float textScale, OnPress onPress) {
        super(x, y, width, height, msg, onPress, DEFAULT_NARRATION);
        this.textScale = textScale;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        ResourceLocation texture = this.isHoveredOrFocused() ? hover : idle;

        blit9Slice(guiGraphics, texture, this.getX(), this.getY(), this.width, this.height);

        int color = this.isHoveredOrFocused() ? 0xFFFFA0 : 0xFFFFFF;

        // Используем переданный масштаб
        int wrapWidth = (int) ((this.width - TEXT_PADDING_LEFT * 2) / textScale);
        List<FormattedCharSequence> lines = mc.font.split(this.getMessage(), wrapWidth);

        int totalTextHeight = (int) ((lines.size() * (mc.font.lineHeight + 1)) * textScale);
        int startY = this.getY() + (this.height - totalTextHeight) / 2;
        int scaledPadLeft = (int) (TEXT_PADDING_LEFT / textScale);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX(), startY, 0);
        guiGraphics.pose().scale(textScale, textScale, 1.0f);

        int scaledLineHeight = mc.font.lineHeight + 1;
        int drawY = 0;

        for (FormattedCharSequence line : lines) {
            guiGraphics.drawString(mc.font, line, scaledPadLeft, drawY, color, true);
            drawY += scaledLineHeight;
        }

        guiGraphics.pose().popPose();
    }

    private void blit9Slice(GuiGraphics g, ResourceLocation tex, int dx, int dy, int dw, int dh) {
        int c = CORNER;
        int midTexW = TEX_W - c * 2;
        int midTexH = TEX_H - c * 2;
        int midDstW = dw - c * 2;
        int midDstH = dh - c * 2;

        blitRegion(g, tex, dx,           dy,           c,       c,       0,       0,       c,       c);
        blitRegion(g, tex, dx+dw-c,      dy,           c,       c,       TEX_W-c, 0,       c,       c);
        blitRegion(g, tex, dx,           dy+dh-c,      c,       c,       0,       TEX_H-c, c,       c);
        blitRegion(g, tex, dx+dw-c,      dy+dh-c,      c,       c,       TEX_W-c, TEX_H-c, c,       c);

        blitRegion(g, tex, dx+c,         dy,           midDstW, c,       c,       0,       midTexW, c);
        blitRegion(g, tex, dx+c,         dy+dh-c,      midDstW, c,       c,       TEX_H-c, midTexW, c);
        blitRegion(g, tex, dx,           dy+c,         c,       midDstH, 0,       c,       c,       midTexH);
        blitRegion(g, tex, dx+dw-c,      dy+c,         c,       midDstH, TEX_W-c, c,       c,       midTexH);
        blitRegion(g, tex, dx+c,         dy+c,         midDstW, midDstH, c,       c,       midTexW, midTexH);
    }

    private void blitRegion(GuiGraphics g, ResourceLocation tex, int dstX, int dstY, int dstW, int dstH, int u, int v, int uW, int uH) {
        if (dstW <= 0 || dstH <= 0) return;
        g.blit(tex, dstX, dstY, dstW, dstH, u, v, uW, uH, TEX_W, TEX_H);
    }
}