package studio.arcana.ethos.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import studio.arcana.ethos.EthosCore;
import java.util.List;

public class DialogueScreen extends Screen {
    private static final ResourceLocation DIALOGUE_BG = new ResourceLocation(EthosCore.MODID, "textures/gui/dialogue_text_bg.png");
    
    private final String npcName;
    private final String dialogueText;
    private final List<DialogueOption> options;

    public DialogueScreen(String npcName, String dialogueText, List<DialogueOption> options) {
        super(Component.literal(npcName));
        this.npcName = npcName;
        this.dialogueText = dialogueText;
        this.options = options;
    }

    @Override
    protected void init() {
        int screenW = this.width;
        int screenH = this.height;

        // Кнопки слева сверху
        int btnWidth = 240; 
        int btnX = 15;      
        int currentY = 15;  
        int spacing = 8;    

        for (DialogueOption option : options) {
            String formattedOption = "[" + option.text + "]";
            
            // Расчет высоты с учетом увеличенного шрифта кнопок (примерно 1.2x)
            // Мы делим ширину на масштаб, чтобы текст корректно переносился
            List<net.minecraft.util.FormattedCharSequence> lines = this.font.split(Component.literal(formattedOption), (int)((btnWidth - 20) / 1.2f));
            int btnHeight = (int)((lines.size() * 12) + 15);

            this.addRenderableWidget(EthosButton.flexibleDialogue(btnX, currentY, btnWidth, btnHeight, 
                Component.literal(formattedOption), (btn) -> {
                    option.action.run();
                    this.onClose();
            }));

            currentY += btnHeight + spacing;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int screenW = this.width;
        int screenH = this.height;

        // --- Фраза NPC (Поднята выше) ---
        guiGraphics.pose().pushPose();
        
        float scale = 1.6f; // Чуть больше масштаб для четкости
        guiGraphics.pose().scale(scale, scale, 1.0f);

        // baseCenterY теперь -80, что поднимает текст значительно выше хотбара
        float baseY = (screenH - 80) / scale; 

        // Имя NPC
        String nameFormatted = "— " + npcName + " —";
        int nameW = this.font.width(nameFormatted);
        float nameX = ((screenW / scale) - nameW) / 2.0f;
        float nameY = baseY - 25; // Расстояние от имени до фразы
        
        guiGraphics.drawString(this.font, nameFormatted, (int)nameX, (int)nameY, 0xFFFFFF, true);

        // Фраза NPC
        int textW = this.font.width(dialogueText);
        float textX = ((screenW / scale) - textW) / 2.0f;
        
        guiGraphics.drawString(this.font, dialogueText, (int)textX, (int)baseY, 0xFFFFFF, true);

        guiGraphics.pose().popPose();

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}