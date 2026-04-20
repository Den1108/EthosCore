package studio.arcana.ethos.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import studio.arcana.ethos.EthosCore;
import studio.arcana.ethos.entity.EthosNpcEntity;

public class ModRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, EthosCore.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EthosCore.MODID);

    public static final RegistryObject<EntityType<EthosNpcEntity>> ETHOS_NPC = ENTITY_TYPES.register("ethos_npc",
            () -> EntityType.Builder.of(EthosNpcEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.8f) // Хитбокс размером с обычного игрока
                    .clientTrackingRange(8)
                    .build("ethos_npc"));

    // Яйцо призыва для теста
    public static final RegistryObject<Item> NPC_SPAWN_EGG = ITEMS.register("ethos_npc_spawn_egg",
            () -> new ForgeSpawnEggItem(ETHOS_NPC, 0x55FF55, 0xFFFFFF, new Item.Properties()));
  
    public static final RegistryObject<Item> NPC_REMOVER = ITEMS.register("npc_remover",
            () -> new NpcRemoverItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
        ITEMS.register(eventBus);
    }
}