package studio.arcana.echostories.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import studio.arcana.echostories.EchoStories;
import studio.arcana.echostories.commands.DialogueCommand;
import studio.arcana.echostories.data.DialogueData;

import java.util.List;

public class DialogueScreen extends Screen {

    private static final ResourceLocation DECO_LINE =
            ResourceLocation.fromNamespaceAndPath(EchoStories.MODID, "textures/gui/dialogue_name_line.png");
            
    private static final ResourceLocation DIALOGUE_BG =
            ResourceLocation.fromNamespaceAndPath(EchoStories.MODID, "textures/gui/dialogue_bg.png");

    private final DialogueData dialogueData;
    private DialogueData.Node currentNode;

    private static final int BTN_WIDTH   = 310;
    private static final int BTN_X       = 10;
    private static final int BTN_START_Y = 10;
    private static final int BTN_SPACING = 10;
    private static final int BTN_PAD_V   = 16;

    private static final float NAME_SCALE   = 1.9f;
    private static final float PHRASE_SCALE = 1.7f;
    private static final float BTN_TEXT_SCALE = 1.7f;
    private static final int PHRASE_MAX_WIDTH = 500;

    public DialogueScreen(DialogueData dialogueData, DialogueData.Node startNode) {
        super(Component.literal(startNode.sender_name));
        this.dialogueData = dialogueData;
        this.currentNode = startNode;
    }

    public void changeNode(DialogueData.Node newNode) {
        this.currentNode = newNode;
        // Очищаем старые кнопки перед перерисовкой новых
        this.clearWidgets();
        // Перевызываем метод добавления кнопок
        this.init();
    }

    @Override
    protected void init() {
        if (currentNode == null || currentNode.options == null) return;

        int currentY = BTN_START_Y;
        for (DialogueData.Option option : currentNode.options) {

            int wrapWidth = (int) ((BTN_WIDTH - 24) / BTN_TEXT_SCALE);
            List<FormattedCharSequence> lines = this.font.split(Component.literal(option.text), wrapWidth);
            int btnHeight = (int) (lines.size() * (this.font.lineHeight + 1) * BTN_TEXT_SCALE) + BTN_PAD_V * 2;

            this.addRenderableWidget(new DialogueButton(
                    BTN_X, currentY, BTN_WIDTH, btnHeight,
                    Component.literal(option.text),
                    (btn) -> {
                        if ("NEXT_NODE".equals(option.action_type)) {
                            DialogueData.Node next = dialogueData.getNode(option.action_value);
                            if (next != null) {
                                if (next.options == null || next.options.isEmpty()) {
                                    // Если у следующего шага нет кнопок, закрываем GUI и выводим его в оверлей!
                                    this.onClose();
                                    DialogueOverlayHandler.showNode(dialogueData, next);
                                } else {
                                    this.changeNode(next);
                                }
                            } else {
                                this.onClose();
                            }
                        } else {
                            DialogueCommand.handleAction(option.action_type, option.action_value);
                            this.onClose();
                        }
                    }
            ));
            currentY += btnHeight + BTN_SPACING;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if (currentNode == null) return;

        int screenW = this.width;
        int screenH = this.height;

        int lineW  = 256;
        int lineH  = 16;
        int lineX  = (screenW - lineW) / 2;

        int bottomMargin = 65;
        int gap = 6;

        int phraseWrapWidth = (int) (PHRASE_MAX_WIDTH / PHRASE_SCALE);
        List<FormattedCharSequence> phraseLines = this.font.split(
                Component.literal(currentNode.dialogue_text), phraseWrapWidth);
        int phraseH = (int) (phraseLines.size() * (this.font.lineHeight + 2) * PHRASE_SCALE);

        int nameH = (int) (this.font.lineHeight * NAME_SCALE);

        int bottomLineY = screenH - bottomMargin - lineH;
        int phraseY     = bottomLineY - gap - phraseH;
        int topLineY    = phraseY - gap - lineH;
        int nameY       = topLineY - gap - nameH;

        int bgPaddingTop = 15;
        int bgY = nameY - bgPaddingTop;
        int bgHeight = screenH - bgY;

        if (bgHeight > 0) {
            com.mojang.blaze3d.systems.RenderSystem.enableBlend();
            guiGraphics.blit(DIALOGUE_BG, 0, bgY, screenW, bgHeight, 0.0f, 0.0f, 1024, 256, 1024, 256);
            com.mojang.blaze3d.systems.RenderSystem.disableBlend();
        }

        int nameRawW = this.font.width(currentNode.sender_name);
        int nameScaledW = (int) (nameRawW * NAME_SCALE);
        int nameX = (screenW - nameScaledW) / 2;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(nameX, nameY, 0);
        guiGraphics.pose().scale(NAME_SCALE, NAME_SCALE, 1.0f);
        guiGraphics.drawString(this.font, currentNode.sender_name, 0, 0, 0xFFFFFF, true);
        guiGraphics.pose().popPose();

        guiGraphics.blit(DECO_LINE, lineX, topLineY, 0, 0, lineW, lineH, 256, 16);

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
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}