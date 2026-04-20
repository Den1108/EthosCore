package studio.arcana.ethos.entity;

import net.minecraft.network.chat.Component;
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
import studio.arcana.ethos.logic.QuestManager;
import studio.arcana.ethos.registry.ModRegistry;

public class EthosNpcEntity extends PathfinderMob {

    public EthosNpcEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        Player player = this.level().getNearestPlayer(this, 16.0D);
        if (player != null) {
            this.lookAt(player, 360.0F, 360.0F);
            this.setYHeadRot(this.getYRot());
            this.setYBodyRot(this.getYRot());
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof Player player) {
            if (player.getMainHandItem().is(ModRegistry.NPC_REMOVER.get())) {
                if (!this.level().isClientSide()) {
                    this.discard();
                    player.sendSystemMessage(Component.literal("§c[Ethos]: NPC удален."));
                }
                return true;
            }
        }
        return false; 
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND) {
            if (player.getItemInHand(hand).is(ModRegistry.NPC_REMOVER.get())) {
                return InteractionResult.PASS; 
            }

            if (this.level().isClientSide()) {
                // ЛОГИКА СТЕЙТОВ:
                String dialogueFile = "test_npc"; // По умолчанию - первый диалог

                // Если квест уже взят
                if (QuestManager.hasQuest("calibration_47")) {
                    // Проверяем, принес ли игрок все вещи
                    if (QuestManager.isQuestComplete("calibration_47")) {
                        dialogueFile = "test_npc_complete"; // Стейт 3: Квест готов к сдаче
                    } else {
                        dialogueFile = "test_npc_progress"; // Стейт 2: Квест в процессе
                    }
                }

                DialogueCommand.loadAndShowDialogue(dialogueFile);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }
        return super.mobInteract(player, hand);
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
    public boolean isPushable() { return false; }
}