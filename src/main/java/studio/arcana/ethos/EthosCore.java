package studio.arcana.ethos;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus; // Добавь
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import studio.arcana.ethos.commands.DialogueCommand;
import studio.arcana.ethos.logic.QuestManager;
import studio.arcana.ethos.registry.ModCreativeTab;
import studio.arcana.ethos.registry.ModRegistry;

@Mod(EthosCore.MODID)
public class EthosCore {
    public static final String MODID = "ethoscore";
    private static final Logger LOGGER = LogManager.getLogger();

    public EthosCore() {
        // Использование FMLJavaModLoadingContext.get() устарело в новых версиях Forge.
        // Правильный способ получить шину событий мода:
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModRegistry.register(modEventBus);
        ModCreativeTab.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
        
        try {
            QuestManager.loadProgress();
            LOGGER.info("EthosCore: Quest progress loaded.");
        } catch (Exception e) {
            LOGGER.error("EthosCore: Failed to load quest progress!");
        }

        LOGGER.info("EthosCore: System Initialized");
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