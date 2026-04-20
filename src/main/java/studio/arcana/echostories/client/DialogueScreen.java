package studio.arcana.echostories.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import studio.arcana.echostories.EchoStories;
import java.util.List;

public class DialogueScreen extends Screen {

    private static final ResourceLocation DECO_LINE =
            ResourceLocation.fromNamespaceAndPath(EchoStories.MODID, "textures/gui/dialogue_name_line.png");
            
    private static final ResourceLocation DIALOGUE_BG =
            ResourceLocation.fromNamespaceAndPath(EchoStories.MODID, "textures/gui/dialogue_bg.png");

    private final String npcName;
    private final String dialogueText;
    private final List<DialogueOption> options;

    private static final int BTN_WIDTH   = 310;
    private static final int BTN_X       = 10;
    private static final int BTN_START_Y = 10;
    private static final int BTN_SPACING = 10;
    private static final int BTN_PAD_V   = 16;

    private static final float NAME_SCALE   = 1.9f;
    private static final float PHRASE_SCALE = 1.7f;

    // ВАЖНО: этот масштаб должен совпадать с TEXT_SCALE в DialogueButton!
    private static final float BTN_TEXT_SCALE = 1.7f;

    // Максимальная ширина фразы NPC для переноса (в пикселях экрана)
    private static final int PHRASE_MAX_WIDTH = 500;

    public DialogueScreen(String npcName, String dialogueText, List<DialogueOption> options) {
        super(Component.literal(npcName));
        this.npcName = npcName;
        this.dialogueText = dialogueText;
        this.options = options;
    }

    @Override
    protected void init() {
        int currentY = BTN_START_Y;
        for (int i = 0; i < options.size(); i++) {
            DialogueOption option = options.get(i);

            // Используем тот же масштаб и отступ, что в DialogueButton.renderWidget()
            // TEXT_PADDING_LEFT = 12 (с обеих сторон = 24)
            int wrapWidth = (int) ((BTN_WIDTH - 24) / BTN_TEXT_SCALE);
            List<FormattedCharSequence> lines = this.font.split(Component.literal(option.text), wrapWidth);

            // Высота = строки * высота строки * масштаб + вертикальные отступы
            int btnHeight = (int) (lines.size() * (this.font.lineHeight + 1) * BTN_TEXT_SCALE) + BTN_PAD_V * 2;

            this.addRenderableWidget(new DialogueButton(
                    BTN_X, currentY, BTN_WIDTH, btnHeight,
                    Component.literal(option.text),
                    (btn) -> {
                        option.action.run();
                        this.onClose();
                    }
            ));
            currentY += btnHeight + BTN_SPACING;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int screenW = this.width;
        int screenH = this.height;

        int lineW  = 220;
        int lineH  = 16;
        int lineX  = (screenW - lineW) / 2;

        int bottomMargin = 65;
        int gap = 6;

        // --- Разбиваем фразу NPC на строки с переносом ---
        int phraseWrapWidth = (int) (PHRASE_MAX_WIDTH / PHRASE_SCALE);
        List<FormattedCharSequence> phraseLines = this.font.split(
                Component.literal(dialogueText), phraseWrapWidth);
        int phraseH = (int) (phraseLines.size() * (this.font.lineHeight + 2) * PHRASE_SCALE);

        int nameH = (int) (this.font.lineHeight * NAME_SCALE);

        int bottomLineY = screenH - bottomMargin - lineH;
        int phraseY     = bottomLineY - gap - phraseH;
        int topLineY    = phraseY - gap - lineH;
        int nameY       = topLineY - gap - nameH;

        // --- Фон ---
        int bgPaddingTop = 15;
        int bgY = nameY - bgPaddingTop;
        int bgHeight = screenH - bgY;

        if (bgHeight > 0) {
            com.mojang.blaze3d.systems.RenderSystem.enableBlend();
            guiGraphics.blit(DIALOGUE_BG, 0, bgY, screenW, bgHeight, 0.0f, 0.0f, 1024, 256, 1024, 256);
            com.mojang.blaze3d.systems.RenderSystem.disableBlend();
        }

        // --- Имя NPC ---
        int nameRawW = this.font.width(npcName);
        int nameScaledW = (int) (nameRawW * NAME_SCALE);
        int nameX = (screenW - nameScaledW) / 2;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(nameX, nameY, 0);
        guiGraphics.pose().scale(NAME_SCALE, NAME_SCALE, 1.0f);
        guiGraphics.drawString(this.font, npcName, 0, 0, 0xFFFFFF, true);
        guiGraphics.pose().popPose();

        guiGraphics.blit(DECO_LINE, lineX, topLineY, 0, 0, lineW, lineH, 256, 16);

        // --- Фраза NPC с переносом строк ---
        // Общая высота в пространстве масштаба
        int scaledLineHeight = (int) ((this.font.lineHeight + 2) * PHRASE_SCALE);
        // Стартовая X: центрируем всю фразу по наибольшей строке
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, phraseY, 0);
        guiGraphics.pose().scale(PHRASE_SCALE, PHRASE_SCALE, 1.0f);

        int scaledScreenW = (int) (screenW / PHRASE_SCALE);
        int lineY = 0;
        for (FormattedCharSequence line : phraseLines) {
            int lineRawW = this.font.width(line);
            int lineX2 = (scaledScreenW - lineRawW) / 2;
            guiGraphics.drawString(this.font, line, lineX2, lineY, 0xFFFFFF, true);
            lineY += (this.font.lineHeight + 2);
        }

        guiGraphics.pose().popPose();

        guiGraphics.blit(DECO_LINE, lineX, bottomLineY, 0, 0, lineW, lineH, 256, 16);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
