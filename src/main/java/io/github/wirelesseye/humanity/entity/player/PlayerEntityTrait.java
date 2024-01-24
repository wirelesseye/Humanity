package io.github.wirelesseye.humanity.entity.player;

import java.util.Set;
import java.util.UUID;

public interface PlayerEntityTrait {
    Set<UUID> humanity$getPartyMembers();

    void humanity$addPartyMember(UUID uuid);

    void humanity$removePartyMember(UUID uuid);
}
