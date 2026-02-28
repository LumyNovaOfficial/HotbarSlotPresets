/*
 * Copyright (c) 2026 LumyNova
 * Licensed under LSUL-1.0 (Custom). See LICENSE.
 * Similarity note (not intentional): https://modrinth.com/project/qi0EsyTy
 */

package dev.lumynova.hotbarslotpresets.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.lumynova.hotbarslotpresets.HotbarSlotPresetsMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HotbarSlotPresetsConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("hotbarslotpresets.json");

    public int slot = 5;
    public String keyA = "key.keyboard.5";
    public String keyB = "key.keyboard.f";

    public static HotbarSlotPresetsConfig load() {
        if (!Files.exists(CONFIG_PATH)) {
            HotbarSlotPresetsConfig d = new HotbarSlotPresetsConfig();
            save(d);
            return d;
        }
        try {
            return GSON.fromJson(Files.readString(CONFIG_PATH), HotbarSlotPresetsConfig.class);
        } catch (IOException e) {
            HotbarSlotPresetsMod.LOGGER.error("Failed to load config", e);
            return new HotbarSlotPresetsConfig();
        }
    }

    public static void save(HotbarSlotPresetsConfig config) {
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(config));
        } catch (IOException e) {
            HotbarSlotPresetsMod.LOGGER.error("Failed to save config", e);
        }
    }
}
