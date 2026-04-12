package studio.arcana.ethos;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import studio.arcana.ethos.commands.DialogueCommand;
import studio.arcana.ethos.logic.QuestManager;

@Mod(EthosCore.MODID)
public class EthosCore {
    public static final String MODID = "ethoscore";
    private static final Logger LOGGER = LogManager.getLogger();

    public EthosCore() {
        // Регистрация этого экземпляра класса в шине событий Forge
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

    // Добавляем аннотацию, чтобы Forge видел этот метод
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // Проверяем фазу и сторону
        if (event.phase == TickEvent.Phase.END && event.side.isClient()) {
            // Проверяем инвентарь раз в секунду
            if (event.player != null && event.player.tickCount % 20 == 0) {
                QuestManager.checkItems(event.player);
            }
        }
    }
}