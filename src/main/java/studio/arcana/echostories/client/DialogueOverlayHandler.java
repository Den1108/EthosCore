package studio.arcana.echostories.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import studio.arcana.echostories.EchoStories;
import studio.arcana.echostories.data.DialogueData;

import java.util.List;

@Mod.EventBusSubscriber(modid = EchoStories.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = net.minecraftforge.api.distmarker.Dist.CLIENT)
public class DialogueOverlayHandler {
    private static final ResourceLocation OVERLAY_BG = ResourceLocation.fromNamespaceAndPath(EchoStories.MODID, "textures/gui/overlay_bg.png");
    
    private static DialogueData activeData = null;
    private static DialogueData.Node currentNode = null;
    private static int timer = 0;

    private static final int FIXED_WIDTH = 300;

    // Метод инициализации отображения шага диалога в оверлее
    public static void showNode(DialogueData data, DialogueData.Node node) {
        activeData = data;
        currentNode = node;
        // Если display_ticks равен 0, рассчитываем базовое время чтения в зависимости от длины строки
        timer = node.display_ticks > 0 ? node.display_ticks : (40 + (node.dialogue_text.length() * 2));
    }

    // Стабильный игровой тик для уменьшения таймера (не зависит от FPS)
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (timer > 0) {
                timer--;
                if (timer <= 0) {
                    handleTimeout();
                }
            }
        }
    }

    // Обработка завершения времени показа текущей реплики
    private static void handleTimeout() {
        if (activeData != null && currentNode != null && currentNode.next_node != null && !currentNode.next_node.isEmpty()) {
            DialogueData.Node next = activeData.getNode(currentNode.next_node);
            if (next != null) {
                if (next.options == null || next.options.isEmpty()) {
                    // Если у следующего шага тоже нет опций — запускаем новый оверлей
                    showNode(activeData, next);
                } else {
                    // Если у следующего шага есть опции — открываем полноценный графический экран
                    Minecraft.getInstance().tell(() -> {
                        Minecraft.getInstance().setScreen(new DialogueScreen(activeData, next));
                    });
                    clear();
                }
            } else {
                clear();
            }
        } else {
            clear();
        }
    }

    private static void clear() {
        activeData = null;
        currentNode = null;
        timer = 0;
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        if (timer <= 0 || currentNode == null || !event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) return;

        Minecraft mc = Minecraft.getInstance();
        GuiGraphics gui = event.getGuiGraphics();
        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();

        // Динамический цвет префикса: Золотой для текущего игрока, Розовый для остальных
        String colorCode = "§d";
        if (mc.player != null && currentNode.sender_name.equals(mc.player.getName().getString())) {
            colorCode = "§6";
        }

        String prefix = colorCode + "[" + currentNode.sender_name + "]: ";
        String message = "§f" + currentNode.dialogue_text;
        Component fullComp = Component.literal(prefix + message);

        List<FormattedCharSequence> lines = mc.font.split(fullComp, FIXED_WIDTH - 20);
        int finalBGH = (lines.size() * 10) + 12;

        int x = (screenW - FIXED_WIDTH) / 2;
        int y = screenH - 58 - finalBGH; 

        RenderSystem.enableBlend();
        gui.blit(OVERLAY_BG, x, y, 0, 0, FIXED_WIDTH, finalBGH, 512, 512);

        int currentY = y + 6;
        for (FormattedCharSequence line : lines) {
            gui.drawString(mc.font, line, x + 10, currentY, 0xFFFFFF, true);
            currentY += 10;
        }
    }
}