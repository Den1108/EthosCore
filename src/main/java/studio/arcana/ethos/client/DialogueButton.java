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

    // Масштаб текста на кнопке
    private static final float TEXT_SCALE = 1.7f;

    // Левый отступ текста внутри кнопки (в экранных пикселях)
    private static final int TEXT_PADDING_LEFT = 12;

    // ── 9-slice параметры ──────────────────────────────────────────────────────
    // Размер текстурного файла
    private static final int TEX_W = 256;
    private static final int TEX_H = 64;
    // Размер угловых/боковых частей в пикселях текстуры
    // Поменяйте CORNER под реальный размер углов вашей текстуры
    private static final int CORNER = 16;
    // ──────────────────────────────────────────────────────────────────────────

    public DialogueButton(int x, int y, int width, int height, Component msg, OnPress onPress) {
        super(x, y, width, height, msg, onPress, DEFAULT_NARRATION);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        ResourceLocation texture = this.isHoveredOrFocused() ? hover : idle;

        // ── Рисуем кнопку через 9-slice ──────────────────────────────────────
        blit9Slice(guiGraphics, texture, this.getX(), this.getY(), this.width, this.height);

        // ── Текст — выравнивание по левому краю с переносом слов ─────────────
        int color = this.isHoveredOrFocused() ? 0xFFFFA0 : 0xFFFFFF;

        int wrapWidth = (int) ((this.width - TEXT_PADDING_LEFT * 2) / TEXT_SCALE);
        List<FormattedCharSequence> lines = mc.font.split(this.getMessage(), wrapWidth);

        int totalTextHeight = (int) ((lines.size() * (mc.font.lineHeight + 1)) * TEXT_SCALE);
        int startY = this.getY() + (this.height - totalTextHeight) / 2;

        // Левый отступ в пространстве масштаба
        int scaledPadLeft = (int) (TEXT_PADDING_LEFT / TEXT_SCALE);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX(), startY, 0);
        guiGraphics.pose().scale(TEXT_SCALE, TEXT_SCALE, 1.0f);

        int scaledLineHeight = mc.font.lineHeight + 1;
        int drawY = 0;

        for (FormattedCharSequence line : lines) {
            // Рисуем от левого края (с отступом), а не по центру
            guiGraphics.drawString(mc.font, line, scaledPadLeft, drawY, color, true);
            drawY += scaledLineHeight;
        }

        guiGraphics.pose().popPose();
    }

    /**
     * 9-slice blit: углы не масштабируются, центральная часть тянется по X и Y.
     *
     * Схема текстуры (256 × 64, угол = CORNER px):
     *
     *  ┌──────┬──────────────────┬──────┐  ▲
     *  │  TL  │       TC         │  TR  │  CORNER
     *  ├──────┼──────────────────┼──────┤  ▼
     *  │  ML  │       MC         │  MR  │  <- TEX_H - CORNER*2 (средняя полоса)
     *  ├──────┼──────────────────┼──────┤  ▲
     *  │  BL  │       BC         │  BR  │  CORNER
     *  └──────┴──────────────────┴──────┘  ▼
     *   CORNER   TEX_W-CORNER*2   CORNER
     *
     * Если у вашей текстуры нет нижней полосы (только верх + середина),
     * выставьте нижний CORNER = 0 и скорректируйте UV ниже.
     */
    private void blit9Slice(GuiGraphics g, ResourceLocation tex,
                            int dx, int dy, int dw, int dh) {
        int c = CORNER;
        int midTexW = TEX_W - c * 2;
        int midTexH = TEX_H - c * 2;
        int midDstW = dw - c * 2;
        int midDstH = dh - c * 2;

        // Углы (u, v, ширина_в_текстуре, высота_в_текстуре → рисуем 1:1 по пикселям)
        // Верхний левый
        blitRegion(g, tex, dx,           dy,           c,       c,       0,       0,       c,       c);
        // Верхний правый
        blitRegion(g, tex, dx+dw-c,      dy,           c,       c,       TEX_W-c, 0,       c,       c);
        // Нижний левый
        blitRegion(g, tex, dx,           dy+dh-c,      c,       c,       0,       TEX_H-c, c,       c);
        // Нижний правый
        blitRegion(g, tex, dx+dw-c,      dy+dh-c,      c,       c,       TEX_W-c, TEX_H-c, c,       c);

        // Верхняя полоса (растягивается по X)
        blitRegion(g, tex, dx+c,         dy,           midDstW, c,       c,       0,       midTexW, c);
        // Нижняя полоса (растягивается по X)
        blitRegion(g, tex, dx+c,         dy+dh-c,      midDstW, c,       c,       TEX_H-c, midTexW, c);
        // Левая полоса (растягивается по Y)
        blitRegion(g, tex, dx,           dy+c,         c,       midDstH, 0,       c,       c,       midTexH);
        // Правая полоса (растягивается по Y)
        blitRegion(g, tex, dx+dw-c,      dy+c,         c,       midDstH, TEX_W-c, c,       c,       midTexH);
        // Центр (растягивается по X и Y)
        blitRegion(g, tex, dx+c,         dy+c,         midDstW, midDstH, c,       c,       midTexW, midTexH);
    }

    /** Тонкая обёртка над guiGraphics.blit с явным указанием UV и размеров текстуры. */
    private void blitRegion(GuiGraphics g, ResourceLocation tex,
                            int dstX, int dstY, int dstW, int dstH,
                            int u, int v, int uW, int uH) {
        if (dstW <= 0 || dstH <= 0) return;
        g.blit(tex, dstX, dstY, dstW, dstH, u, v, uW, uH, TEX_W, TEX_H);
    }
}
