package com.minenash.pickblockstate.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

	@Shadow public ClientPlayerEntity player;

	@Redirect(method = "doItemPick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getPickStack(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;"))
	public ItemStack getPickStack(Block block, BlockView world, BlockPos pos, BlockState state) {
		ItemStack stack = block.getPickStack(world, pos, state);

		if (!player.abilities.creativeMode || !Screen.hasControlDown() || block.hasBlockEntity())
			return stack;

		CompoundTag compoundTag = stack.getOrCreateTag();
		CompoundTag compoundTag3;
		if (compoundTag.contains("BlockStateTag", 10)) {
			compoundTag3 = compoundTag.getCompound("BlockStateTag");
		} else {
			compoundTag3 = new CompoundTag();
			compoundTag.put("BlockStateTag", compoundTag3);
		}

		for (Property property : state.getProperties()) {
			if (state.contains(property))
				compoundTag3.putString(property.getName(), property.name(state.get(property)));
		}

		return stack;

	}

}
