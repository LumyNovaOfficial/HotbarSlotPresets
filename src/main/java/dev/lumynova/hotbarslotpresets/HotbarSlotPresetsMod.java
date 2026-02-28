/*
 * Copyright (c) 2026 LumyNova
 * Licensed under LSUL-1.0 (Custom). See LICENSE.
 * Similarity note (not intentional): https://modrinth.com/project/qi0EsyTy
 */

package dev.lumynova.hotbarslotpresets;

import dev.lumynova.hotbarslotpresets.compat.KeybindCompat;
import dev.lumynova.hotbarslotpresets.config.HotbarSlotPresetsConfig;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HotbarSlotPresetsMod implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("hotbarslotpresets");
    private static HotbarSlotPresetsConfig config;
    private static int currentState = 0;

    @Override
    public void onInitializeClient() {
        config = HotbarSlotPresetsConfig.load();
        LOGGER.info("HotbarSlotPresets initialized — slot {} | A={} | B={}", config.slot, config.keyA, config.keyB);
        resetState();
    }

    public static HotbarSlotPresetsConfig getConfig() { return config; }

    public static void resetState() {
        currentState = 0;
        applyKey(InputUtil.fromTranslationKey(config.keyA));
    }

    public static void toggle() {
        currentState = (currentState + 1) % 2;
        InputUtil.Key target = currentState == 0
            ? InputUtil.fromTranslationKey(config.keyA)
            : InputUtil.fromTranslationKey(config.keyB);
        applyKey(target);
    }

    public static int getCurrentState() { return currentState; }

    private static void applyKey(InputUtil.Key newKey) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) return;
        int slotIndex = Math.max(0, Math.min(8, config.slot - 1));
        KeyBinding binding = client.options.hotbarKeys[slotIndex];
        boolean ok = KeybindCompat.applyBoundKey(binding, newKey);
        if (ok) {
            LOGGER.info("Slot {} → {}", config.slot, newKey.getTranslationKey());
        } else {
            LOGGER.warn("Failed to apply key {} on slot {}", newKey.getTranslationKey(), config.slot);
        }
    }

    public static String getButtonLabel() {
        String keyName = currentState == 0
            ? InputUtil.fromTranslationKey(config.keyA).getLocalizedText().getString()
            : InputUtil.fromTranslationKey(config.keyB).getLocalizedText().getString();
        return "Slot " + config.slot + " → " + keyName;
    }
}
