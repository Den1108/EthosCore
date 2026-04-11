package studio.arcana.ethos;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = EthosCore.MODID, value = Dist.CLIENT)
public class KeyBindingHandler {
    public static final KeyMapping QUEST_JOURNAL_KEY = new KeyMapping(
            "key.ethoscore.quests", 
            InputConstants.Type.KEYSYM, 
            GLFW.GLFW_KEY_V, 
            "category.ethoscore"
    );

    @SubscribeEvent
    public static void onKeyRegister(RegisterKeyMappingsEvent event) {
        event.register(QUEST_JOURNAL_KEY);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            while (QUEST_JOURNAL_KEY.consumeClick()) {
                // Открываем журнал квестов
                Minecraft.getInstance().setScreen(new QuestJournalScreen());
            }
        }
    }
}