package io.github.wirelesseye.humanity.block;

import io.github.wirelesseye.humanity.Humanity;
import io.github.wirelesseye.humanity.block.doorplate.DoorplateEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class AllBlockEntityTypes {
    public static final BlockEntityType<DoorplateEntity> DOORPLATE_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            new Identifier(Humanity.ID, "doorplate_entity"),
            FabricBlockEntityTypeBuilder.create(DoorplateEntity::new, AllBlocks.DOORPLATE).build()
    );

    public static void register() {}
}
