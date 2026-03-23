package dev.lumynova.hotbarslotpresets.mixin;

import dev.lumynova.hotbarslotpresets.HotbarSlotPresetsMod;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {

    protected GameMenuScreenMixin() { super(Text.empty()); }

    @Inject(method = "init", at = @At("TAIL"))
    private void hotbarslotpresets$addButton(CallbackInfo ci) {
        ButtonWidget topmost = null;
        for (var element : children()) {
            if (element instanceof ButtonWidget btn) {
                if (topmost == null || btn.getY() < topmost.getY()) {
                    topmost = btn;
                }
            }
        }

        int w = topmost != null ? topmost.getWidth() : 204;
        int x = topmost != null ? topmost.getX() : (width / 2 - 102);
        int topmostY = topmost != null ? topmost.getY() : (height / 4 + 48);
        int y = Math.max(8, topmostY - 63);

        addDrawableChild(ButtonWidget.builder(
            Text.literal(HotbarSlotPresetsMod.getButtonLabel()),
            btn -> {
                HotbarSlotPresetsMod.toggle();
                btn.setMessage(Text.literal(HotbarSlotPresetsMod.getButtonLabel()));
            })
            .dimensions(x, y, w, 20)
            .build());
    }
}
