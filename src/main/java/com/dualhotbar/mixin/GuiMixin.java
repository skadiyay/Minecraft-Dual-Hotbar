package com.dualhotbar.mixin;

import com.dualhotbar.util.InventoryLayout;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Renders the bottom hotbar with a configurable number of slots (1-18), centered,
 * using the vanilla semi-transparent hotbar sprite tiled per slot (supports any
 * slot count while keeping the vanilla look).
 */
@Mixin(Gui.class)
public abstract class GuiMixin {
    private static final ResourceLocation HOTBAR_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar");
    private static final ResourceLocation HOTBAR_SELECTION_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_selection");
    private static final ResourceLocation HOTBAR_OFFHAND_LEFT_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_offhand_left");
    private static final ResourceLocation HOTBAR_OFFHAND_RIGHT_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_offhand_right");
    private static final ResourceLocation ATTACK_INDICATOR_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_attack_indicator_background");
    private static final ResourceLocation ATTACK_INDICATOR_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_attack_indicator_progress");

    /** Vanilla hotbar sprite: 182x22, 9 slots of 20 px pitch. */
    private static final int HOTBAR_SPRITE_W = 182;
    private static final int HOTBAR_SPRITE_H = 22;
    private static final int SLOT_PITCH = 20;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "renderItemHotbar", at = @At("HEAD"), cancellable = true)
    private void dualhotbar$renderItemHotbar(GuiGraphics gui, DeltaTracker delta, CallbackInfo ci) {
        Player player = this.minecraft.player;
        if (player == null) {
            return;
        }
        dualhotbar$renderHotbar(gui, delta, player);
        ci.cancel();
    }

    private void dualhotbar$renderHotbar(GuiGraphics gui, DeltaTracker delta, Player player) {
        int slots = InventoryLayout.bottomSlots();
        ItemStack offhand = player.getOffhandItem();
        HumanoidArm arm = player.getMainArm().getOpposite();
        int cx = gui.guiWidth() / 2;
        int bottom = gui.guiHeight() - 22;
        int totalWidth = slots * SLOT_PITCH + 2;
        int x0 = cx - totalWidth / 2;
        int selected = player.getInventory().selected;

        RenderSystem.enableBlend();
        gui.pose().pushPose();
        gui.pose().translate(0.0F, 0.0F, -90.0F);

        // Semi-transparent vanilla background, tiled per slot.
        for (int i = 0; i < slots; i++) {
            int u = dualhotbar$slotCropU(i, slots);
            // The last slot includes the sprite's right edge (2 px wider).
            int w = (i == slots - 1) ? SLOT_PITCH + 2 : SLOT_PITCH;
            gui.blitSprite(HOTBAR_SPRITE, HOTBAR_SPRITE_W, HOTBAR_SPRITE_H, u, 0,
                    x0 + i * SLOT_PITCH, bottom, w, HOTBAR_SPRITE_H);
        }

        // Selection frame.
        if (selected >= 0 && selected < slots) {
            gui.blitSprite(HOTBAR_SELECTION_SPRITE, x0 - 1 + selected * SLOT_PITCH, bottom - 1, 24, 23);
        }

        // Offhand slot frame.
        if (!offhand.isEmpty()) {
            if (arm == HumanoidArm.LEFT) {
                gui.blitSprite(HOTBAR_OFFHAND_LEFT_SPRITE, x0 - 3 - 29, gui.guiHeight() - 23, 29, 24);
            } else {
                gui.blitSprite(HOTBAR_OFFHAND_RIGHT_SPRITE, x0 + totalWidth + 2, gui.guiHeight() - 23, 29, 24);
            }
        }

        gui.pose().popPose();
        RenderSystem.disableBlend();

        // Items.
        Inventory inv = player.getInventory();
        int seed = 1;
        for (int i = 0; i < slots; i++) {
            if (i >= inv.items.size()) {
                break;
            }
            int slotX = x0 + 3 + i * SLOT_PITCH;
            int slotY = gui.guiHeight() - 16 - 3;
            dualhotbar$renderSlot(gui, slotX, slotY, delta, player, inv.items.get(i), seed++);
        }

        if (!offhand.isEmpty()) {
            int offY = gui.guiHeight() - 16 - 3;
            if (arm == HumanoidArm.LEFT) {
                dualhotbar$renderSlot(gui, x0 - 26, offY, delta, player, offhand, seed++);
            } else {
                dualhotbar$renderSlot(gui, x0 + totalWidth + 10, offY, delta, player, offhand, seed++);
            }
        }

        // Attack indicator.
        if (this.minecraft.options.attackIndicator().get() == net.minecraft.client.AttackIndicatorStatus.HOTBAR) {
            RenderSystem.enableBlend();
            float strength = player.getAttackStrengthScale(0.0F);
            if (strength < 1.0F) {
                int y = gui.guiHeight() - 20;
                int x = arm == HumanoidArm.RIGHT ? x0 - 22 : x0 + totalWidth + 6;
                int height = (int) (strength * 19.0F);
                gui.blitSprite(ATTACK_INDICATOR_BACKGROUND_SPRITE, x, y, 18, 18);
                gui.blitSprite(ATTACK_INDICATOR_PROGRESS_SPRITE, 18, 18, 0, 18 - height, x, y + 18 - height, 18, height);
            }
            RenderSystem.disableBlend();
        }
    }

    /**
     * Horizontal crop offset inside the 9-slot hotbar sprite: first slot uses the
     * rounded left end, interior slots a straight-edged tile, last slot the right end.
     */
    private static int dualhotbar$slotCropU(int index, int slots) {
        if (index == 0) {
            return 0;
        }
        if (index == slots - 1) {
            return 160; // 8 * 20 - the right end slot
        }
        return 20; // straight-edged interior tile
    }

    /** Copy of vanilla {@code Gui.renderSlot}. */
    private void dualhotbar$renderSlot(GuiGraphics gui, int x, int y, DeltaTracker delta, Player player,
                                       ItemStack stack, int seed) {
        if (!stack.isEmpty()) {
            float pop = stack.getPopTime() - delta.getGameTimeDeltaPartialTick(false);
            if (pop > 0.0F) {
                float scale = 1.0F + pop / 5.0F;
                gui.pose().pushPose();
                gui.pose().translate(x + 8, y + 12, 0.0F);
                gui.pose().scale(1.0F / scale, (scale + 1.0F) / 2.0F, 1.0F);
                gui.pose().translate(-(x + 8), -(y + 12), 0.0F);
            }
            gui.renderItem(player, stack, x, y, seed);
            if (pop > 0.0F) {
                gui.pose().popPose();
            }
            gui.renderItemDecorations(this.minecraft.font, stack, x, y);
        }
    }
}
