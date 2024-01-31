package io.github.wirelesseye.humanity.block.doorplate;

import io.github.wirelesseye.humanity.block.AllBlockEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DoorplateEntity extends BlockEntity {
    private final Room room = new Room();
    private int checkTickLeft;

    public DoorplateEntity(BlockPos pos, BlockState state) {
        super(AllBlockEntityTypes.DOORPLATE_ENTITY, pos, state);
    }

    public static void doTick(World world, BlockPos pos, BlockState state, DoorplateEntity entity) {
        entity.tick(world, pos, state);
    }

    protected void tick(World world, BlockPos pos, BlockState state) {
        if (!world.isClient) {
            if (this.checkTickLeft <= 0) {
                this.updateRoom(world, pos, state);
                this.checkTickLeft = 200;
            } else {
                if (this.room.hasRoomTilesChanged()) {
                    this.room.tickAnimation(world);
                }
                this.checkTickLeft--;
            }
        }
    }

    private void updateRoom(World world, BlockPos pos, BlockState state) {
        Direction opposite = state.get(Doorplate.FACING).getOpposite();
        BlockPos startPos = pos.offset(opposite, 2);
        this.room.update(world, startPos);
    }
}
