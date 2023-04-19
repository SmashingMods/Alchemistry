package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.common.block.reactor.AbstractReactorBlockEntity;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent.Context;

public class SetReactorIOModePacket implements AlchemyPacket {
    private final BlockPos blockPos;

    /**
     * True if the reactor should actively seek it's input (e.g. from a chest).
     * Otherwise the reactor will seek it's input passively - which means that it needs
     * to be feed it's input through hoppers or "pushing" pipes.
     *
     * <p>The input mode of a reactor is not yet implemented. However since it
     * will most likely be implemented in the future, the mode is already part
     * of the packet.
     */
    private final boolean inuptActive;

    private final boolean outputActive;

    public SetReactorIOModePacket(BlockPos blockPos, boolean inputActive, boolean outputActive) {
        this.blockPos = blockPos;
        this.inuptActive = inputActive;
        this.outputActive = outputActive;
    }

    public SetReactorIOModePacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.inuptActive = pBuffer.readBoolean();
        this.outputActive = pBuffer.readBoolean();
    }

    @Override
    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeBoolean(inuptActive);
        pBuffer.writeBoolean(outputActive);
    }

    @Override
    public void handle(Context pContext) {
        Player player = pContext.getSender();
        if (player != null) {
            AbstractProcessingBlockEntity blockEntity = (AbstractProcessingBlockEntity) player.level.getBlockEntity(blockPos);

            if (blockEntity instanceof AbstractReactorBlockEntity reactorController) {
                reactorController.setPushingOutputActively(outputActive);
                if (outputActive) {
                    reactorController.tryPushOutputs();
                }
                blockEntity.setChanged();
            }
        }
    }
}
