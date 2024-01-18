package io.github.wirelesseye.humanity.mixin;

import io.github.wirelesseye.humanity.util.WorldHelper;
import io.github.wirelesseye.humanity.entity.human.HumanEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HoglinBrain;
import net.minecraft.entity.mob.HoglinEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(HoglinBrain.class)
abstract class HoglinBrainMixin {
    @Inject(method = "getNearestVisibleTargetablePlayer", at = @At("TAIL"), cancellable = true)
    private static void injectGetNearestVisibleTargetablePlayer(
            HoglinEntity hoglin, CallbackInfoReturnable<Optional<? extends LivingEntity>> cir) {

        Optional<? extends LivingEntity> player = cir.getReturnValue();
        HumanEntity human = WorldHelper.getClosestHuman(hoglin);

        if (human != null && (player.isEmpty()
                || hoglin.squaredDistanceTo(human) < hoglin.squaredDistanceTo(player.get()))) {
            cir.setReturnValue(Optional.of(human));
        }
    }
}
