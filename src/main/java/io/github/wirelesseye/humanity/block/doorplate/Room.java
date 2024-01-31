package io.github.wirelesseye.humanity.block.doorplate;

import io.github.wirelesseye.humanity.entity.AllEntityTypes;
import io.github.wirelesseye.humanity.entity.TileUpdateAnimEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Collectors;

public class Room {
    private static class RoomChecker {
        protected LinkedList<ArrayList<BlockPos>> layeredTiles = new LinkedList<>();
        protected HashSet<BlockPos> tiles = new HashSet<>();
        protected LinkedList<BlockPos> visitQueue = new LinkedList<>();
        protected HashSet<BlockPos> visited = new HashSet<>();
        protected int layer = 0;
        protected boolean isStopCheck = false;

        protected void check(World world, BlockPos startPos) {
            this.visitQueue.add(startPos);

            while (!this.visitQueue.isEmpty() && !this.isStopCheck) {
                BlockPos blockPos = this.visitQueue.poll();
                this.checkBlock(world, blockPos);
            }

            this.tiles = this.layeredTiles.stream().flatMap(List::stream)
                    .collect(Collectors.toCollection(HashSet::new));
        }

        private void addVisited(BlockPos blockPos) {
            this.visited.add(blockPos);
        }

        private boolean hasVisited(BlockPos blockPos) {
            return this.visited.contains(blockPos);
        }

        private void checkBlock(World world, BlockPos blockPos) {
            if (this.hasVisited(blockPos) || this.isStopCheck) {
                return;
            }

            this.addVisited(blockPos);

            if (this.layeredTiles.size() > 300) {
                this.isStopCheck = true;
                return;
            }

            if (this.hasBlock(world, blockPos)) {
                if (!this.hasBlock(world, blockPos.up()) && !this.hasBlock(world, blockPos.up(2))) {
                    this.checkBlock(world, blockPos.up());
                }
            } else if (!this.isOnGround(world, blockPos)) {
                if (this.isOnGround(world, blockPos.down())) {
                    this.checkBlock(world, blockPos.down());
                }
            } else if (!this.hasBlock(world, blockPos.up())) {
                this.addTile(blockPos);
                this.layer++;
                if (!this.hasVisited(blockPos.north())) {
                    this.visitQueue.add(blockPos.north());
                }
                if (!this.hasVisited(blockPos.south())) {
                    this.visitQueue.add(blockPos.south());
                }
                if (!this.hasVisited(blockPos.west())) {
                    this.visitQueue.add(blockPos.west());
                }
                if (!this.hasVisited(blockPos.east())) {
                    this.visitQueue.add(blockPos.east());
                }
            }
        }

        private boolean hasBlock(World world, BlockPos blockPos) {
            BlockState blockState = world.getBlockState(blockPos);
            return blockState != null && !blockState.getCollisionShape(world, blockPos).isEmpty();
        }

        private boolean isOnGround(World world, BlockPos blockPos) {
            return this.hasBlock(world, blockPos.down());
        }

        private void addTile(BlockPos blockPos) {
            if (this.layer == this.layeredTiles.size()) {
                this.layeredTiles.add(new ArrayList<>());
            }
            this.layeredTiles.get(this.layer).add(blockPos);
        }
    }

    private RoomChecker checker = new RoomChecker();
    private boolean hasRoomTilesChanged = false;

    public void update(World world, BlockPos startPos) {
        HashSet<BlockPos> prevRoomTiles = this.checker.tiles;

        this.checker = new RoomChecker();
        this.checker.check(world, startPos);
        this.hasRoomTilesChanged = !this.checker.tiles.equals(prevRoomTiles);
    }

    public Set<BlockPos> getRoomTiles() {
        return this.checker.tiles;
    }

    public boolean hasRoomTilesChanged() {
        return this.hasRoomTilesChanged;
    }

    public void tickAnimation(World world) {
        List<BlockPos> tiles = this.checker.layeredTiles.poll();
        if (tiles != null) {
            for (BlockPos tile : tiles) {
                this.playTileUpdateAnimation(world, tile);
            }
        }
    }

    private void playTileUpdateAnimation(World world, BlockPos blockPos) {
        TileUpdateAnimEntity activateGrid = new TileUpdateAnimEntity(AllEntityTypes.TILE_UPDATE_ANIM, world);
        activateGrid.setPosition(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
        world.spawnEntity(activateGrid);
    }
}
