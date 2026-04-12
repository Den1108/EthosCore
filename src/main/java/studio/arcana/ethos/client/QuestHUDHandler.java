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
    private static final ResourceLocation HUD_BG = new ResourceLocation(EthosCore.MODID, "textures/gui/overlay_bg.png");

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        // Рисуем поверх хотбара, чтобы не было конфликтов слоев
        if (!event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;

        var activeQuests = QuestManager.getActiveQuests();
        if (activeQuests.isEmpty()) return;

        GuiGraphics gui = event.getGuiGraphics();
        int y = mc.getWindow().getGuiScaledHeight() / 2 - 50; // Центр экрана по вертикали
        int x = 10; // Небольшой отступ слева

        for (QuestData quest : activeQuests) {
            // 1. Заголовок квеста
            gui.drawString(mc.font, "§e" + quest.title, x, y, 0xFFFFFF);
            y += 12;

            // 2. Список задач
            for (QuestData.Objective obj : quest.objectives) {
                String status = obj.current_progress >= obj.amount_required ? "§a✔" : "§7-";
                String text = String.format("%s §f%s: §b%d/%d", status, obj.description, obj.current_progress, obj.amount_required);
                
                gui.drawString(mc.font, text, x + 5, y, 0xFFFFFF, true);
                y += 10;
            }
            y += 5; // Отступ между квестами
        }
    }
}