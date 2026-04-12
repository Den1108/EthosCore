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
        // Рисуем только если таймер активен и поверх хотбара
        if (timer <= 0 || !event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) return;

        Minecraft mc = Minecraft.getInstance();
        GuiGraphics gui = event.getGuiGraphics();
        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();

        // Настройки текста и автопереноса
        int maxWidth = 200; 
        List<FormattedCharSequence> lines = mc.font.split(Component.literal(fullText), maxWidth);
        
        int padding = 5;
        int lineHeight = 10;
        int totalHeight = (lines.size() * lineHeight) + 15; // + место под имя
        int x = (screenW - maxWidth) / 2;
        int y = screenH - 70 - totalHeight; // Позиция над хотбаром

        // 1. Рисуем подложку
        RenderSystem.enableBlend();
        gui.blit(OVERLAY_BG, x - padding, y - padding, 0, 0, maxWidth + (padding * 2), totalHeight + padding, maxWidth + (padding * 2), totalHeight + padding);

        // 2. Рисуем имя NPC
        gui.drawString(mc.font, "§6" + npcName, x, y, 0xFFFFFF);

        // 3. Рисуем строки текста
        int currentY = y + 12;
        for (FormattedCharSequence line : lines) {
            gui.drawString(mc.font, line, x, currentY, 0xDDDDDD, false);
            currentY += lineHeight;
        }

        timer--;
    }
}