package studio.arcana.ethos.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import studio.arcana.ethos.EthosCore;

import java.util.List;

@Mod.EventBusSubscriber(modid = EthosCore.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = net.minecraftforge.api.distmarker.Dist.CLIENT)
public class DialogueOverlayHandler {
    private static final ResourceLocation OVERLAY_BG = new ResourceLocation(EthosCore.MODID, "textures/gui/overlay_bg.png");
    
    private static String npcName = "";
    private static String fullText = "";
    private static int timer = 0;

    public static void show(String name, String text, int ticks) {
        npcName = name;
        fullText = text;
        timer = ticks;
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        if (timer <= 0 || !event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) return;

        Minecraft mc = Minecraft.getInstance();
        GuiGraphics gui = event.getGuiGraphics();
        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();

        // 1. Увеличиваем ширину текста (как у Шахрезады)
        int maxWidth = 320; 
        List<FormattedCharSequence> lines = mc.font.split(Component.literal(fullText), maxWidth);
        
        int padding = 6;
        int lineHeight = 10;
        int totalHeight = (lines.size() * lineHeight) + 12; 
        
        // Центрируем по горизонтали
        int x = (screenW - maxWidth) / 2;
        
        // 2. Опускаем максимально низко к хотбару
        // Подбирай это число (55-65), чтобы панель не накладывалась на ячейки инвентаря
        int y = screenH - 58 - totalHeight; 

        // 3. Рисуем длинную подложку
        RenderSystem.enableBlend();
        // Используем blit или fill для создания полупрозрачного черного фона
        // Если у тебя есть текстура overlay_bg.png, она растянется под размер maxWidth
        gui.blit(OVERLAY_BG, x - padding, y - padding, 0, 0, maxWidth + (padding * 2), totalHeight + padding, maxWidth + (padding * 2), totalHeight + padding);

        // 4. Имя NPC (Розовым/Фиолетовым как на скрине)
        gui.drawString(mc.font, "§d[" + npcName + "]:", x, y, 0xFFFFFF);

        // 5. Текст (Белым, начинаем чуть правее имени или сразу под ним)
        int currentY = y + 11;
        for (FormattedCharSequence line : lines) {
            gui.drawString(mc.font, line, x, currentY, 0xFFFFFF, true); // true добавляет тень буквам
            currentY += lineHeight;
        }

        timer--;
    }
}