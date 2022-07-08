package com.smashingmods.alchemistry.api.blockentity;

import com.smashingmods.alchemistry.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReactorShape {

    private final ReactorType reactorType;

    private final BoundingBox fullBoundingBox;
    private final BoundingBox core;
    private final BoundingBox innerFrontPlane;
    private final BoundingBox innerRearPlane;
    private final BoundingBox innerTopPlane;
    private final BoundingBox innerBottomPlane;
    private final BoundingBox innerLeftPlane;
    private final BoundingBox innerRightPlane;

    private final BoundingBox frontTopBorder;
    private final BoundingBox frontBottomBorder;
    private final BoundingBox leftTopBorder;
    private final BoundingBox leftBottomBorder;
    private final BoundingBox rightTopBorder;
    private final BoundingBox rightBottomBorder;
    private final BoundingBox rearTopBorder;
    private final BoundingBox rearBottomBorder;
    private final BoundingBox frontLeftCornerBorder;
    private final BoundingBox frontRightCornerBorder;
    private final BoundingBox rearLeftCornerBorder;
    private final BoundingBox rearRightCornerBorder;

    public ReactorShape(BlockPos pBlockPos, ReactorType pReactorType, Level pLevel) {

        reactorType = pReactorType;

        Direction facing = pLevel.getBlockState(pBlockPos).getValue(BlockStateProperties.HORIZONTAL_FACING);
        Direction oppositeFacing = facing.getOpposite();
        Direction rightFacing = facing.getCounterClockWise();
        Direction leftFacing = facing.getClockWise();

        BlockPos coreBottom = pBlockPos.relative(oppositeFacing, 2);
        BlockPos coreTop = coreBottom.relative(Direction.UP, 2);
        core = fromCorners(coreBottom, coreTop);

        BlockPos frontTopRight = pBlockPos.relative(Direction.UP, 3).relative(rightFacing, 2);
        BlockPos frontTopLeft = pBlockPos.relative(Direction.UP, 3).relative(leftFacing, 2);
        BlockPos frontBottomRight = pBlockPos.relative(Direction.DOWN, 1).relative(rightFacing, 2);
        BlockPos frontBottomLeft = pBlockPos.relative(Direction.DOWN, 1).relative(leftFacing, 2);
        BlockPos rearTopRight = pBlockPos.relative(Direction.UP, 3).relative(oppositeFacing, 4).relative(rightFacing, 2);
        BlockPos rearTopLeft = pBlockPos.relative(Direction.UP, 3).relative(oppositeFacing, 4).relative(leftFacing, 2);
        BlockPos rearBottomRight = pBlockPos.relative(Direction.DOWN, 1).relative(oppositeFacing, 4).relative(rightFacing, 2);
        BlockPos rearBottomLeft = pBlockPos.relative(Direction.DOWN, 1).relative(oppositeFacing, 4).relative(leftFacing, 2);

        fullBoundingBox = fromCorners(frontTopLeft, rearBottomRight);
        frontTopBorder = fromCorners(frontTopRight, frontTopLeft);
        frontBottomBorder = fromCorners(frontBottomRight, frontBottomLeft);
        leftTopBorder = fromCorners(frontTopLeft, rearTopLeft);
        leftBottomBorder = fromCorners(frontBottomLeft, rearBottomLeft);
        rightTopBorder = fromCorners(frontTopRight, rearTopRight);
        rightBottomBorder = fromCorners(frontBottomRight, rearBottomRight);
        rearTopBorder = fromCorners(rearTopRight, rearTopLeft);
        rearBottomBorder = fromCorners(rearBottomRight, rearBottomLeft);
        frontLeftCornerBorder = fromCorners(frontTopLeft, frontBottomLeft);
        frontRightCornerBorder = fromCorners(frontTopRight, frontBottomRight);
        rearLeftCornerBorder = fromCorners(rearTopLeft, rearBottomLeft);
        rearRightCornerBorder = fromCorners(rearTopRight, rearBottomRight);

        BlockPos innerFrontTopClockwise = pBlockPos.relative(Direction.UP, 3).relative(oppositeFacing, 1).relative(rightFacing, 1);
        BlockPos innerRearTopCounterClockwise = pBlockPos.relative(Direction.UP, 3).relative(oppositeFacing, 3).relative(leftFacing, 1);
        innerTopPlane = fromCorners(innerFrontTopClockwise, innerRearTopCounterClockwise);

        BlockPos innerFrontBottomRight = pBlockPos.relative(Direction.DOWN, 1).relative(oppositeFacing, 1).relative(rightFacing, 1);
        BlockPos innerRearBottomLeft = pBlockPos.relative(Direction.DOWN, 1).relative(oppositeFacing, 3).relative(leftFacing, 1);
        innerBottomPlane = fromCorners(innerFrontBottomRight, innerRearBottomLeft);

        BlockPos innerLeftFrontTop = pBlockPos.relative(Direction.UP, 2).relative(oppositeFacing, 1).relative(leftFacing, 2);
        BlockPos innerLeftRearBottom = pBlockPos.relative(oppositeFacing, 3).relative(leftFacing, 2);
        innerLeftPlane = fromCorners(innerLeftFrontTop, innerLeftRearBottom);

        BlockPos innerRightFrontTop = pBlockPos.relative(Direction.UP, 2).relative(oppositeFacing, 1).relative(rightFacing, 2);
        BlockPos innerRightRearBottom = pBlockPos.relative(oppositeFacing, 3).relative(rightFacing, 2);
        innerRightPlane = fromCorners(innerRightFrontTop, innerRightRearBottom);

        BlockPos innerFrontLeftTop = pBlockPos.relative(Direction.UP, 2).relative(leftFacing, 1);
        BlockPos innerFrontRightBottom = pBlockPos.relative(rightFacing, 1);
        innerFrontPlane = fromCorners(innerFrontLeftTop, innerFrontRightBottom);

        BlockPos innerRearLeftTop = pBlockPos.relative(Direction.UP, 2).relative(oppositeFacing, 4).relative(leftFacing, 1);
        BlockPos innerRearRightBottom = pBlockPos.relative(oppositeFacing, 4).relative(rightFacing, 1);
        innerRearPlane = fromCorners(innerRearLeftTop, innerRearRightBottom);
    }

    public Map<BoundingBox, List<Block>> createShapeMap() {
        Map<BoundingBox, List<Block>> reactorShapeMap = new HashMap<>();

        Block coreComponent = switch (reactorType) {
            case FUSION -> BlockRegistry.FUSION_CORE.get();
            case FISSION -> BlockRegistry.FISSION_CORE.get();
        };

        Block controllerComponent = switch(reactorType) {
            case FUSION -> BlockRegistry.FUSION_CONTROLLER.get();
            case FISSION -> BlockRegistry.FISSION_CONTROLLER.get();
        };

        reactorShapeMap.put(core, List.of(coreComponent));

        reactorShapeMap.put(frontTopBorder, List.of(BlockRegistry.REACTOR_CASING.get(), BlockRegistry.REACTOR_ENERGY_INPUT.get(), BlockRegistry.REACTOR_ITEM_INPUT.get(), BlockRegistry.REACTOR_ITEM_OUTPUT.get()));
        reactorShapeMap.put(frontBottomBorder, List.of(BlockRegistry.REACTOR_CASING.get(), BlockRegistry.REACTOR_ENERGY_INPUT.get(), BlockRegistry.REACTOR_ITEM_INPUT.get(), BlockRegistry.REACTOR_ITEM_OUTPUT.get()));
        reactorShapeMap.put(rearTopBorder, List.of(BlockRegistry.REACTOR_CASING.get(), BlockRegistry.REACTOR_ENERGY_INPUT.get(), BlockRegistry.REACTOR_ITEM_INPUT.get(), BlockRegistry.REACTOR_ITEM_OUTPUT.get()));
        reactorShapeMap.put(rearBottomBorder, List.of(BlockRegistry.REACTOR_CASING.get(), BlockRegistry.REACTOR_ENERGY_INPUT.get(), BlockRegistry.REACTOR_ITEM_INPUT.get(), BlockRegistry.REACTOR_ITEM_OUTPUT.get()));
        reactorShapeMap.put(leftTopBorder, List.of(BlockRegistry.REACTOR_CASING.get(), BlockRegistry.REACTOR_ENERGY_INPUT.get(), BlockRegistry.REACTOR_ITEM_INPUT.get(), BlockRegistry.REACTOR_ITEM_OUTPUT.get()));
        reactorShapeMap.put(leftBottomBorder, List.of(BlockRegistry.REACTOR_CASING.get(), BlockRegistry.REACTOR_ENERGY_INPUT.get(), BlockRegistry.REACTOR_ITEM_INPUT.get(), BlockRegistry.REACTOR_ITEM_OUTPUT.get()));
        reactorShapeMap.put(rightTopBorder, List.of(BlockRegistry.REACTOR_CASING.get(), BlockRegistry.REACTOR_ENERGY_INPUT.get(), BlockRegistry.REACTOR_ITEM_INPUT.get(), BlockRegistry.REACTOR_ITEM_OUTPUT.get()));
        reactorShapeMap.put(rightBottomBorder, List.of(BlockRegistry.REACTOR_CASING.get(), BlockRegistry.REACTOR_ENERGY_INPUT.get(), BlockRegistry.REACTOR_ITEM_INPUT.get(), BlockRegistry.REACTOR_ITEM_OUTPUT.get()));
        reactorShapeMap.put(frontLeftCornerBorder, List.of(BlockRegistry.REACTOR_CASING.get(), BlockRegistry.REACTOR_ENERGY_INPUT.get(), BlockRegistry.REACTOR_ITEM_INPUT.get(), BlockRegistry.REACTOR_ITEM_OUTPUT.get()));
        reactorShapeMap.put(frontRightCornerBorder, List.of(BlockRegistry.REACTOR_CASING.get(), BlockRegistry.REACTOR_ENERGY_INPUT.get(), BlockRegistry.REACTOR_ITEM_INPUT.get(), BlockRegistry.REACTOR_ITEM_OUTPUT.get()));
        reactorShapeMap.put(rearLeftCornerBorder, List.of(BlockRegistry.REACTOR_CASING.get(), BlockRegistry.REACTOR_ENERGY_INPUT.get(), BlockRegistry.REACTOR_ITEM_INPUT.get(), BlockRegistry.REACTOR_ITEM_OUTPUT.get()));
        reactorShapeMap.put(rearRightCornerBorder, List.of(BlockRegistry.REACTOR_CASING.get(), BlockRegistry.REACTOR_ENERGY_INPUT.get(), BlockRegistry.REACTOR_ITEM_INPUT.get(), BlockRegistry.REACTOR_ITEM_OUTPUT.get()));

        reactorShapeMap.put(innerTopPlane, List.of(BlockRegistry.REACTOR_CASING.get()));
        reactorShapeMap.put(innerBottomPlane, List.of(BlockRegistry.REACTOR_CASING.get()));
        reactorShapeMap.put(innerFrontPlane, List.of(BlockRegistry.REACTOR_CASING.get(), BlockRegistry.REACTOR_GLASS.get(), controllerComponent));
        reactorShapeMap.put(innerRearPlane, List.of(BlockRegistry.REACTOR_GLASS.get(), BlockRegistry.REACTOR_GLASS.get()));
        reactorShapeMap.put(innerLeftPlane, List.of(BlockRegistry.REACTOR_GLASS.get(), BlockRegistry.REACTOR_GLASS.get()));
        reactorShapeMap.put(innerRightPlane, List.of(BlockRegistry.REACTOR_GLASS.get(), BlockRegistry.REACTOR_GLASS.get()));

        return reactorShapeMap;
    }

    private static BoundingBox fromCorners(BlockPos pStart, BlockPos pEnd) {
        return BoundingBox.fromCorners(blockPosToVec3i(pStart), blockPosToVec3i(pEnd));
    }

    private static Vec3i blockPosToVec3i(BlockPos pBlockPos) {
        return new Vec3i(pBlockPos.getX(), pBlockPos.getY(), pBlockPos.getZ());
    }

    public BoundingBox getFullBoundingBox() {
        return fullBoundingBox;
    }
}
