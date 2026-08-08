package com.dualhotbar.mixin;

import com.dualhotbar.client.SideBarState;
import com.dualhotbar.util.InventoryLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Intercepts the vanilla hotbar number-key handling so that
 * Shift + 1..9 selects the left side bar and Ctrl + 1..9 the right side bar.
 *
 * <p>The clicks are consumed before vanilla sees them, so the vanilla hotbar
 * selection is left untouched in those cases.</p>
 */
@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    @Final
    public Options options;

    @Shadow
    public LocalPlayer player;

    @Inject(method = "handleKeybinds", at = @At("HEAD"))
    private void dualhotbar$handleKeybinds(CallbackInfo ci) {
        LocalPlayer p = this.player;
        if (p == null || p.isSpectator()) {
            return;
        }
        boolean shift = Screen.hasShiftDown();
        boolean ctrl = Screen.hasControlDown();
        if (!shift && !ctrl) {
            return;
        }
        Inventory inv = p.getInventory();
        int left = InventoryLayout.leftSlots();
        int right = InventoryLayout.rightSlots();
        for (int i = 0; i < 9; i++) {
            boolean handleLeft = shift && SideBarState.isLeftBarActive() && i < left;
            boolean handleRight = ctrl && SideBarState.isRightBarActive() && i < right;
            if (!handleLeft && !handleRight) {
                continue;
            }
            if (this.options.keyHotbarSlots[i].consumeClick()) {
                if (handleLeft) {
                    SideBarState.selectLeft(i);
                } else {
                    SideBarState.selectRight(i);
                }
            }
        }
    }
}
