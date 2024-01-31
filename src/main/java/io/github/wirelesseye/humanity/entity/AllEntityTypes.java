package io.github.wirelesseye.humanity.entity;

import io.github.wirelesseye.humanity.Humanity;
import io.github.wirelesseye.humanity.entity.human.HumanEntity;
import io.github.wirelesseye.humanity.util.RegistryHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;

public class AllEntityTypes {
    public static final EntityType<HumanEntity> HUMAN = RegistryHelper.registerEntityType(
            new Identifier(Humanity.ID, "human"),
            EntityType.Builder.create(HumanEntity::new, SpawnGroup.CREATURE)
                    .setDimensions(0.6f, 1.8f)
    );

    public static final EntityType<TileUpdateAnimEntity> TILE_UPDATE_ANIM = RegistryHelper.registerEntityType(
            new Identifier(Humanity.ID, "tile_update_anim"),
            EntityType.Builder.create(TileUpdateAnimEntity::new, SpawnGroup.AMBIENT).setDimensions(1f, 1f)
    );

    public static void register() {
        RegistryHelper.registerDefaultAttributes(HUMAN, HumanEntity.createHumanAttributes());
    }
}
