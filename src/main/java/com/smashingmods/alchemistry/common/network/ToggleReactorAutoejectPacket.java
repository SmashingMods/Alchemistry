package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.block.reactor.AbstractReactorBlockEntity;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ToggleReactorAutoejectPacket implements AlchemyPacket {

    public static final ResourceLocation ID = new ResourceLocation(Alchemistry.MODID, "toggle_reactor_autoeject");

    private final BlockPos blockPos;

    private final boolean autoeject;

    public ToggleReactorAutoejectPacket(BlockPos blockPos, boolean autoeject) {
        this.blockPos = blockPos;
        this.autoeject = autoeject;
    }

    public ToggleReactorAutoejectPacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.autoeject = pBuffer.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeBoolean(autoeject);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public void handle(PlayPayloadContext pContext) {
        pContext.player().ifPresent(player -> {
            if (player.level().getBlockEntity(blockPos) instanceof AbstractReactorBlockEntity reactorController) {
                reactorController.setAutoeject(autoeject);
                if (autoeject) {
                    reactorController.tryEjectOutputs();
                }
                reactorController.setChanged();
            }
        });
    }
}
