package studio.arcana.ethos.client.render;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import studio.arcana.ethos.EthosCore;
import studio.arcana.ethos.entity.EthosNpcEntity;

public class EthosNpcRenderer extends MobRenderer<EthosNpcEntity, PlayerModel<EthosNpcEntity>> {
    
    private static final ResourceLocation NPC_TEXTURE = ResourceLocation.fromNamespaceAndPath(EthosCore.MODID, "textures/entity/npc_vika.png");

    public EthosNpcRenderer(EntityRendererProvider.Context context) {
        // Используем PLAYER_SLIM для модели Алекс (тонкие руки)
        // В конструкторе PlayerModel второй параметр true означает "slim model"
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM), true), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(EthosNpcEntity entity) {
        return NPC_TEXTURE;
    }
}