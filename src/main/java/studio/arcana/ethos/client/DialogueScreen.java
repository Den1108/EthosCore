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

    // Константы размеров
    private static final int BTN_WIDTH     = 310;
    private static final int SCREEN_PAD_X  = 20; // Отступы от краев экрана
    private static final float NAME_SCALE   = 1.9f;
    private static final float PHRASE_SCALE = 1.6f;
    private static final float BTN_SCALE    = 1.4f;

    public DialogueScreen(String npcName, String dialogueText, List<DialogueOption> options) {
        super(Component.literal("Dialogue Screen"));
        this.npcName = npcName;
        this.dialogueText = dialogueText;
        this.options = options;
    }

    @Override
    protected void init() {
        this.clearWidgets();
        
        int screenW = this.width;
        int screenH = this.height;

        // 1. Считаем высоту текста NPC с переносом
        int maxTextWidth = screenW - (SCREEN_PAD_X * 2);
        List<FormattedCharSequence> phraseLines = this.font.split(Component.literal(dialogueText), (int)(maxTextWidth / PHRASE_SCALE));
        int phraseHeight = (int) (phraseLines.size() * (10 * PHRASE_SCALE));

        // 2. Размещаем кнопки снизу вверх
        int currentY = screenH - 25; // Начинаем почти от самого низа

        for (int i = options.size() - 1; i >= 0; i--) {
            DialogueOption opt = options.get(i);
            
            // Считаем сколько строк займет текст кнопки
            List<FormattedCharSequence> btnLines = this.font.split(Component.literal(opt.text), (int)((BTN_WIDTH - 20) / BTN_SCALE));
            
            // Высота кнопки зависит от кол-ва строк (минимум 24 пикселя)
            int btnHeight = Math.max(24, (int)(btnLines.size() * (10 * BTN_SCALE)) + 12);
            
            currentY -= btnHeight; // Сдвигаем Y вверх на высоту кнопки

            this.addRenderableWidget(new DialogueButton(
                (screenW - BTN_WIDTH) / 2, 
                currentY, 
                BTN_WIDTH, 
                btnHeight, 
                Component.literal(opt.text), 
                BTN_SCALE,
                (b) -> {
                    opt.action.run();
                    this.onClose();
                }
            ));
            
            currentY -= 5; // Небольшой отступ между кнопками
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int screenW = this.width;
        int screenH = this.height;

        // --- Фон (растягиваем на пол-экрана снизу) ---
        int bgHeight = screenH / 2;
        int bgY = screenH - bgHeight;

        com.mojang.blaze3d.systems.RenderSystem.enableBlend();
        guiGraphics.blit(DIALOGUE_BG, 0, bgY, screenW, bgHeight, 0.0f, 0.0f, 1024, 256, 1024, 256);

        // --- Имя NPC ---
        float nameY = bgY + 20;
        int nameRawW = this.font.width(npcName);
        int nameX = (int)((screenW - (nameRawW * NAME_SCALE)) / 2);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(nameX, nameY, 0);
        guiGraphics.pose().scale(NAME_SCALE, NAME_SCALE, 1.0f);
        guiGraphics.drawString(this.font, npcName, 0, 0, 0xFFFFFF, true);
        guiGraphics.pose().popPose();

        // Линия под именем
        int lineW = 200;
        int lineH = 4;
        guiGraphics.blit(DECO_LINE, (screenW - lineW) / 2, (int)(nameY + (12 * NAME_SCALE)), 0, 0, lineW, lineH, 256, 16);

        // --- Фраза NPC (с переносом строк) ---
        int maxTextWidth = screenW - (SCREEN_PAD_X * 4);
        List<FormattedCharSequence> phraseLines = this.font.split(Component.literal(dialogueText), (int)(maxTextWidth / PHRASE_SCALE));
        
        float phraseStartY = nameY + (25 * NAME_SCALE);
        
        for (int i = 0; i < phraseLines.size(); i++) {
            FormattedCharSequence line = phraseLines.get(i);
            lineW = (int)(this.font.width(line) * PHRASE_SCALE);
            int lineX = (screenW - lineW) / 2;

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(lineX, phraseStartY + (i * 12 * PHRASE_SCALE), 0);
            guiGraphics.pose().scale(PHRASE_SCALE, PHRASE_SCALE, 1.0f);
            guiGraphics.drawString(this.font, line, 0, 0, 0xDDDDDD, true);
            guiGraphics.pose().popPose();
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}