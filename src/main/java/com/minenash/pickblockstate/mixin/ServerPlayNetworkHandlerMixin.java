package com.minenash.pickblockstate.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {


    @WrapOperation(
            method = "onPickItemFromBlock",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;getPickStack(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;Z)Lnet/minecraft/item/ItemStack;"
            )
    )
    private ItemStack wrapGetPickStack(BlockState state,
                                       WorldView world, BlockPos pos, boolean includeData,
                                       Operation<ItemStack> original) {

        ItemStack stack = original.call(state, world, pos, includeData);

        if(!includeData || state.hasBlockEntity()) {
            return stack;
        }

        BlockStateComponent component = BlockStateComponent.DEFAULT;
        for (var p : state.getProperties())
            component = component.with(p, state);

        stack.applyComponentsFrom( ComponentMap.builder().add(DataComponentTypes.BLOCK_STATE, component).build() );
        return stack;
    }

}

