package io.github.wirelesseye.humanity.block.doorplate;

import io.github.wirelesseye.humanity.block.AllBlockEntityTypes;
import io.github.wirelesseye.humanity.util.VoxelShapeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;


public class Doorplate extends BlockWithEntity {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty OPEN = Properties.OPEN;
    public static final EnumProperty<DoorHinge> HINGE = Properties.DOOR_HINGE;

    private static final VoxelShapeBuilder basicShape = VoxelShapeBuilder.base()
            .position(2, 2, 15).size(12, 7, 1);
    private static final VoxelShape NORTH_SHAPE = basicShape.build();
    private static final VoxelShape NORTH_OPEN_LEFT_SHAPE = basicShape.rotateY(VoxelShapeBuilder.Degree.D270)
            .offsetZ(16).offsetX(-3).build();
    private static final VoxelShape NORTH_OPEN_RIGHT_SHAPE = basicShape.rotateY(VoxelShapeBuilder.Degree.D90)
            .offsetZ(16).offsetX(3).build();
    private static final VoxelShape SOUTH_SHAPE = basicShape.rotateY(VoxelShapeBuilder.Degree.D180).build();
    private static final VoxelShape SOUTH_OPEN_LEFT_SHAPE = basicShape.rotateY(VoxelShapeBuilder.Degree.D90)
            .offsetZ(-16).offsetX(3).build();
    private static final VoxelShape SOUTH_OPEN_RIGHT_SHAPE = basicShape.rotateY(VoxelShapeBuilder.Degree.D270)
            .offsetZ(-16).offsetX(-3).build();
    private static final VoxelShape EAST_SHAPE = basicShape.rotateY(VoxelShapeBuilder.Degree.D90).build();
    private static final VoxelShape EAST_OPEN_LEFT_SHAPE = basicShape.offsetX(-16).offsetZ(-3).build();
    private static final VoxelShape EAST_OPEN_RIGHT_SHAPE = basicShape.rotateY(VoxelShapeBuilder.Degree.D180)
            .offsetX(-16).offsetZ(3).build();
    private static final VoxelShape WEST_SHAPE = basicShape.rotateY(VoxelShapeBuilder.Degree.D270).build();
    private static final VoxelShape WEST_OPEN_LEFT_SHAPE = basicShape.rotateY(VoxelShapeBuilder.Degree.D180)
            .offsetX(16).offsetZ(3).build();
    private static final VoxelShape WEST_OPEN_RIGHT_SHAPE = basicShape.offsetX(16).offsetZ(-3).build();

    public Doorplate() {
        super(FabricBlockSettings.of(Material.WOOD).strength(1.0f).sounds(BlockSoundGroup.WOOD)
                .nonOpaque().noCollision());
        setDefaultState(getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(OPEN, false)
                .with(HINGE, DoorHinge.LEFT));
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction dir = state.get(FACING);
        boolean open = state.get(OPEN);
        DoorHinge hinge = state.get(HINGE);

        return switch (dir) {
            case NORTH -> open
                    ? hinge == DoorHinge.LEFT
                        ? NORTH_OPEN_LEFT_SHAPE
                        : NORTH_OPEN_RIGHT_SHAPE
                    : NORTH_SHAPE;
            case SOUTH -> open
                    ? hinge == DoorHinge.LEFT
                        ? SOUTH_OPEN_LEFT_SHAPE
                        : SOUTH_OPEN_RIGHT_SHAPE
                    : SOUTH_SHAPE;
            case EAST -> open
                    ? hinge == DoorHinge.LEFT
                        ? EAST_OPEN_LEFT_SHAPE
                        : EAST_OPEN_RIGHT_SHAPE
                    : EAST_SHAPE;
            case WEST -> open
                    ? hinge == DoorHinge.LEFT
                        ? WEST_OPEN_LEFT_SHAPE
                        : WEST_OPEN_RIGHT_SHAPE
                    : WEST_SHAPE;
            default -> VoxelShapes.fullCube();
        };
    }

    @Override
    public boolean canMobSpawnInside() {
        return true;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, HINGE);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction placeDirection = state.get(FACING).getOpposite();
        BlockState blockState = world.getBlockState(pos.offset(placeDirection));
        return blockState.getBlock() instanceof DoorBlock && blockState.get(DoorBlock.FACING).equals(placeDirection);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction[] directions = ctx.getPlacementDirections();
        BlockState blockState = this.getDefaultState();
        World worldView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        for (Direction direction : directions) {
            Direction opposite = direction.getOpposite();
            if (direction.getAxis().isHorizontal()
                    && (blockState = blockState.with(FACING, opposite)).canPlaceAt(worldView, blockPos)) {
                BlockState doorBlockState = worldView.getBlockState(blockPos.offset(direction));
                return blockState.with(OPEN, doorBlockState.get(DoorBlock.OPEN))
                        .with(HINGE, doorBlockState.get(DoorBlock.HINGE));
            }
        }
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction.getOpposite() == state.get(FACING) && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        if (world.getBlockState(neighborPos).getBlock() instanceof DoorBlock) {
            boolean open = neighborState.get(DoorBlock.OPEN);
            DoorHinge hinge = neighborState.get(DoorBlock.HINGE);
            return state.with(OPEN, open).with(HINGE, hinge);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Direction opposite = state.get(FACING).getOpposite();
        BlockPos blockPos = pos.offset(opposite);
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.getBlock() instanceof DoorBlock doorBlock) {
            return doorBlock.onUse(blockState, world, blockPos, player, hand, hit);
        }

        return ActionResult.PASS;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public DoorplateEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DoorplateEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
                                                                  BlockEntityType<T> type) {
        return checkType(type, AllBlockEntityTypes.DOORPLATE_ENTITY,
                (world1, pos, state1, blockEntity) -> DoorplateEntity.doTick(world, pos, state, blockEntity));
    }
}
