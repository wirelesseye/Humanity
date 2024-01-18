package io.github.wirelesseye.humanity;

import io.github.wirelesseye.humanity.entity.human.HumanEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class AllEntityTypes {
    public static final EntityType<HumanEntity> HUMAN = Registry.register(Registry.ENTITY_TYPE,
            new Identifier(Humanity.ID, "human"), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE,
                    HumanEntity::new).dimensions(EntityDimensions.fixed(0.6f, 1.8f)).build());

    public static void register() {
        FabricDefaultAttributeRegistry.register(HUMAN, HumanEntity.createHumanAttributes());
    }
}
