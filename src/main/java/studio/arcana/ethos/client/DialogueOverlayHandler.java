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
    private static final ResourceLocation OVERLAY_BG = ResourceLocation.fromNamespaceAndPath(EthosCore.MODID, "textures/gui/overlay_bg.png");
    
    private static String npcName = "";
    private static String fullText = "";
    private static int timer = 0;

    // Уменьшенные размеры под скриншот (300x40)
    private static final int BG_W = 300;
    private static final int BG_H = 40;

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

        // Имя и текст в одну строку
        String prefix = "§d[" + npcName + "]: ";
        String fullMessage = prefix + "§f" + fullText;

        // Максимальная ширина текста внутри рамки (с отступами)
        int textMaxWidth = BG_W - 20; 
        List<FormattedCharSequence> lines = mc.font.split(Component.literal(fullMessage), textMaxWidth);
        
        int x = (screenW - BG_W) / 2;
        // Позиция над хотбаром (чуть ближе к нему)
        int y = screenH - 55 - BG_H; 

        RenderSystem.enableBlend();
        
        // Отрисовка текстуры 300x40 (убедись, что файл overlay_bg.png тоже 300x40 или 512x64)
        gui.blit(OVERLAY_BG, x, y, 0, 0, BG_W, BG_H, BG_W, BG_H);

        // Центрирование текста по вертикали в маленькой рамке
        int currentY = y + (BG_H / 2) - (lines.size() * 10 / 2); 
        
        for (FormattedCharSequence line : lines) {
            // Небольшой отступ слева (10 пикселей)
            gui.drawString(mc.font, line, x + 10, currentY, 0xFFFFFF, true);
            currentY += 10;
        }

        timer--;
    }
}