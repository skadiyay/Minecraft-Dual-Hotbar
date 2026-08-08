package com.dualhotbar.mixin;

import com.dualhotbar.util.InventoryLayout;
import com.dualhotbar.util.MixinReflect;
import java.lang.reflect.Method;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adds the extra hotbar slots to the vanilla player inventory menu so they can be
 * filled by dragging items from the inventory (press E), just like the vanilla hotbar.
 */
@Mixin(InventoryMenu.class)
public abstract class InventoryMenuMixin {
    /**
     * {@code addSlot} is declared in {@code AbstractContainerMenu} (a superclass of
     * the mixin target), which {@code @Shadow} cannot resolve - see MixinReflect.
     */
    private static final Method ADD_SLOT = MixinReflect.method(AbstractContainerMenu.class, "addSlot", Slot.class);

    @Inject(method = "<init>", at = @At("TAIL"))
    private void dualhotbar$addExtraSlots(Inventory inventory, boolean active, Player player, CallbackInfo ci) {
        for (InventoryLayout.MenuSlotInfo info : InventoryLayout.menuExtraSlots()) {
            try {
                ADD_SLOT.invoke(this, new Slot(inventory, info.inventorySlot(), info.x(), info.y()));
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("dualhotbar: failed to add extended slot", e);
            }
        }
    }
}
