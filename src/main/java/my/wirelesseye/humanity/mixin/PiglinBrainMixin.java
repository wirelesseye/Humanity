package my.wirelesseye.humanity.mixin;

import my.wirelesseye.humanity.entity.human.HumanEntity;
import my.wirelesseye.humanity.util.WorldHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PiglinBrain.class)
public class PiglinBrainMixin {
    @Inject(
            method = "getPreferredTarget",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/ai/brain/Brain;getOptionalMemory(Lnet/minecraft/entity/ai/brain/MemoryModuleType;)Ljava/util/Optional;",
                    ordinal = 2
            ),
            cancellable = true
    )
    private static void injectGetPreferredTarget(
            PiglinEntity piglin, CallbackInfoReturnable<Optional<? extends LivingEntity>> cir) {

        Brain<PiglinEntity> brain = piglin.getBrain();
        Optional<PlayerEntity> player = brain.getOptionalMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD);
        HumanEntity human = WorldHelper.getClosestHuman(piglin, entity -> !PiglinBrain.wearsGoldArmor(entity));

        if (human != null && (player.isEmpty()
                || piglin.squaredDistanceTo(human) < piglin.squaredDistanceTo(player.get()))) {
            cir.setReturnValue(Optional.of(human));
        }
    }
}
