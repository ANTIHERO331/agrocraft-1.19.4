package com.agrocraft.mixin.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ToolItem;
import net.minecraft.resource.featuretoggle.ToggleableFeature;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public abstract class AbstractBlockMixin implements ToggleableFeature {
    @Inject(method = "calcBlockBreakingDelta", at = @At("HEAD"), cancellable = true)
    private void agrocraft$calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        float hardness = state.getHardness(world, pos);
        if (!player.canHarvest(state) || hardness < 0) cir.setReturnValue(0.0f);
        else {
            int foodLevel = player.getHungerManager().getFoodLevel();
            int harvestLevel = player.getMainHandStack().getItem() instanceof ToolItem ? 6 : 30;
            int restrictedHarvestLevel = player.getMainHandStack().getItem() instanceof ToolItem ? 20 - (int) (2.8 * foodLevel) : 100 - 14 * foodLevel;
            int multiplier = foodLevel >= 5 ? harvestLevel : restrictedHarvestLevel;
            cir.setReturnValue(player.getBlockBreakingSpeed(state) / hardness / (float) (multiplier * 20));
        }
    }
}
