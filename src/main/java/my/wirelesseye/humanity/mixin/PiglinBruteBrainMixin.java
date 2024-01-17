package my.wirelesseye.humanity.mixin;

import my.wirelesseye.humanity.entity.human.HumanEntity;
import my.wirelesseye.humanity.util.WorldHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.PiglinBruteBrain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PiglinBruteBrain.class)
public abstract class PiglinBruteBrainMixin {
    @Shadow
    private static Optional<? extends LivingEntity> method_30249(AbstractPiglinEntity piglin, MemoryModuleType<? extends LivingEntity> memoryModuleType) {
        throw new AssertionError();
    }

    @Inject(
            method = "getTarget",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/mob/PiglinBruteBrain;method_30249(Lnet/minecraft/entity/mob/AbstractPiglinEntity;Lnet/minecraft/entity/ai/brain/MemoryModuleType;)Ljava/util/Optional;"
            ),
            cancellable = true
    )
    static private void injectGetTarget(AbstractPiglinEntity piglin, CallbackInfoReturnable<Optional<? extends LivingEntity>> cir) {
        Optional<? extends LivingEntity> player = method_30249(piglin, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER);
        HumanEntity human = WorldHelper.getClosestHuman(piglin);

        if (human != null && (player.isEmpty()
                || piglin.squaredDistanceTo(human) < piglin.squaredDistanceTo(player.get()))) {
            cir.setReturnValue(Optional.of(human));
        }
    }
}
