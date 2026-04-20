package studio.arcana.echostories;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus; // Добавь
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import studio.arcana.echostories.commands.DialogueCommand;
import studio.arcana.echostories.logic.QuestManager;
import studio.arcana.echostories.registry.ModCreativeTab;
import studio.arcana.echostories.registry.ModRegistry;

@Mod(EchoStories.MODID)
public class EchoStories {
    public static final String MODID = "echostories";
    private static final Logger LOGGER = LogManager.getLogger();

    public EchoStories() {
        // Использование FMLJavaModLoadingContext.get() устарело в новых версиях Forge.
        // Правильный способ получить шину событий мода:
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModRegistry.register(modEventBus);
        ModCreativeTab.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
        
        try {
            QuestManager.loadProgress();
            LOGGER.info("EchoStories: Quest progress loaded.");
        } catch (Exception e) {
            LOGGER.error("EchoStories: Failed to load quest progress!");
        }

        LOGGER.info("EchoStories: System Initialized");
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        DialogueCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.side.isClient()) {
            if (event.player != null && event.player.tickCount % 20 == 0) {
                QuestManager.checkItems(event.player);
            }
        }
    }
}