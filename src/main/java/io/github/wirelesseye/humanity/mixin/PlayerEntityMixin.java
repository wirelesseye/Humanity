package io.github.wirelesseye.humanity.mixin;

import io.github.wirelesseye.humanity.entity.player.PlayerEntityTrait;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements PlayerEntityTrait {
    @Unique
    private HashSet<UUID> partyMembers = new HashSet<>();

    @Override
    public Set<UUID> humanity$getPartyMembers() {
        return this.partyMembers;
    }

    @Override
    public void humanity$addPartyMember(UUID uuid) {
        this.partyMembers.add(uuid);
    }

    @Override
    public void humanity$removePartyMember(UUID uuid) {
        this.partyMembers.remove(uuid);
    }

    @Unique
    private void writePartyMembersToNbt(NbtCompound nbt) {
        NbtList list = new NbtList();
        for (UUID uuid : partyMembers) {
            list.add(NbtString.of(uuid.toString()));
        }
        nbt.put("PartyMembers", list);
    }

    @Unique
    private void readPartyMembersFromNbt(NbtCompound nbt) {
        this.partyMembers = new HashSet<>();
        NbtList list = nbt.getList("PartyMembers", NbtType.COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            String uuidString = list.getString(i);
            UUID uuid = UUID.fromString(uuidString);
            this.partyMembers.add(uuid);
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void injectReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        this.readPartyMembersFromNbt(nbt);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void injectWriteCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        this.writePartyMembersToNbt(nbt);
    }
}
