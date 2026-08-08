package com.dualhotbar.client;

import com.dualhotbar.util.InventoryLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import org.lwjgl.glfw.GLFW;

/**
 * Client-side event handlers: key toggle, 0-key / scroll switching, HUD rendering.
 */
public final class ClientEvents {
    private static boolean initialized = false;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (!initialized) {
            // Config is fully loaded by the first client tick.
            SideBarState.init();
            initialized = true;
        }
        while (KeyBindings.TOGGLE_SIDE_BARS.consumeClick()) {
            SideBarState.toggleVisible();
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                mc.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.5F, 1.0F);
            }
        }
    }

    /**
     * Handles the 0 key (extra bottom hotbar slot, bar index 9) and
     * Shift/Ctrl + 0 (left/right bar slot 10).
     */
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (event.getAction() != GLFW.GLFW_PRESS || event.getKey() != GLFW.GLFW_KEY_0) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null || mc.player.isSpectator()) {
            return;
        }
        boolean shift = Screen.hasShiftDown();
        boolean ctrl = Screen.hasControlDown();
        if (shift && SideBarState.isLeftBarActive() && InventoryLayout.leftSlots() > 9) {
            SideBarState.selectLeft(9);
        } else if (ctrl && SideBarState.isRightBarActive() && InventoryLayout.rightSlots() > 9) {
            SideBarState.selectRight(9);
        } else if (InventoryLayout.bottomSlots() > 9) {
            SideBarState.selectBottomExtra(0);
        }
    }

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null || mc.player.isSpectator()) {
            return;
        }
        double deltaY = event.getScrollDeltaY();
        if (deltaY == 0) {
            return;
        }
        boolean shift = Screen.hasShiftDown();
        boolean ctrl = Screen.hasControlDown();
        if (shift && SideBarState.isLeftBarActive()) {
            SideBarState.scrollLeft(deltaY);
            event.setCanceled(true);
        } else if (ctrl && SideBarState.isRightBarActive()) {
            SideBarState.scrollRight(deltaY);
            event.setCanceled(true);
        }
        // Plain scroll: vanilla handles it (Inventory.swapPaint, mixin'd to cycle the whole bottom bar).
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) {
            return;
        }
        if (mc.options.hideGui) {
            return;
        }
        SideHotbarRenderer.render(event.getGuiGraphics(), event.getPartialTick(), mc);
    }
}
