package studio.arcana.echostories.client.render;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import studio.arcana.echostories.EchoStories;
import studio.arcana.echostories.entity.EchoNpcEntity;

public class EchoNpcRenderer extends MobRenderer<EchoNpcEntity, PlayerModel<EchoNpcEntity>> {
    
    private static final ResourceLocation NPC_TEXTURE = ResourceLocation.fromNamespaceAndPath(EchoStories.MODID, "textures/entity/npc_vika.png");

    public EchoNpcRenderer(EntityRendererProvider.Context context) {
        // Используем PLAYER_SLIM для модели Алекс (тонкие руки)
        // В конструкторе PlayerModel второй параметр true означает "slim model"
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM), true), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(EchoNpcEntity entity) {
        return NPC_TEXTURE;
    }
}