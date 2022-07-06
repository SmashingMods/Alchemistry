package com.smashingmods.alchemistry.api.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class ReactorShape {

    public final BoundingBox CORE;
    public final BoundingBox FRONT_PLANE;
    public final BoundingBox REAR_PLANE;
    public final BoundingBox TOP_PLANE;
    public final BoundingBox BOTTOM_PLANE;
    public final BoundingBox LEFT_PLANE;
    public final BoundingBox RIGHT_PLANE;

    public ReactorShape(BlockPos pBlockPos, Level pLevel) {
        Direction frontFacing = pLevel.getBlockState(pBlockPos).getValue(BlockStateProperties.HORIZONTAL_FACING);
        Direction oppositeFacing = frontFacing.getOpposite();
        Direction leftFacing = frontFacing.getCounterClockWise();
        Direction rightFacing = frontFacing.getClockWise();

        BlockPos coreBottom = pBlockPos.relative(oppositeFacing, 2);
        BlockPos coreTop = coreBottom.relative(Direction.UP, 2);
        BlockPos frontTopClockwise = pBlockPos.relative(Direction.UP, 3).relative(leftFacing, 2);
        BlockPos frontTopCounterClockwise = pBlockPos.relative(Direction.UP, 3).relative(rightFacing, 2);
        BlockPos frontBottomClockwise = pBlockPos.relative(Direction.DOWN, 1).relative(leftFacing, 2);
        BlockPos frontBottomCounterClockwise = pBlockPos.relative(Direction.DOWN, 1).relative(rightFacing, 2);
        BlockPos rearTopClockwise = pBlockPos.relative(Direction.UP, 3).relative(oppositeFacing, 4).relative(leftFacing, 2);
        BlockPos rearTopCounterClockwise = pBlockPos.relative(Direction.UP, 3).relative(oppositeFacing, 4).relative(rightFacing, 2);
        BlockPos rearBottomClockwise = pBlockPos.relative(Direction.DOWN, 1).relative(oppositeFacing, 4).relative(leftFacing, 2);
        BlockPos rearBottomCounterClockwise = pBlockPos.relative(Direction.DOWN, 1).relative(oppositeFacing, 4).relative(rightFacing, 2);

        CORE = BoundingBox.fromCorners(blockPosToVec3i(coreBottom), blockPosToVec3i(coreTop));
        FRONT_PLANE = BoundingBox.fromCorners(blockPosToVec3i(frontBottomClockwise), blockPosToVec3i(frontTopCounterClockwise));
        REAR_PLANE = BoundingBox.fromCorners(blockPosToVec3i(rearBottomClockwise), blockPosToVec3i(rearTopCounterClockwise));
        TOP_PLANE = BoundingBox.fromCorners(blockPosToVec3i(frontTopClockwise), blockPosToVec3i(rearTopCounterClockwise));
        BOTTOM_PLANE = BoundingBox.fromCorners(blockPosToVec3i(frontBottomClockwise), blockPosToVec3i(rearBottomCounterClockwise));
        LEFT_PLANE = BoundingBox.fromCorners(blockPosToVec3i(frontBottomCounterClockwise), blockPosToVec3i(rearTopCounterClockwise));
        RIGHT_PLANE = BoundingBox.fromCorners(blockPosToVec3i(frontBottomClockwise), blockPosToVec3i(rearTopClockwise));
    }

    public static ReactorShape create(BlockPos pBlockPos, Level pLevel) {
        return new ReactorShape(pBlockPos, pLevel);
    }

    private Vec3i blockPosToVec3i(BlockPos pBlockPos) {
        return new Vec3i(pBlockPos.getX(), pBlockPos.getY(), pBlockPos.getZ());
    }
}
