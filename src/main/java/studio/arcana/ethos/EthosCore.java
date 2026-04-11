package studio.arcana.ethos;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(EthosCore.MODID)
public class EthosCore {
    public static final String MODID = "ethoscore";
    private static final Logger LOGGER = LogManager.getLogger();

    public EthosCore() {
        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("EthosCore: System Initialized");
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        // Регистрация команд для тестирования диалогов
        DialogueCommand.register(event.getDispatcher());
    }
}