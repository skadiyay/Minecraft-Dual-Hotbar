package com.dualhotbar;

import com.dualhotbar.client.KeyBindings;
import com.dualhotbar.config.DualHotbarConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Dual Hotbar - extended hotbar mod for Minecraft 1.21.1 (NeoForge).
 *
 * <p>Installed on BOTH client and server (the extended inventory slots are part of
 * the player inventory and must match on both sides).</p>
 *
 * <ul>
 *   <li>Bottom hotbar with a configurable slot count (1-18).</li>
 *   <li>Left/right vertical side hotbars with configurable slot counts (1-18),
 *       independent storage, fillable from the inventory screen (press E).</li>
 *   <li>Toggle side bars with a key (default: mouse middle button).</li>
 *   <li>Selecting a side-bar slot "selects and uses" that item (no swapping),
 *       exactly like the vanilla hotbar.</li>
 * </ul>
 */
@Mod(DualHotbarMod.MOD_ID)
public class DualHotbarMod {
    public static final String MOD_ID = "dualhotbar";

    public DualHotbarMod(IEventBus modEventBus, ModContainer container) {
        // COMMON config so client and server agree on the extended inventory layout.
        container.registerConfig(net.neoforged.fml.config.ModConfig.Type.COMMON, DualHotbarConfig.SPEC);

        if (FMLEnvironment.dist.isClient()) {
            // Config screen + key binding + HUD/input handlers are client-only.
            container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, screen) -> new ConfigurationScreen(modContainer, screen));
            modEventBus.addListener(DualHotbarMod::registerKeyMappings);
            NeoForge.EVENT_BUS.register(com.dualhotbar.client.ClientEvents.class);
        }
    }

    private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        KeyBindings.register(event);
    }
}
