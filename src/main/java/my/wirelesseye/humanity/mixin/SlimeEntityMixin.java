package my.wirelesseye.humanity.mixin;

import my.wirelesseye.humanity.entity.human.HumanEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.SlimeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SlimeEntity.class)
public abstract class SlimeEntityMixin {
    @Shadow protected abstract boolean canAttack();

    @Shadow protected abstract void damage(LivingEntity target);

    @Inject(method = "pushAwayFrom", at = @At("TAIL"))
    void injectPushAwayFrom(Entity entity, CallbackInfo ci) {
        if (entity instanceof HumanEntity && this.canAttack()) {
            this.damage((LivingEntity)entity);
        }
    }
}
