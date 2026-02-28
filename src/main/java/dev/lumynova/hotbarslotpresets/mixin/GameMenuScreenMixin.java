/*
 * Copyright (c) 2026 LumyNova
 * Licensed under LSUL-1.0 (Custom). See LICENSE.
 * Similarity note (not intentional): https://modrinth.com/project/qi0EsyTy
 */

package dev.lumynova.hotbarslotpresets.mixin;

import dev.lumynova.hotbarslotpresets.HotbarSlotPresetsMod;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {

    protected GameMenuScreenMixin() { super(Text.empty()); }

    @Inject(method = "init", at = @At("TAIL"))
    private void hotbarslotpresets$addButton(CallbackInfo ci) {
        ButtonWidget reference = hotbarslotpresets$findExitButton();
        int x = reference != null ? reference.getX() : (width / 2 - 102);
        int y = reference != null
            ? reference.getY() + 24
            : (height / 4 + 144);
        int w = reference != null ? reference.getWidth() : 204;

        addDrawableChild(ButtonWidget.builder(
            Text.literal(HotbarSlotPresetsMod.getButtonLabel()),
            btn -> {
                HotbarSlotPresetsMod.toggle();
                btn.setMessage(Text.literal(HotbarSlotPresetsMod.getButtonLabel()));
            })
            .dimensions(x, y, w, 20)
            .build());
    }

    private ButtonWidget hotbarslotpresets$findExitButton() {
        List<ButtonWidget> buttons = new ArrayList<>();
        for (var element : children()) {
            if (element instanceof ButtonWidget button) {
                buttons.add(button);
            }
        }

        for (ButtonWidget button : buttons) {
            String key = hotbarslotpresets$translationKey(button.getMessage().getContent());
            if ("menu.disconnect".equals(key) || "menu.returnToMenu".equals(key)) {
                return button;
            }
        }

        return buttons.isEmpty() ? null : buttons.get(buttons.size() - 1);
    }

    private String hotbarslotpresets$translationKey(TextContent content) {
        if (content instanceof TranslatableTextContent translatable) {
            return translatable.getKey();
        }
        return "";
    }
}
