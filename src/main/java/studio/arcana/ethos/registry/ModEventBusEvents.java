package studio.arcana.ethos.registry;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import studio.arcana.ethos.EthosCore;
import studio.arcana.ethos.client.render.EthosNpcRenderer;
import studio.arcana.ethos.entity.EthosNpcEntity;

public class ModEventBusEvents {

    @Mod.EventBusSubscriber(modid = EthosCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class CommonEvents {
        @SubscribeEvent
        public static void onAttributeCreate(EntityAttributeCreationEvent event) {
            event.put(ModRegistry.ETHOS_NPC.get(), EthosNpcEntity.createAttributes().build());
        }
    }

    @Mod.EventBusSubscriber(modid = EthosCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModRegistry.ETHOS_NPC.get(), EthosNpcRenderer::new);
        }
    }
}