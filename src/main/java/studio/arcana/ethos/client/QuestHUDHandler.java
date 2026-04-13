package studio.arcana.ethos.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import studio.arcana.ethos.EthosCore;
import studio.arcana.ethos.data.QuestData;
import studio.arcana.ethos.logic.QuestManager;

@Mod.EventBusSubscriber(modid = EthosCore.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class QuestHUDHandler {
    // Используем ту же подложку, что и для диалогов, или создаем новую hud_bg.png
    private static final ResourceLocation HUD_BG = ResourceLocation.fromNamespaceAndPath(EthosCore.MODID, "textures/gui/hud_bg.png");

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (!event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) return;

        Minecraft mc = Minecraft.getInstance();
        GuiGraphics gui = event.getGuiGraphics();
        
        int y = mc.getWindow().getGuiScaledHeight() / 2 - 50;
        int x = 10;
        int maxWidth = 150; // Ширина фона

        for (QuestData quest : QuestManager.getActiveQuests()) {
            // ПРОВЕРКА: Если отслеживание выключено — пропускаем
            if (!quest.is_tracked) continue;

            // Считаем высоту фона динамически
            int linesCount = 1 + quest.objectives.size();
            int bgHeight = (linesCount * 11) + 10;

            // РИСУЕМ ТЕКСТУРУ (подложку)
            RenderSystem.enableBlend();
            // Используем overlay_bg.png, которую мы делали для диалогов
            gui.blit(HUD_BG, x - 5, y - 5, 0, 0, maxWidth, bgHeight, maxWidth, bgHeight);

            // РИСУЕМ ТЕКСТ
            gui.drawString(mc.font, "§e" + quest.title, x, y, 0xFFFFFF);
            y += 12;

            for (QuestData.Objective obj : quest.objectives) {
                String status = obj.current_progress >= obj.amount_required ? "§a✔" : "§7-";
                gui.drawString(mc.font, status + " §f" + obj.description + ": §b" + obj.current_progress + "/" + obj.amount_required, x + 5, y, 0xFFFFFF);
                y += 10;
            }
            y += 10; // Отступ между квестами
        }
    }
}