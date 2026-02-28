package dev.lumynova.hotbarslotpresets.mixin;

import dev.lumynova.hotbarslotpresets.HotbarSlotPresetsMod;
import dev.lumynova.hotbarslotpresets.config.HotbarSlotPresetsConfig;
import dev.lumynova.hotbarslotpresets.optimization.FoliageCullingEngine;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.client.render.block.BlockRenderManager")
public abstract class BlockRenderManagerMixin {
    @Inject(method = "renderBlock", at = @At("HEAD"), cancellable = true, require = 0)
    private void hotbarslotpresets$cullFoliage(
        BlockState state,
        BlockPos pos,
        Object world,
        Object matrices,
        Object vertexConsumer,
        boolean cull,
        Object random,
        CallbackInfoReturnable<Boolean> cir
    ) {
        HotbarSlotPresetsConfig config = HotbarSlotPresetsMod.getConfig();
        if (config == null) {
            return;
        }

        if (FoliageCullingEngine.shouldSkip(state, pos, config)) {
            cir.setReturnValue(false);
        }
    }
}
