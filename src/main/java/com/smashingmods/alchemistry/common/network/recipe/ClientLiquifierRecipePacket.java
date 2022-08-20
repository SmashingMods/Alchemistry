package com.smashingmods.alchemistry.common.network.recipe;

import com.smashingmods.alchemistry.api.item.IngredientStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientLiquifierRecipePacket {

    private final BlockPos blockPos;
    private final IngredientStack input;

    public ClientLiquifierRecipePacket(BlockPos pBlockPos, IngredientStack pInput) {
        this.blockPos = pBlockPos;
        this.input = pInput;
    }

    public ClientLiquifierRecipePacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.input = IngredientStack.fromNetwork(pBuffer);
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        input.toNetwork(pBuffer);
    }

    public static void handle(final ClientLiquifierRecipePacket pPacket, Supplier<NetworkEvent.Context> pContext) {
        pContext.get().enqueueWork(() -> {

//            if (pContext.get().getDirection().getReceptionSide().isClient()) {
//                Level level = Minecraft.getInstance().level;
//                if (level != null) {
//                    ProcessingBlockEntity blockEntity = (ProcessingBlockEntity) level.getBlockEntity(pPacket.blockPos);
//                    if (blockEntity != null) {
//                        RecipeRegistry.getRecipesByType(RecipeRegistry.LIQUIFIER_TYPE.get(), level).stream()
//                                .filter(recipe -> Arrays.stream(recipe.getInput().getIngredient().getItems()).allMatch(pPacket.input.getIngredient()))
//                                .findFirst()
//                                .ifPresent(blockEntity::setRecipe);
//                    }
//                }
//            }
        });
        pContext.get().setPacketHandled(true);
    }
}
