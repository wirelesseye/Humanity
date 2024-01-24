package io.github.wirelesseye.humanity.gui.human;

import io.github.wirelesseye.humanity.entity.human.HumanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

import java.util.Optional;
import java.util.UUID;

public class HumanData {
    public String firstName;
    public String lastName;
    public float health;
    public int foodLevel;
    public UUID leaderPlayerUuid;

    public void readFromHumanEntity(HumanEntity human) {
        this.firstName = human.getFirstName();
        this.lastName = human.getLastName();
        this.health = human.getHealth();
        this.foodLevel = human.getHungerManager().getFoodLevel();
        this.leaderPlayerUuid = human.getLeaderPlayerUuid();
    }

    public void readFromBuffer(PacketByteBuf buf) {
        this.firstName = buf.readString();
        this.lastName = buf.readString();
        this.health = buf.readFloat();
        this.foodLevel = buf.readInt();
        Optional<UUID> leaderPlayerUuid = buf.readOptional(PacketByteBuf::readUuid);
        this.leaderPlayerUuid = leaderPlayerUuid.orElse(null);
    }

    public void writeToBuffer(PacketByteBuf buf) {
        buf.writeString(this.firstName);
        buf.writeString(this.lastName);
        buf.writeFloat(this.health);
        buf.writeInt(this.foodLevel);
        buf.writeOptional(Optional.ofNullable(this.leaderPlayerUuid), PacketByteBuf::writeUuid);
    }
}
