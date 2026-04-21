package studio.arcana.echostories.registry;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import studio.arcana.echostories.EchoStories;
import studio.arcana.echostories.client.render.EchoNpcRenderer;
import studio.arcana.echostories.entity.EchoNpcEntity;

public class ModEventBusEvents {

    @Mod.EventBusSubscriber(modid = EchoStories.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class CommonEvents {
        @SubscribeEvent
        public static void onAttributeCreate(EntityAttributeCreationEvent event) {
            event.put(ModRegistry.ECHO_NPC.get(), EchoNpcEntity.createAttributes().build());
        }
    }

    @Mod.EventBusSubscriber(modid = EchoStories.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModRegistry.ECHO_NPC.get(), EchoNpcRenderer::new);
        }
    }
}