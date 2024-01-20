package io.github.wirelesseye.humanity.mixin;

import io.github.wirelesseye.humanity.entity.human.HumanEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StatusEffect.class)
abstract class StatusEffectMixin {
    @Inject(method = "applyUpdateEffect", at = @At("TAIL"))
    private void injectApplyUpdateEffect(LivingEntity entity, int amplifier, CallbackInfo ci) {
        StatusEffect self = (StatusEffect) (Object) this;
        if (self == StatusEffects.HUNGER && entity instanceof HumanEntity human) {
            human.addExhaustion(0.005f * (float) (amplifier + 1));
        } else if (self == StatusEffects.SATURATION && entity instanceof HumanEntity human) {
            if (!entity.world.isClient) {
                human.getHungerManager().add(amplifier + 1, 1.0f);
            }
        }
    }
}
