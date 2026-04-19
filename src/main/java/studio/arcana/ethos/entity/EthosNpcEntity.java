package studio.arcana.ethos.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import studio.arcana.ethos.commands.DialogueCommand;

public class EthosNpcEntity extends PathfinderMob {

    public EthosNpcEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    // Задаем базовые характеристики. Скорость 0, чтобы он стоял на месте.
    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D); // Чтобы не отлетал от ударов
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND) {
            // Диалоги — это клиентская часть, поэтому вызываем только на клиенте
            if (this.level().isClientSide()) {
                // Вызываем твой метод из команд, передаем ID диалога
                DialogueCommand.loadAndShowDialogue("test_npc");
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }
        return super.mobInteract(player, hand);
    }

    // Запрещаем игроку и мобам толкать нашего NPC
    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(net.minecraft.world.entity.Entity entity) {
        // Пусто
    }
}