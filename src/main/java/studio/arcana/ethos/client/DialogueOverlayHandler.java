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

    // Твои размеры 400x60
    private static final int BG_W = 400;
    private static final int BG_H = 60;

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

        // 1. Формируем сообщение: [Имя]: Текст
        String prefix = "§d[" + npcName + "]: ";
        String fullMessage = prefix + "§f" + fullText;

        // 2. Настройка переноса (отступаем по 15 пикселей от краев рамки)
        int textMaxWidth = BG_W - 30; 
        List<FormattedCharSequence> lines = mc.font.split(Component.literal(fullMessage), textMaxWidth);
        
        // 3. Позиционирование панели по центру экрана
        int x = (screenW - BG_W) / 2;
        // 65 — это отступ от нижнего края экрана, чтобы висело ровно над опытом
        int y = screenH - 65 - BG_H; 

        RenderSystem.enableBlend();
        
        // 4. Отрисовка твоей текстуры 400x60 (без растяжения)
        // Если твой файл .png размером 400x60, последние два параметра должны быть 400, 60.
        // Если файл 512x64, то последние два параметра — 512, 64.
        gui.blit(OVERLAY_BG, x, y, 0, 0, BG_W, BG_H, BG_W, BG_H);

        // 5. Отрисовка текста внутри рамки
        // Центрируем текст по вертикали внутри 60 пикселей высоты
        int currentY = y + (BG_H / 2) - (lines.size() * 10 / 2); 
        
        for (FormattedCharSequence line : lines) {
            // x + 15 — небольшой отступ слева внутри рамки
            gui.drawString(mc.font, line, x + 15, currentY, 0xFFFFFF, true);
            currentY += 10;
        }

        timer--;
    }
}