package com.dualhotbar.client;

import com.dualhotbar.util.InventoryLayout;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Renders the two vertical side hotbars. Each cell uses the vanilla semi-transparent
 * hotbar sprite rotated 90 degrees, so the bars keep the vanilla translucent look.
 */
public final class SideHotbarRenderer {
    private static final ResourceLocation HOTBAR_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar");
    private static final ResourceLocation HOTBAR_SELECTION_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_selection");

    /** Sprite dimensions (182x22, 9 slots, 20 px pitch). */
    private static final int HOTBAR_SPRITE_W = 182;
    private static final int HOTBAR_SPRITE_H = 22;
    private static final int PITCH = 20;
    /** Rotated cell size: 22 wide x 20 tall. */
    private static final int CELL_W = 22;
    private static final int CELL_H = 20;
    private static final int ITEM_X = 3;
    private static final int ITEM_Y = 2;

    private SideHotbarRenderer() {
    }

    public static void render(GuiGraphics gui, DeltaTracker delta, Minecraft mc) {
        Player player = mc.player;
        if (player == null || player.isSpectator() || mc.screen != null || !SideBarState.isVisible()) {
            return;
        }
        Inventory inv = player.getInventory();
        int width = gui.guiWidth();
        int height = gui.guiHeight();
        int selected = inv.selected;

        if (SideBarState.isLeftBarActive()) {
            int barX = 2;
            renderBar(gui, delta, mc, player, inv, barX, height,
                    InventoryLayout.leftSlots(), InventoryLayout.LEFT_OFFSET, selected);
        }
        if (SideBarState.isRightBarActive()) {
            int barX = width - CELL_W - 2;
            renderBar(gui, delta, mc, player, inv, barX, height,
                    InventoryLayout.rightSlots(), InventoryLayout.rightOffset(), selected);
        }
    }

    private static void renderBar(GuiGraphics gui, DeltaTracker delta, Minecraft mc, Player player,
                                  Inventory inv, int barX, int screenHeight, int slots, int slotOffset, int selected) {
        // Last cell includes the sprite's right edge, so the bar is 2 px taller.
        int barH = slots * PITCH + 2;
        int top = (screenHeight - barH) / 2;

        RenderSystem.enableBlend();
        for (int i = 0; i < slots; i++) {
            int cellY = top + i * PITCH;
            int invSlot = slotOffset + i;
            boolean isSelected = invSlot == selected;

            // Semi-transparent rotated hotbar cell.
            gui.pose().pushPose();
            gui.pose().translate(barX + CELL_W / 2.0F, cellY + CELL_H / 2.0F, 0.0F);
            gui.pose().mulPose(Axis.ZP.rotationDegrees(90.0F));
            int u = slotCropU(i, slots);
            int w = (i == slots - 1) ? PITCH + 2 : PITCH;
            gui.blitSprite(HOTBAR_SPRITE, HOTBAR_SPRITE_W, HOTBAR_SPRITE_H, u, 0,
                    -w / 2, -HOTBAR_SPRITE_H / 2, w, HOTBAR_SPRITE_H);
            if (isSelected) {
                gui.blitSprite(HOTBAR_SELECTION_SPRITE, -12, -12, 24, 23);
            }
            gui.pose().popPose();

            ItemStack stack = invSlot < inv.getContainerSize() ? inv.getItem(invSlot) : ItemStack.EMPTY;
            if (!stack.isEmpty()) {
                int cellX = barX;
                if (mc.mouseHandler.xpos() >= cellX && mc.mouseHandler.xpos() < cellX + CELL_W
                        && mc.mouseHandler.ypos() >= cellY && mc.mouseHandler.ypos() < cellY + CELL_H) {
                    gui.renderTooltip(mc.font, stack, (int) mc.mouseHandler.xpos(), (int) mc.mouseHandler.ypos());
                }
                gui.renderItem(player, stack, cellX + ITEM_X, cellY + ITEM_Y, invSlot);
                gui.renderItemDecorations(mc.font, stack, cellX + ITEM_X, cellY + ITEM_Y);
            }
        }
        RenderSystem.disableBlend();
    }

    /** Vertical crop offset: top cell = rounded end, interior = straight tile, bottom cell = rounded end. */
    private static int slotCropU(int index, int slots) {
        if (index == 0) {
            return 0;
        }
        if (index == slots - 1) {
            return 160;
        }
        return 20;
    }
}
