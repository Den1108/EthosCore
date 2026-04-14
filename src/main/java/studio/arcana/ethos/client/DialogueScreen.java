package studio.arcana.ethos.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import studio.arcana.ethos.EthosCore;
import java.util.List;

public class DialogueScreen extends Screen {

    private static final ResourceLocation DECO_LINE =
            ResourceLocation.fromNamespaceAndPath(EthosCore.MODID, "textures/gui/dialogue_name_line.png");

    private final String npcName;
    private final String dialogueText;
    private final List<DialogueOption> options;

    // --- Параметры кнопок ---
    private static final int BTN_WIDTH   = 310;
    private static final int BTN_X       = 10;
    private static final int BTN_START_Y = 10;
    // Одинаковый отступ между всеми кнопками — никаких индивидуальных смещений
    private static final int BTN_SPACING = 10;
    private static final int BTN_PAD_V   = 16;

    // --- Масштаб имени NPC и фразы ---
    private static final float NAME_SCALE   = 1.9f;
    private static final float PHRASE_SCALE = 1.7f;

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

            int wrapWidth = (int) ((BTN_WIDTH - 16) / 1.5f);
            List<FormattedCharSequence> lines =
                    this.font.split(Component.literal(option.text), wrapWidth);
            int btnHeight = (int) (lines.size() * (this.font.lineHeight + 1) * 1.5f) + BTN_PAD_V * 2;

            // Все кнопки идут строго одна за другой с одинаковым BTN_SPACING —
            // никаких индивидуальных смещений больше нет
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

        int nameH   = (int) (this.font.lineHeight * NAME_SCALE);
        int phraseH = (int) (this.font.lineHeight * PHRASE_SCALE);
        int gap     = 6;

        int bottomLineY  = screenH - bottomMargin - lineH;
        int phraseY      = bottomLineY - gap - phraseH;
        int topLineY     = phraseY - gap - lineH;
        int nameY        = topLineY - gap - nameH;

        // --- Имя NPC ---
        int nameRawW = this.font.width(npcName);
        int nameScaledW = (int) (nameRawW * NAME_SCALE);
        int nameX = (screenW - nameScaledW) / 2;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(nameX, nameY, 0);
        guiGraphics.pose().scale(NAME_SCALE, NAME_SCALE, 1.0f);
        guiGraphics.drawString(this.font, npcName, 0, 0, 0xFFFFFF, true);
        guiGraphics.pose().popPose();

        // Верхняя декоративная линия
        guiGraphics.blit(DECO_LINE, lineX, topLineY, 0, 0, lineW, lineH, 256, 16);

        // --- Фраза NPC ---
        int phraseRawW = this.font.width(dialogueText);
        int phraseScaledW = (int) (phraseRawW * PHRASE_SCALE);
        int phraseX = (screenW - phraseScaledW) / 2;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(phraseX, phraseY, 0);
        guiGraphics.pose().scale(PHRASE_SCALE, PHRASE_SCALE, 1.0f);
        guiGraphics.drawString(this.font, dialogueText, 0, 0, 0xFFFFFF, true);
        guiGraphics.pose().popPose();

        // Нижняя декоративная линия
        guiGraphics.blit(DECO_LINE, lineX, bottomLineY, 0, 0, lineW, lineH, 256, 16);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
