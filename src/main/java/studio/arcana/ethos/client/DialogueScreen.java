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
            
    private static final ResourceLocation DIALOGUE_BG =
            ResourceLocation.fromNamespaceAndPath(EthosCore.MODID, "textures/gui/dialogue_bg.png");

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
            List<FormattedCharSequence> lines = this.font.split(Component.literal(option.text), wrapWidth);
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

        // --- Измененная логика фона ---
        int bgPaddingTop = 15; 
        int bgY = nameY - bgPaddingTop;
        int bgHeight = screenH - bgY; 
        
        // Рендерим фон (он растянется до самого низа экрана)
        if (bgHeight > 0) {
            // Включаем поддержку прозрачности
            com.mojang.blaze3d.systems.RenderSystem.enableBlend();
    
            // Рисуем текстуру
            uiGraphics.blit(DIALOGUE_BG, 0, bgY, screenW, bgHeight, 0.0f, 0.0f, 1024, 256, 1024, 256);
    
            // Выключаем блендинг после отрисовки, чтобы не сломать другие элементы интерфейса
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

        // --- Фраза NPC ---
        int phraseRawW = this.font.width(dialogueText);
        int phraseScaledW = (int) (phraseRawW * PHRASE_SCALE);
        int phraseX = (screenW - phraseScaledW) / 2;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(phraseX, phraseY, 0);
        guiGraphics.pose().scale(PHRASE_SCALE, PHRASE_SCALE, 1.0f);
        guiGraphics.drawString(this.font, dialogueText, 0, 0, 0xFFFFFF, true);
        guiGraphics.pose().popPose();

        guiGraphics.blit(DECO_LINE, lineX, bottomLineY, 0, 0, lineW, lineH, 256, 16);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}