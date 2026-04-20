package studio.arcana.ethos.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import studio.arcana.ethos.entity.EthosNpcEntity;

public class NpcRemoverItem extends Item {
    public NpcRemoverItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        // Проверяем, что кликнули именно по нашему NPC
        if (target instanceof EthosNpcEntity) {
            if (!player.level().isClientSide()) {
                target.discard(); // Удаляем сущность из мира
                player.sendSystemMessage(Component.literal("§c[Ethos]: NPC успешно удален."));
            }
            return InteractionResult.sidedSuccess(player.level().isClientSide());
        }
        return InteractionResult.PASS;
    }
}