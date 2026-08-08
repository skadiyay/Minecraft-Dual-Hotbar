package com.dualhotbar.client;

import com.dualhotbar.config.DualHotbarConfig;
import com.dualhotbar.util.InventoryLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

/**
 * Client-side state for the side hotbars.
 *
 * <p>Selecting a side-bar cell simply points the player's {@code selected} slot at
 * that cell (no item swapping), exactly like selecting a vanilla hotbar slot - the
 * item is then held and usable. Changes are synced to the server immediately.</p>
 */
public final class SideBarState {
    private static boolean visible = true;
    private static int leftIndex = 0;
    private static int rightIndex = 0;

    private SideBarState() {
    }

    public static void init() {
        visible = DualHotbarConfig.SHOW_BARS_BY_DEFAULT.get();
    }

    public static boolean isVisible() {
        return visible;
    }

    public static void toggleVisible() {
        visible = !visible;
    }

    public static boolean isLeftBarActive() {
        return visible && DualHotbarConfig.LEFT_ENABLED.get() && InventoryLayout.leftSlots() > 0;
    }

    public static boolean isRightBarActive() {
        return visible && DualHotbarConfig.RIGHT_ENABLED.get() && InventoryLayout.rightSlots() > 0;
    }

    public static int leftIndex() {
        return leftIndex;
    }

    public static int rightIndex() {
        return rightIndex;
    }

    /** Select a left-bar cell by its bar index (Shift + number key). */
    public static void selectLeft(int barIndex) {
        int slots = InventoryLayout.leftSlots();
        if (slots <= 0) {
            return;
        }
        leftIndex = Math.floorMod(barIndex, slots);
        setSelected(InventoryLayout.LEFT_OFFSET + leftIndex);
    }

    /** Select a right-bar cell by its bar index (Ctrl + number key). */
    public static void selectRight(int barIndex) {
        int slots = InventoryLayout.rightSlots();
        if (slots <= 0) {
            return;
        }
        rightIndex = Math.floorMod(barIndex, slots);
        setSelected(InventoryLayout.rightOffset() + rightIndex);
    }

    /** Scroll the left bar selection (Shift + scroll). */
    public static void scrollLeft(double deltaY) {
        int steps = (int) Math.round(deltaY);
        if (steps == 0) {
            return;
        }
        int slots = InventoryLayout.leftSlots();
        if (slots <= 0) {
            return;
        }
        leftIndex = Math.floorMod(leftIndex + steps, slots);
        setSelected(InventoryLayout.LEFT_OFFSET + leftIndex);
    }

    /** Scroll the right bar selection (Ctrl + scroll). */
    public static void scrollRight(double deltaY) {
        int steps = (int) Math.round(deltaY);
        if (steps == 0) {
            return;
        }
        int slots = InventoryLayout.rightSlots();
        if (slots <= 0) {
            return;
        }
        rightIndex = Math.floorMod(rightIndex + steps, slots);
        setSelected(InventoryLayout.rightOffset() + rightIndex);
    }

    /** Select an extra bottom-hotbar slot (bar index 9+), used by the 0 key. */
    public static void selectBottomExtra(int barIndex) {
        setSelected(InventoryLayout.bottomExtOffset() + barIndex);
    }

    private static void setSelected(int inventorySlot) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || player.isSpectator()) {
            return;
        }
        Inventory inv = player.getInventory();
        if (inventorySlot < 0 || inventorySlot >= inv.getContainerSize()) {
            return;
        }
        inv.selected = inventorySlot;
        if (mc.player.connection != null) {
            mc.player.connection.send(new ServerboundSetCarriedItemPacket(inventorySlot));
        }
    }
}
