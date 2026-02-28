/*
 * Copyright (c) 2026 LumyNova
 * Licensed under LSUL-1.0 (Custom). See LICENSE.
 * Similarity note (not intentional): https://modrinth.com/project/qi0EsyTy
 */

package dev.lumynova.hotbarslotpresets.gui;

import dev.lumynova.hotbarslotpresets.HotbarSlotPresetsMod;
import dev.lumynova.hotbarslotpresets.config.HotbarSlotPresetsConfig;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class HotbarSlotPresetsScreen extends Screen {
    private static final int MAX_MOUSE_BUTTONS = 8;

    private final Screen parent;
    private final HotbarSlotPresetsConfig config;
    private int listening = 0;
    private boolean ignoreMouseUntilRelease;
    private final boolean[] keyWasDown = new boolean[GLFW.GLFW_KEY_LAST + 1];
    private final boolean[] mouseWasDown = new boolean[MAX_MOUSE_BUTTONS];

    private ButtonWidget slotLabel;
    private ButtonWidget keyABtn;
    private ButtonWidget keyBBtn;

    public HotbarSlotPresetsScreen(Screen parent, HotbarSlotPresetsConfig config) {
        super(Text.literal("Hotbar Slot Presets"));
        this.parent = parent;
        this.config = config;
    }

    @Override
    protected void init() {
        int cx = width / 2;
        int startY = height / 2 - 40;

        addDrawableChild(ButtonWidget.builder(Text.literal("◀"), b -> {
            config.slot = Math.max(1, config.slot - 1);
            HotbarSlotPresetsMod.resetState();
            HotbarSlotPresetsConfig.save(config);
            refresh();
        }).dimensions(cx - 60, startY, 20, 20).build());

        slotLabel = addDrawableChild(ButtonWidget.builder(Text.literal("Slot: " + config.slot), b -> {})
            .dimensions(cx - 38, startY, 76, 20).build());
        slotLabel.active = false;

        addDrawableChild(ButtonWidget.builder(Text.literal("▶"), b -> {
            config.slot = Math.min(9, config.slot + 1);
            HotbarSlotPresetsMod.resetState();
            HotbarSlotPresetsConfig.save(config);
            refresh();
        }).dimensions(cx + 40, startY, 20, 20).build());

        keyABtn = addDrawableChild(ButtonWidget.builder(rowLabel(config.slot, config.keyA), b -> {
            startListening(1);
        }).dimensions(cx - 60, startY + 26, 120, 20).build());

        keyBBtn = addDrawableChild(ButtonWidget.builder(rowLabel(config.slot, config.keyB), b -> {
            startListening(2);
        }).dimensions(cx - 60, startY + 52, 120, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
            .dimensions(cx - 40, startY + 84, 80, 20).build());
    }

    private Text rowLabel(int slot, String tk) {
        String name = InputUtil.fromTranslationKey(tk).getLocalizedText().getString();
        return Text.literal("Slot " + slot + " -> " + name);
    }

    private void startListening(int mode) {
        listening = mode;
        ignoreMouseUntilRelease = true;
        setFocused(null);
        clearInputState();
        refresh();
    }

    private void clearInputState() {
        for (int i = 0; i < keyWasDown.length; i++) keyWasDown[i] = false;
        for (int i = 0; i < mouseWasDown.length; i++) mouseWasDown[i] = false;
    }

    private void refresh() {
        if (slotLabel != null) slotLabel.setMessage(Text.literal("Slot: " + config.slot));
        if (keyABtn != null) keyABtn.setMessage(listening == 1
            ? Text.literal("Slot " + config.slot + " -> [press key or mouse]")
            : rowLabel(config.slot, config.keyA));
        if (keyBBtn != null) keyBBtn.setMessage(listening == 2
            ? Text.literal("Slot " + config.slot + " -> [press key or mouse]")
            : rowLabel(config.slot, config.keyB));
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);
        pollListeningInput();
        int cx = width / 2;
        int startY = height / 2 - 40;
        ctx.drawCenteredTextWithShadow(textRenderer, "Top row = keybind A. Bottom row = keybind B.", cx, startY - 22, 0xAAAAAA);
        ctx.drawCenteredTextWithShadow(textRenderer, "Pause menu button toggles between both.", cx, startY - 12, 0xAAAAAA);
        super.render(ctx, mouseX, mouseY, delta);
    }

    private void pollListeningInput() {
        if (listening == 0 || client == null || client.getWindow() == null) {
            return;
        }

        long handle = client.getWindow().getHandle();

        for (int keyCode = 32; keyCode <= GLFW.GLFW_KEY_LAST; keyCode++) {
            boolean down = GLFW.glfwGetKey(handle, keyCode) == GLFW.GLFW_PRESS;
            if (down && !keyWasDown[keyCode]) {
                applyListened(InputUtil.Type.KEYSYM.createFromCode(keyCode).getTranslationKey());
                clearInputState();
                return;
            }
            keyWasDown[keyCode] = down;
        }

        boolean anyMouseDown = false;
        for (int button = 0; button < MAX_MOUSE_BUTTONS; button++) {
            boolean down = GLFW.glfwGetMouseButton(handle, button) == GLFW.GLFW_PRESS;
            anyMouseDown |= down;

            if (!ignoreMouseUntilRelease && down && !mouseWasDown[button]) {
                applyListened(InputUtil.Type.MOUSE.createFromCode(button).getTranslationKey());
                clearInputState();
                return;
            }
            mouseWasDown[button] = down;
        }

        if (ignoreMouseUntilRelease && !anyMouseDown) {
            ignoreMouseUntilRelease = false;
        }
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (handleListeningKeyboard(input.key(), input.scancode())) {
            return true;
        }
        return super.keyPressed(input);
    }

    private boolean handleListeningKeyboard(int keyCode, int scanCode) {
        if (listening != 0) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                listening = 0;
                refresh();
                return true;
            }

            InputUtil.Key captured = InputUtil.Type.KEYSYM.createFromCode(keyCode);
            if (captured == null || "key.keyboard.unknown".equals(captured.getTranslationKey())) {
                captured = InputUtil.Type.SCANCODE.createFromCode(scanCode);
            }
            applyListened(captured.getTranslationKey());
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(Click click, boolean focused) {
        if (handleListeningMouse(click.x(), click.y(), click.button())) {
            return true;
        }
        return super.mouseClicked(click, focused);
    }

    private boolean handleListeningMouse(double mouseX, double mouseY, int button) {
        // Allow right-click on key rows to enter listening mode too.
        if (listening == 0) {
            if (keyABtn != null && keyABtn.isMouseOver(mouseX, mouseY)) {
                startListening(1);
                return true;
            }
            if (keyBBtn != null && keyBBtn.isMouseOver(mouseX, mouseY)) {
                startListening(2);
                return true;
            }
        }

        if (listening != 0) {
            applyListened(InputUtil.Type.MOUSE.createFromCode(button).getTranslationKey());
            return true;
        }
        return false;
    }

    private void applyListened(String tk) {
        if (listening == 1) config.keyA = tk;
        else config.keyB = tk;
        listening = 0;
        HotbarSlotPresetsMod.resetState();
        HotbarSlotPresetsConfig.save(config);
        refresh();
    }

    @Override
    public void close() {
        HotbarSlotPresetsConfig.save(config);
        if (client != null) client.setScreen(parent);
    }
}
