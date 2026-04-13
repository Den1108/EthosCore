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

        // 1. Подготовка текста
        String prefix = "§d[" + npcName + "]: ";
        String message = "§f" + fullText;
        Component fullComp = Component.literal(prefix + message);

        // 2. Динамический расчет размеров
        int maxAllowedWidth = 280; // Максимальная ширина, чтобы не было слишком длинно
        int textWidth = mc.font.width(fullComp);
        
        // Ширина плашки: либо ширина текста + отступы, либо максимум
        int finalBGW = Math.min(textWidth + 20, maxAllowedWidth); 
        List<FormattedCharSequence> lines = mc.font.split(fullComp, finalBGW - 20);
        
        // Высота плашки подстраивается под количество строк
        int finalBGH = (lines.size() * 10) + 12;

        int x = (screenW - finalBGW) / 2;
        int y = screenH - 58 - finalBGH; // Позиция над хотбаром

        RenderSystem.enableBlend();
        
        // 3. Отрисовка текстуры (из файла 256x256)
        // Мы берем область 0,0 -> finalBGW, finalBGH и рисуем её на экране.
        // Последние два параметра (256, 256) — это реальный размер твоего файла.
        gui.blit(OVERLAY_BG, x, y, 0, 0, finalBGW, finalBGH, 512, 512);

        // 4. Отрисовка текста
        int currentY = y + 6;
        for (FormattedCharSequence line : lines) {
            gui.drawString(mc.font, line, x + 10, currentY, 0xFFFFFF, true);
            currentY += 10;
        }

        timer--;
    }
}