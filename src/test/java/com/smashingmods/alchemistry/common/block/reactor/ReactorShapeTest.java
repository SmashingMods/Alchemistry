package com.smashingmods.alchemistry.common.block.reactor;

import com.smashingmods.alchemistry.testsupport.BootstrappedTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tier-1/2 tests for the rotation geometry in {@link ReactorShape}, built through the {@code Direction}-param
 * constructor so no {@code Level} is needed. Extends {@link BootstrappedTest} only because the class's unrelated
 * {@code createShapeMap} references {@code BlockRegistry}; the geometry itself is pure {@code BlockPos} math.
 *
 * <p>The multiblock is the same size whichever way it faces -- only its orientation rotates -- so the spans of the
 * full box (5x5x5) and the core (1x3x1) are asserted to be identical across all four horizontal facings, and the
 * core is asserted to sit inside the full box.</p>
 */
class ReactorShapeTest extends BootstrappedTest {

    private static final BlockPos ORIGIN = BlockPos.ZERO;
    private static final Direction[] HORIZONTAL = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

    @Test
    void reactorShape_fullBox_perFacing() {
        for (Direction facing : HORIZONTAL) {
            BoundingBox full = new ReactorShape(ORIGIN, ReactorType.FISSION, facing).getFullBoundingBox();

            // fromCorners(pos.up(3).left(2), pos.down(1).back(4).right(2)) -> 5 wide x 5 tall x 5 deep, size-invariant under rotation.
            assertEquals(5, xSpan(full), () -> "full box X span for facing " + facing);
            assertEquals(5, ySpan(full), () -> "full box Y span for facing " + facing);
            assertEquals(5, zSpan(full), () -> "full box Z span for facing " + facing);
        }
    }

    @Test
    void reactorShape_coreContainedPerFacing() {
        for (Direction facing : HORIZONTAL) {
            ReactorShape shape = new ReactorShape(ORIGIN, ReactorType.FISSION, facing);
            BoundingBox full = shape.getFullBoundingBox();
            BoundingBox core = shape.getCoreBoundingBox();

            // fromCorners(pos.back(2), pos.back(2).up(2)) -> 1 wide x 3 tall x 1 deep; the back(2) axis is depth=1 either way.
            assertEquals(1, xSpan(core), () -> "core X span for facing " + facing);
            assertEquals(3, ySpan(core), () -> "core Y span for facing " + facing);
            assertEquals(1, zSpan(core), () -> "core Z span for facing " + facing);

            assertTrue(contains(full, core),
                    () -> "core must sit inside the full box for facing " + facing);
        }
    }

    private static int xSpan(BoundingBox box) {
        return box.maxX() - box.minX() + 1;
    }

    private static int ySpan(BoundingBox box) {
        return box.maxY() - box.minY() + 1;
    }

    private static int zSpan(BoundingBox box) {
        return box.maxZ() - box.minZ() + 1;
    }

    private static boolean contains(BoundingBox outer, BoundingBox inner) {
        return inner.minX() >= outer.minX() && inner.maxX() <= outer.maxX()
                && inner.minY() >= outer.minY() && inner.maxY() <= outer.maxY()
                && inner.minZ() >= outer.minZ() && inner.maxZ() <= outer.maxZ();
    }
}
