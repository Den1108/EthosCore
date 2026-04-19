package studio.arcana.ethos.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import studio.arcana.ethos.EthosCore;

public class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EthosCore.MODID);

    public static final RegistryObject<CreativeModeTab> ETHOS_TAB = CREATIVE_MODE_TABS.register("ethos_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.ethos_tab"))
                    .icon(() -> new ItemStack(ModRegistry.NPC_SPAWN_EGG.get())) // Иконка вкладки - наше яйцо
                    .displayItems((parameters, output) -> {
                        output.accept(ModRegistry.NPC_SPAWN_EGG.get()); // Добавляем яйцо во вкладку
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}