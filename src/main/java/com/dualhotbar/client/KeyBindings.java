package com.dualhotbar.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

/**
 * Key bindings for Dual Hotbar. They are rebindable in Options -> Controls -> Key Binds
 * under the "Dual Hotbar" category.
 */
public final class KeyBindings {
    public static final String CATEGORY = "key.categories.dualhotbar";

    /** Toggles the visibility of both side bars. Default: mouse middle button. */
    public static final KeyMapping TOGGLE_SIDE_BARS = new KeyMapping(
            "key.dualhotbar.toggle",
            InputConstants.Type.MOUSE,
            InputConstants.MOUSE_BUTTON_MIDDLE,
            CATEGORY);

    private KeyBindings() {
    }

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_SIDE_BARS);
    }
}
