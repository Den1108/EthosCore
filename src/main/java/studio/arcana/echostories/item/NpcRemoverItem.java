package studio.arcana.echostories.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import studio.arcana.echostories.entity.EchoNpcEntity;

public class NpcRemoverItem extends Item {
    public NpcRemoverItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (target instanceof EchoNpcEntity) {
            if (!player.level().isClientSide()) {
                target.discard();
                player.sendSystemMessage(Component.literal("§c[Echo Stories]: NPC успешно удален."));
            }
            // Возвращаем SUCCESS, чтобы анимация руки проигралась и другие события отменились
            return InteractionResult.sidedSuccess(player.level().isClientSide());
        }
        return InteractionResult.PASS;
    }
}