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

    // Основной фон кнопки: 256x64 px
    private final ResourceLocation idle  = ResourceLocation.fromNamespaceAndPath(EthosCore.MODID, "textures/gui/dialogue_btn_idle.png");
    // Hover-вариант: тот же размер, чуть светлее
    private final ResourceLocation hover = ResourceLocation.fromNamespaceAndPath(EthosCore.MODID, "textures/gui/dialogue_btn_hover.png");

    public DialogueButton(int x, int y, int width, int height, Component msg, OnPress onPress) {
        super(x, y, width, height, msg, onPress, DEFAULT_NARRATION);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        ResourceLocation texture = this.isHoveredOrFocused() ? hover : idle;

        // --- Растягиваем текстуру на всю кнопку ---
        // Текстура 256x64, рисуем её в (x, y, width, height)
        guiGraphics.blit(texture, this.getX(), this.getY(), 0, 0,
                this.width, this.height, 256, 64);

        // --- Текст ---
        guiGraphics.pose().pushPose();
        float scale = 1.0f; // без лишнего масштаба — Minecraft-шрифт сам читается хорошо
        guiGraphics.pose().scale(scale, scale, 1.0f);

        int color = this.isHoveredOrFocused() ? 0xFFFFA0 : 0xFFFFFF;

        int wrapWidth = this.width - 16; // поля 8px с каждой стороны
        List<FormattedCharSequence> lines = mc.font.split(this.getMessage(), wrapWidth);

        int totalTextHeight = lines.size() * (mc.font.lineHeight + 1);
        int startY = this.getY() + (this.height - totalTextHeight) / 2;

        for (FormattedCharSequence line : lines) {
            // Центрируем каждую строку по горизонтали внутри кнопки
            int lineW = mc.font.width(line);
            int lineX = this.getX() + (this.width - lineW) / 2;
            guiGraphics.drawString(mc.font, line, lineX, startY, color, true);
            startY += mc.font.lineHeight + 1;
        }

        guiGraphics.pose().popPose();
    }
}
