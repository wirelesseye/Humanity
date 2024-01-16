package my.wirelesseye.humanity.mixin;

import my.wirelesseye.humanity.entity.human.HumanEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(ActiveTargetGoal.class)
abstract class ActiveTargetGoalMixin extends TrackTargetGoal {
    public ActiveTargetGoalMixin(MobEntity mob, boolean checkVisibility) {
        super(mob, checkVisibility);
    }

    @Shadow protected abstract Box getSearchBox(double distance);

    @Shadow protected TargetPredicate targetPredicate;

    @Shadow @Nullable protected LivingEntity targetEntity;

    @Inject(
            method = "findClosestTarget",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getClosestPlayer(Lnet/minecraft/entity/ai/TargetPredicate;Lnet/minecraft/entity/LivingEntity;DDD)Lnet/minecraft/entity/player/PlayerEntity;"),
            cancellable = true
    )
    protected void onGetClosestPlayer(CallbackInfo ci) {
        ArrayList<LivingEntity> playersAndHumans = new ArrayList<>();
        World world = this.mob.world;
        playersAndHumans.addAll(world.getPlayers());
        playersAndHumans.addAll(world.getEntitiesByClass(
                HumanEntity.class, this.getSearchBox(this.getFollowRange()), livingEntity -> true));
        this.targetEntity = world.getClosestEntity(
                playersAndHumans,
                this.targetPredicate,
                this.mob,
                this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
        ci.cancel();
    }
}
