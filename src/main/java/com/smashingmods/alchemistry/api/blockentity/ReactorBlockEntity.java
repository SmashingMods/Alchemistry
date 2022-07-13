package com.smashingmods.alchemistry.api.blockentity;

import com.mojang.datafixers.util.Function3;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface ReactorBlockEntity {

    ReactorShape getReactorShape();

    void setReactorShape(ReactorShape pReactorShape);

    ReactorType getReactorType();

    void setReactorType(ReactorType pReactorType);

    void setMultiblockHandlers();

    void setPowerState(PowerState pPowerState);

    default Function3<BoundingBox, List<Block>, Level, Boolean> blockPredicate() {
        return (box, blockList, level) -> BlockPos.betweenClosedStream(box).allMatch(blockPos -> blockList.contains(level.getBlockState(blockPos).getBlock()));
    }

    default boolean validateMultiblockShape(Level pLevel, Map<BoundingBox, List<Block>> pMap) {
        List<Boolean> checks = new ArrayList<>();
        pMap.forEach((box, list) -> checks.add(blockPredicate().apply(box, list, pLevel)));
        return checks.stream().allMatch(check -> check);
    }

    boolean isValidMultiblock();
}
