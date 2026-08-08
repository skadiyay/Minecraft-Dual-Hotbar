package com.dualhotbar.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Configuration for Dual Hotbar.
 *
 * <p>This is a COMMON config: the extended hotbar layout (slot counts) must be
 * identical on the client and on the server, otherwise inventory synchronisation
 * breaks. Slot counts require a game restart; visibility toggles apply live.</p>
 */
public final class DualHotbarConfig {
    public static final ModConfigSpec SPEC;

    /** Number of slots in the bottom hotbar (1-18). Slots 10-18 are extra slots. */
    public static final ModConfigSpec.IntValue HOTBAR_SLOTS;

    /** Number of slots in the left vertical bar (1-18). Independent storage. */
    public static final ModConfigSpec.IntValue LEFT_SLOTS;

    /** Number of slots in the right vertical bar (1-18). Independent storage. */
    public static final ModConfigSpec.IntValue RIGHT_SLOTS;

    public static final ModConfigSpec.BooleanValue LEFT_ENABLED;
    public static final ModConfigSpec.BooleanValue RIGHT_ENABLED;
    public static final ModConfigSpec.BooleanValue SHOW_BARS_BY_DEFAULT;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Bottom hotbar settings")
                .push("hotbar");
        HOTBAR_SLOTS = builder
                .comment("Number of slots in the bottom hotbar (1-18).",
                        "Slots 1-9 are the vanilla hotbar; slots 10-18 are extra hotbar slots.",
                        "Default: 9 (vanilla). Changing this requires a game restart.")
                .gameRestart()
                .defineInRange("hotbar_slots", 9, 1, 18);
        builder.pop();

        builder.comment("Side hotbars settings")
                .push("side_bars");
        LEFT_SLOTS = builder
                .comment("Number of slots in the left vertical bar (1-18).",
                        "These are independent hotbar slots (not a mirror of the inventory);",
                        "you can fill them from the inventory screen (press E).",
                        "Default: 9. Changing this requires a game restart.")
                .gameRestart()
                .defineInRange("left_slots", 9, 1, 18);
        RIGHT_SLOTS = builder
                .comment("Number of slots in the right vertical bar (1-18).",
                        "Independent hotbar slots; fill them from the inventory screen.",
                        "Default: 9. Changing this requires a game restart.")
                .gameRestart()
                .defineInRange("right_slots", 9, 1, 18);
        LEFT_ENABLED = builder
                .comment("Whether the left vertical bar is rendered.", "Default: true.")
                .define("left_enabled", true);
        RIGHT_ENABLED = builder
                .comment("Whether the right vertical bar is rendered.", "Default: true.")
                .define("right_enabled", true);
        SHOW_BARS_BY_DEFAULT = builder
                .comment("Whether the side bars start visible.",
                        "You can always toggle them with the key binding (default: mouse middle button).",
                        "Default: true.")
                .define("show_by_default", true);
        builder.pop();

        SPEC = builder.build();
    }

    private DualHotbarConfig() {
    }
}
