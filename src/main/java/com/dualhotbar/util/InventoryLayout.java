package com.dualhotbar.util;

import com.dualhotbar.config.DualHotbarConfig;

/**
 * Shared layout math for the extended hotbar slots.
 *
 * <p>Player inventory layout (slot indices):
 * <pre>
 * 0-8    vanilla hotbar
 * 9-35   main inventory
 * 36+    left bar  (left_slots)
 * ...    right bar (right_slots)
 * ...    bottom extra hotbar slots (hotbar_slots - 9)
 * </pre>
 * The {@code Inventory} list is extended at construction; the layout values must
 * match between client and server (COMMON config).</p>
 */
public final class InventoryLayout {
    public static final int VANILLA_HOTBAR_SIZE = 9;
    public static final int LEFT_OFFSET = 36;

    private InventoryLayout() {
    }

    public static int bottomSlots() {
        return Math.max(1, Math.min(18, DualHotbarConfig.HOTBAR_SLOTS.get()));
    }

    public static int leftSlots() {
        return Math.max(0, Math.min(18, DualHotbarConfig.LEFT_SLOTS.get()));
    }

    public static int rightSlots() {
        return Math.max(0, Math.min(18, DualHotbarConfig.RIGHT_SLOTS.get()));
    }

    /** Extra bottom-hotbar slots (beyond the 9 vanilla ones). */
    public static int bottomExtraSlots() {
        return Math.max(0, bottomSlots() - VANILLA_HOTBAR_SIZE);
    }

    public static int rightOffset() {
        return LEFT_OFFSET + leftSlots();
    }

    public static int bottomExtOffset() {
        return rightOffset() + rightSlots();
    }

    /** Total extra inventory slots added by this mod. */
    public static int totalExtraSlots() {
        return leftSlots() + rightSlots() + bottomExtraSlots();
    }

    public static boolean isLeftSlot(int inventorySlot) {
        return inventorySlot >= LEFT_OFFSET && inventorySlot < LEFT_OFFSET + leftSlots();
    }

    public static boolean isRightSlot(int inventorySlot) {
        return inventorySlot >= rightOffset() && inventorySlot < rightOffset() + rightSlots();
    }

    public static boolean isBottomExtraSlot(int inventorySlot) {
        return inventorySlot >= bottomExtOffset() && inventorySlot < bottomExtOffset() + bottomExtraSlots();
    }

    /** Returns the bar index within the left bar for an inventory slot, or -1. */
    public static int leftBarIndex(int inventorySlot) {
        return isLeftSlot(inventorySlot) ? inventorySlot - LEFT_OFFSET : -1;
    }

    /** Returns the bar index within the right bar for an inventory slot, or -1. */
    public static int rightBarIndex(int inventorySlot) {
        return isRightSlot(inventorySlot) ? inventorySlot - rightOffset() : -1;
    }

    /** Geometry of one extra slot shown in the inventory screen. */
    public record MenuSlotInfo(int inventorySlot, int x, int y) {
    }

    /** Number of extra rows the inventory screen must grow by. */
    public static int extraMenuRows() {
        int rows = bottomExtraSlots() > 0 ? 1 : 0;
        rows += (leftSlots() + 8) / 9;
        rows += (rightSlots() + 8) / 9;
        return rows;
    }

    /** All extra slots (inventory index + menu x/y) added to the inventory menu. */
    public static java.util.List<MenuSlotInfo> menuExtraSlots() {
        java.util.List<MenuSlotInfo> list = new java.util.ArrayList<>();
        int bottomExt = bottomExtraSlots();
        for (int i = 0; i < bottomExt; i++) {
            list.add(new MenuSlotInfo(bottomExtOffset() + i, 8 + (i % 9) * 18, 160));
        }
        int left = leftSlots();
        for (int i = 0; i < left; i++) {
            list.add(new MenuSlotInfo(LEFT_OFFSET + i, 8 + (i % 9) * 18, 178 + (i / 9) * 18));
        }
        int rightRowStart = (left + 8) / 9;
        for (int i = 0; i < rightSlots(); i++) {
            list.add(new MenuSlotInfo(rightOffset() + i, 8 + (i % 9) * 18, 178 + (rightRowStart + i / 9) * 18));
        }
        return list;
    }
}
