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
    private static final int BTN_SPACING = 10;
    private static final int BTN_PAD_V   = 16;

    // --- Масштаб имени NPC и фразы ---
    private static final float NAME_SCALE   = 1.9f;
    private static final float PHRASE_SCALE = 1.7f;

    // --- Фоновая панель для имени + фразы ---
    // Ширина панели (подбери под свой экран, обычно 380–500)
    private static final int PANEL_WIDTH  = 420;
    // Отступ текста внутри панели по вертикали
    private static final int PANEL_PAD_V  = 12;
    // Отступ текста внутри панели по горизонтали
    private static final int PANEL_PAD_H  = 20;
    // Цвет панели: верхние 2 hex-цифры = alpha (AA ≈ 67%, CC ≈ 80%, 80 ≈ 50%)
    private static final int PANEL_COLOR  = 0xAA000000;
    // Отступ панели от нижнего края экрана
    private static final int PANEL_BOTTOM_MARGIN = 12;

    public DialogueScreen(String npcName, String dialogueText, List<DialogueOption> options) {
        super(Component.literal(npcName));
        this.npcName = npcName;
        this.dialogueText = dialogueText;
        this.options = options;
    }

    @Override
    protected void init() {
        int currentY = BTN_START_Y;

        for (DialogueOption option : options) {
            int wrapWidth = (int) ((BTN_WIDTH - 16) / 1.5f);
            List<FormattedCharSequence> lines =
                    this.font.split(Component.literal(option.text), wrapWidth);
            int btnHeight = (int) (lines.size() * (this.font.lineHeight + 1) * 1.5f) + BTN_PAD_V * 2;

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

        // ── Считаем высоты текстов ───────────────────────────────────────────
        int nameH   = (int) (this.font.lineHeight * NAME_SCALE);
        int phraseH = (int) (this.font.lineHeight * PHRASE_SCALE);
        int lineH   = 16;   // высота декоративной линии
        int gap     = 6;    // отступ между элементами

        // Общая высота содержимого панели:
        // [отступ] name [gap] topLine [gap] phrase [gap] bottomLine [отступ]
        int contentH = PANEL_PAD_V + nameH + gap + lineH + gap + phraseH + gap + lineH + PANEL_PAD_V;

        // ── Позиция панели ───────────────────────────────────────────────────
        int panelX = (screenW - PANEL_WIDTH) / 2;
        int panelY = screenH - PANEL_BOTTOM_MARGIN - contentH;
        int panelW = PANEL_WIDTH;
        int panelH = contentH;

        // ── Рисуем полупрозрачный фон ────────────────────────────────────────
        guiGraphics.fill(panelX, panelY, panelX + panelW, panelY + panelH, PANEL_COLOR);

        // ── Раскладываем элементы внутри панели (сверху вниз) ───────────────
        int curY = panelY + PANEL_PAD_V;

        // Имя NPC — по центру панели
        int nameRawW    = this.font.width(npcName);
        int nameScaledW = (int) (nameRawW * NAME_SCALE);
        int nameX       = panelX + (panelW - nameScaledW) / 2;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(nameX, curY, 0);
        guiGraphics.pose().scale(NAME_SCALE, NAME_SCALE, 1.0f);
        guiGraphics.drawString(this.font, npcName, 0, 0, 0xFFFFFF, true);
        guiGraphics.pose().popPose();
        curY += nameH + gap;

        // Верхняя декоративная линия — по центру панели
        int lineW = 220;
        int lineX = panelX + (panelW - lineW) / 2;
        guiGraphics.blit(DECO_LINE, lineX, curY, 0, 0, lineW, lineH, 256, 16);
        curY += lineH + gap;

        // Фраза NPC — по центру панели
        int phraseRawW    = this.font.width(dialogueText);
        int phraseScaledW = (int) (phraseRawW * PHRASE_SCALE);
        int phraseX       = panelX + (panelW - phraseScaledW) / 2;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(phraseX, curY, 0);
        guiGraphics.pose().scale(PHRASE_SCALE, PHRASE_SCALE, 1.0f);
        guiGraphics.drawString(this.font, dialogueText, 0, 0, 0xFFFFFF, true);
        guiGraphics.pose().popPose();
        curY += phraseH + gap;

        // Нижняя декоративная линия — по центру панели
        guiGraphics.blit(DECO_LINE, lineX, curY, 0, 0, lineW, lineH, 256, 16);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}