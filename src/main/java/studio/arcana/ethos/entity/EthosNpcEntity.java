package studio.arcana.ethos.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
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

    @Override
    protected void registerGoals() {
        // Оставляем пустым, чтобы он не крутил головой постоянно
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        // При спавне ищем ближайшего игрока в радиусе 16 блоков
        Player player = this.level().getNearestPlayer(this, 16.0D);
        if (player != null) {
            // Поворачиваем туловище и голову к игроку
            this.lookAt(player, 360.0F, 360.0F);
            this.setYHeadRot(this.getYRot());
            this.setYBodyRot(this.getYRot());
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return true;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND) {
            if (this.level().isClientSide()) {
                DialogueCommand.loadAndShowDialogue("test_npc");
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isPushable() {
        return false;
    }
}