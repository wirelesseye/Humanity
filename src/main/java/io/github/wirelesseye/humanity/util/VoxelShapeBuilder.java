package io.github.wirelesseye.humanity.util;

import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;


public class VoxelShapeBuilder implements Cloneable {
    private static final float PX = 1f / 16f;

    private float x;
    private float y;
    private float z;
    private float sizeX;
    private float sizeY;
    private float sizeZ;

    public enum Degree {
        D90,
        D180,
        D270,
    }

    private VoxelShapeBuilder() {}

    public static VoxelShapeBuilder base() {
        return new VoxelShapeBuilder();
    }

    public VoxelShapeBuilder position(float x, float y, float z) {
        VoxelShapeBuilder shape = this.clone();
        shape.x = x;
        shape.y = y;
        shape.z = z;
        return shape;
    }

    public VoxelShapeBuilder size(float sizeX, float sizeY, float sizeZ) {
        VoxelShapeBuilder shape = this.clone();
        shape.sizeX = sizeX;
        shape.sizeY = sizeY;
        shape.sizeZ = sizeZ;
        return shape;
    }

    public VoxelShapeBuilder rotateY(Degree degree) {
        VoxelShapeBuilder shape = this.clone();

        if (degree == Degree.D90) {
            shape.x = 16 - (this.z + this.sizeZ);
            shape.z = this.x;
            shape.sizeX = this.sizeZ;
            shape.sizeZ = this.sizeX;
        } else if (degree == Degree.D180) {
            shape.z = 16 - (this.z + this.sizeZ);
        } else {
            shape.x = this.z;
            shape.z = 16 - (this.x + this.sizeX);
            shape.sizeX = this.sizeZ;
            shape.sizeZ = this.sizeX;
        }

        return shape;
    }

    public VoxelShapeBuilder offsetX(float offset) {
        VoxelShapeBuilder shape = this.clone();
        shape.x += offset;
        return shape;
    }

    public VoxelShapeBuilder offsetZ(float offset) {
        VoxelShapeBuilder shape = this.clone();
        shape.z += offset;
        return shape;
    }

    public VoxelShape build() {
        return VoxelShapes.cuboid(
                this.x * PX,
                this.y * PX,
                this.z * PX,
                (this.x + this.sizeX) * PX,
                (this.y + this.sizeY) * PX,
                (this.z + this.sizeZ) * PX);
    }

    @Override
    public VoxelShapeBuilder clone() {
        try {
            return (VoxelShapeBuilder) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
