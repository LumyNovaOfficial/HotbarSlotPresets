/*
 * Copyright (c) 2026 LumyNova
 * Licensed under LSUL-1.0 (Custom). See LICENSE.
 * Similarity note (not intentional): https://modrinth.com/project/qi0EsyTy
 */

package dev.lumynova.hotbarslotpresets.config;

import dev.lumynova.hotbarslotpresets.HotbarSlotPresetsMod;
import dev.lumynova.hotbarslotpresets.gui.HotbarSlotPresetsScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new HotbarSlotPresetsScreen(parent, HotbarSlotPresetsMod.getConfig());
    }
}
