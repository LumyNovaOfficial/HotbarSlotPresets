/*
 * Copyright (c) 2026 LumyNova
 * Licensed under LSUL-1.0 (Custom). See LICENSE.
 * Similarity note (not intentional): https://modrinth.com/project/qi0EsyTy
 */

package dev.lumynova.hotbarslotpresets.compat;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.lang.reflect.Method;

public final class KeybindCompat {
    public enum KeybindMode {
        SET_BOUND_KEY_FIRST,
        SET_KEY_FIRST
    }

    private KeybindCompat() {
    }

    public static boolean applyBoundKey(KeyBinding binding, InputUtil.Key key) {
        return applyBoundKey(binding, key, KeybindMode.SET_BOUND_KEY_FIRST);
    }

    public static boolean applyBoundKey(KeyBinding binding, InputUtil.Key key, KeybindMode mode) {
        // First try direct mapped calls (stable for normal remapped runtime).
        if (tryDirectMappedCalls(binding, key)) return true;

        if (mode == KeybindMode.SET_KEY_FIRST) {
            if (trySetKey(binding, key)) return true;
            if (trySetBoundKey(binding, key)) return true;
            return false;
        }

        if (trySetBoundKey(binding, key)) return true;
        if (trySetKey(binding, key)) return true;
        return false;
    }

    private static boolean tryDirectMappedCalls(KeyBinding binding, InputUtil.Key key) {
        try {
            binding.setBoundKey(key);
            KeyBinding.updateKeysByCode();
            return true;
        } catch (Throwable ignored) {
            // Fall back to reflection paths below.
        }
        return false;
    }

    private static boolean trySetBoundKey(KeyBinding binding, InputUtil.Key key) {
        try {
            Method setBoundKey = binding.getClass().getMethod("setBoundKey", InputUtil.Key.class);
            setBoundKey.invoke(binding, key);
            refreshKeyMaps();
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean trySetKey(KeyBinding binding, InputUtil.Key key) {
        try {
            Method setKey = binding.getClass().getMethod("setKey", InputUtil.Key.class);
            setKey.invoke(binding, key);
            refreshKeyMaps();
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static void refreshKeyMaps() {
        try {
            Method updateKeysByCode = KeyBinding.class.getMethod("updateKeysByCode");
            updateKeysByCode.invoke(null);
            return;
        } catch (Throwable ignored) {
            // Fallback below.
        }

        try {
            Method updateKeysByCode = KeyBinding.class.getMethod("updateKeysById");
            updateKeysByCode.invoke(null);
        } catch (Throwable ignored) {
            // No-op.
        }
    }
}
