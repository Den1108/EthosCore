package studio.arcana.ethos;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Импортируем наши классы из новых пакетов
import studio.arcana.ethos.commands.DialogueCommand;
import studio.arcana.ethos.logic.QuestManager;

@Mod(EthosCore.MODID)
public class EthosCore {
    public static final String MODID = "ethoscore";
    private static final Logger LOGGER = LogManager.getLogger();

    public EthosCore() {
        // Регистрация событий
        MinecraftForge.EVENT_BUS.register(this);
        
        // Загружаем прогресс квестов при запуске
        // Это важно, чтобы список принятых заданий не обнулялся
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
        // Регистрация команд. Теперь Java знает, что DialogueCommand 
        // находится в пакете studio.arcana.ethos.commands
        DialogueCommand.register(event.getDispatcher());
    }
}