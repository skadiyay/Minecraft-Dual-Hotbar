package com.dualhotbar.mixin;

import com.dualhotbar.util.InventoryLayout;
import com.dualhotbar.util.MixinReflect;
import java.lang.reflect.Field;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Makes the inventory screen taller to show the extra hotbar rows, and renders a
 * vanilla-style translucent panel with slot frames behind them.
 */
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin {
    private static final ResourceLocation SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot");

    /**
     * {@code imageHeight}/{@code leftPos}/{@code topPos} are declared in
     * {@code AbstractContainerScreen} (a superclass of the mixin target), which
     * {@code @Shadow} cannot resolve - see MixinReflect.
     */
    private static final Field IMAGE_HEIGHT = MixinReflect.field(AbstractContainerScreen.class, "imageHeight");
    private static final Field LEFT_POS = MixinReflect.field(AbstractContainerScreen.class, "leftPos");
    private static final Field TOP_POS = MixinReflect.field(AbstractContainerScreen.class, "topPos");

    @Inject(method = "<init>", at = @At("TAIL"))
    private void dualhotbar$extendHeight(net.minecraft.world.entity.player.Player player, CallbackInfo ci) {
        try {
            IMAGE_HEIGHT.setInt(this, IMAGE_HEIGHT.getInt(this) + InventoryLayout.extraMenuRows() * 18);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("dualhotbar: failed to extend inventory screen height", e);
        }
    }

    @Inject(method = "renderBg", at = @At("TAIL"))
    private void dualhotbar$renderExtraRows(GuiGraphics gui, float partialTick, int mouseX, int mouseY, CallbackInfo ci) {
        var slots = InventoryLayout.menuExtraSlots();
        if (slots.isEmpty()) {
            return;
        }
        int leftPos;
        int topPos;
        try {
            leftPos = LEFT_POS.getInt(this);
            topPos = TOP_POS.getInt(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("dualhotbar: failed to read inventory screen position", e);
        }
        // Translucent panel behind the extra rows.
        int rows = InventoryLayout.extraMenuRows();
        int panelX = leftPos + 8 - 1;
        int panelY = topPos + 160 - 1;
        int panelW = 9 * 18 + 2;
        int panelH = rows * 18 + 2;
        gui.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xE60C0C0C);

        // Slot frames + items are rendered by the container screen; draw the frames.
        for (InventoryLayout.MenuSlotInfo info : slots) {
            gui.blitSprite(SLOT_SPRITE, leftPos + info.x(), topPos + info.y(), 18, 18);
        }
    }
}
