package studio.arcana.ethos.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import studio.arcana.ethos.EthosCore;
import java.util.List;

public class DialogueScreen extends Screen {

    // Декоративная линия под именем NPC и под фразой: 256x16 px
    private static final ResourceLocation DECO_LINE =
            ResourceLocation.fromNamespaceAndPath(EthosCore.MODID, "textures/gui/dialogue_name_line.png");

    private final String npcName;
    private final String dialogueText;
    private final List<DialogueOption> options;

    // Параметры кнопок (подбирайте под свою текстуру)
    private static final int BTN_WIDTH   = 220;
    private static final int BTN_X       = 12;
    private static final int BTN_START_Y = 12;
    private static final int BTN_SPACING = 6;
    private static final int BTN_PAD_V   = 10; // вертикальные поля текста внутри кнопки

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
            // Считаем строки без скобок (на скрине скобок нет)
            List<FormattedCharSequence> lines =
                    this.font.split(Component.literal(option.text), BTN_WIDTH - 16);
            int btnHeight = lines.size() * (this.font.lineHeight + 1) + BTN_PAD_V * 2;

            int finalCurrentY = currentY;
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
        // Рисуем кнопки и всё остальное через super
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int screenW = this.width;
        int screenH = this.height;

        // ── Блок NPC внизу по центру ────────────────────────────────────────
        // Размеры декоративной линии
        int lineW  = 200; // ширина отрисовки линии (исходник 256x16)
        int lineH  = 16;
        int lineX  = (screenW - lineW) / 2;

        // Отступ снизу — чтобы не перекрывать хотбар (хотбар ~22px)
        int bottomMargin = 30;

        // Расстояния (в px, без масштаба — шрифт уже достаточно крупный)
        int nameH   = this.font.lineHeight;      // высота строки имени
        int phraseH = this.font.lineHeight;      // высота строки фразы
        int gap     = 4;                          // зазор между элементами

        // Считаем Y снизу вверх:
        // [нижняя декоративная линия] [gap] [фраза] [gap] [верхняя декоративная линия] [gap] [имя]
        int bottomLineY  = screenH - bottomMargin - lineH;
        int phraseY      = bottomLineY - gap - phraseH;
        int topLineY     = phraseY - gap - lineH;
        int nameY        = topLineY - gap - nameH;

        // Имя NPC
        int nameW = this.font.width(npcName);
        guiGraphics.drawString(this.font, npcName,
                (screenW - nameW) / 2, nameY, 0xFFFFFF, true);

        // Верхняя декоративная линия (под именем)
        guiGraphics.blit(DECO_LINE, lineX, topLineY, 0, 0, lineW, lineH, 256, 16);

        // Фраза NPC
        int phraseW = this.font.width(dialogueText);
        guiGraphics.drawString(this.font, dialogueText,
                (screenW - phraseW) / 2, phraseY, 0xFFFFFF, true);

        // Нижняя декоративная линия (под фразой)
        guiGraphics.blit(DECO_LINE, lineX, bottomLineY, 0, 0, lineW, lineH, 256, 16);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
