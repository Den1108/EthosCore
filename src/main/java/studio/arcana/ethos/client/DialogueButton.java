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

    // Масштаб текста на кнопке (1.5 = крупнее стандартного)
    private static final float TEXT_SCALE = 1.5f;

    public DialogueButton(int x, int y, int width, int height, Component msg, OnPress onPress) {
        super(x, y, width, height, msg, onPress, DEFAULT_NARRATION);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        ResourceLocation texture = this.isHoveredOrFocused() ? hover : idle;

        // Растягиваем текстуру на всю кнопку
        guiGraphics.blit(texture, this.getX(), this.getY(), 0, 0,
                this.width, this.height, 256, 64);

        int color = this.isHoveredOrFocused() ? 0xFFFFA0 : 0xFFFFFF;

        // Ширина обёртки с учётом масштаба — текст "думает" что места меньше
        int wrapWidth = (int) ((this.width - 16) / TEXT_SCALE);
        List<FormattedCharSequence> lines = mc.font.split(this.getMessage(), wrapWidth);

        // Реальная высота всего текстового блока в экранных пикселях
        int totalTextHeight = (int) ((lines.size() * (mc.font.lineHeight + 1)) * TEXT_SCALE);

        // Центрируем блок текста вертикально по середине кнопки
        int startY = this.getY() + (this.height - totalTextHeight) / 2;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX(), startY, 0);
        guiGraphics.pose().scale(TEXT_SCALE, TEXT_SCALE, 1.0f);

        // После translate+scale рисуем от (0,0) — координаты уже в пространстве масштаба
        int scaledLineHeight = mc.font.lineHeight + 1;
        int drawY = 0;

        for (FormattedCharSequence line : lines) {
            int lineW = mc.font.width(line);
            // Центрируем строку горизонтально внутри кнопки (ширина делится на scale)
            int lineX = (int) ((this.width - lineW * TEXT_SCALE) / 2 / TEXT_SCALE);
            guiGraphics.drawString(mc.font, line, lineX, drawY, color, true);
            drawY += scaledLineHeight;
        }

        guiGraphics.pose().popPose();
    }
}
