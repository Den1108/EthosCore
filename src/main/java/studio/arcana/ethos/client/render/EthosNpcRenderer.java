package studio.arcana.ethos.client.render;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import studio.arcana.ethos.EthosCore;
import studio.arcana.ethos.entity.EthosNpcEntity;

public class EthosNpcRenderer extends MobRenderer<EthosNpcEntity, PlayerModel<EthosNpcEntity>> {
    
    // Путь к текстуре скина. Обязательно закинь картинку по этому пути!
    private static final ResourceLocation NPC_TEXTURE = ResourceLocation.fromNamespaceAndPath(EthosCore.MODID, "textures/entity/npc_vika.png");

    public EthosNpcRenderer(EntityRendererProvider.Context context) {
        // false означает использование широкой модели игрока (не "Alex" руки)
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(EthosNpcEntity entity) {
        return NPC_TEXTURE;
    }
}