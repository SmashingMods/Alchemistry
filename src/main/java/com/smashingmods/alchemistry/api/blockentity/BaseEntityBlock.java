package com.smashingmods.alchemistry.api.blockentity;

import com.mojang.datafixers.util.Function3;
import com.smashingmods.alchemistry.api.container.BaseContainer;
import com.smashingmods.alchemistry.blocks.AlchemistryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiFunction;

public abstract class BaseEntityBlock extends Block implements EntityBlock {

    BiFunction<BlockPos, BlockState, BlockEntity> blockEntity;
    Function3<Integer, BlockPos, Inventory, BaseContainer> containerFunction;

    public BaseEntityBlock(Block.Properties properties, BiFunction<BlockPos, BlockState, BlockEntity> blockEntity, Function3<Integer, BlockPos, Inventory, BaseContainer> containerFunction) {
        super(properties);
        this.blockEntity = blockEntity;
        this.containerFunction = containerFunction;
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(@Nonnull BlockState state, @Nonnull LootContext.Builder builder) {
        List<ItemStack> output = super.getDrops(state, builder);
        ItemStack itemStack = output.stream().filter(stack -> Block.byItem(stack.getItem()) == this).findFirst().orElse(ItemStack.EMPTY);

        if (!itemStack.isEmpty()) {
            itemStack.setTag(builder.getParameter(LootContextParams.BLOCK_ENTITY).getUpdateTag());
            if (itemStack.getTag() != null) {
                itemStack.getTag().remove("x");
                itemStack.getTag().remove("y");
                itemStack.getTag().remove("z");
            }
        }
        return output;
    }

    @Override
    public void setPlacedBy(@Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull BlockState pState, @Nullable LivingEntity pPlacer, @Nonnull ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);

        if (blockEntity instanceof AlchemistryBlockEntity && pStack.hasTag()) {

            CompoundTag tag = pStack.getOrCreateTag();
            tag.putInt("x", pPos.getX());
            tag.putInt("y", pPos.getY());
            tag.putInt("z", pPos.getZ());
            blockEntity.load(tag);
            blockEntity.setChanged();
        }
    }

    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pPos, @Nonnull BlockState pState) {
        return blockEntity.apply(pPos, pState);
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public InteractionResult use(@Nonnull BlockState pState, Level pLevel, @Nonnull BlockPos pPos, @Nonnull Player pPlayer, @Nonnull InteractionHand pHand, @Nonnull BlockHitResult pBlockHitResult) {
        if (!pLevel.isClientSide) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            boolean interactionSuccessful = true;
            if (blockEntity instanceof AlchemistryBlockEntity) {
                interactionSuccessful = ((AlchemistryBlockEntity) blockEntity).onBlockActivated(pLevel, pPos, pPlayer, pHand);
            }

            MenuProvider provider = new MenuProvider() {
                @Override
                @Nonnull
                public Component getDisplayName() {
                    return new TextComponent("");
                }

                @Override
                public AbstractContainerMenu createMenu(int pContainerId, @Nonnull Inventory pInventory, @Nonnull Player pPlayer) {
                    return containerFunction.apply(pContainerId, pPos, pInventory);
                }
            };

            if (!interactionSuccessful) {
                NetworkHooks.openGui((ServerPlayer) pPlayer, provider, blockEntity.getBlockPos());
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }
}
