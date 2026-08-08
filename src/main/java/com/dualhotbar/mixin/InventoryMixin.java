package com.dualhotbar.mixin;

import com.dualhotbar.util.InventoryLayout;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Extends the player inventory with the extra hotbar slots (left/right bars and
 * the extra bottom hotbar slots) and makes {@code selected} able to point at any
 * of them ("select-and-use", like the vanilla hotbar).
 */
@Mixin(Inventory.class)
public abstract class InventoryMixin {
    @Shadow
    @Final
    public NonNullList<ItemStack> items;

    @Shadow
    public int selected;

    /** {@code NonNullList(List, Object)} is protected; needed to build an ArrayList-backed instance. */
    private static final Constructor<NonNullList> NLIST_CTOR = findNListCtor();

    /**
     * Vanilla {@code NonNullList.withSize()} is backed by {@code Arrays.asList},
     * a fixed-size list whose {@code add()} throws {@code UnsupportedOperationException}
     * (this crashed player creation: "Invalid player data").
     * <p>
     * Replace every {@code withSize} call inside {@code Inventory.<init>} (items/armor/offhand)
     * with an ArrayList-backed list. {@code compartments} keeps referencing the same
     * instances, so it stays in sync automatically.
     */
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/NonNullList;withSize(ILjava/lang/Object;)Lnet/minecraft/core/NonNullList;"))
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static NonNullList dualhotbar$mutableWithSize(int size, Object value) {
        List<Object> backing = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            backing.add(value);
        }
        try {
            return NLIST_CTOR.newInstance(backing, value);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("dualhotbar: failed to create mutable NonNullList", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Constructor<NonNullList> findNListCtor() {
        try {
            Constructor<NonNullList> c = NonNullList.class.getDeclaredConstructor(List.class, Object.class);
            c.setAccessible(true);
            return c;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("dualhotbar: cannot find NonNullList(List, Object) constructor", e);
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void dualhotbar$extendItems(Player player, CallbackInfo ci) {
        int extra = InventoryLayout.totalExtraSlots();
        if (extra > 0) {
            // items is now ArrayList-backed (see dualhotbar$mutableWithSize), so append works.
            this.items.addAll(Collections.nCopies(extra, ItemStack.EMPTY));
        }
    }

    /**
     * The vanilla implementation only returns the item for hotbar slots 0-8.
     * Allow the selected slot to be any extended hotbar slot so the item is
     * actually "held and used" (main hand, attacks, placing...).
     */
    @Inject(method = "getSelected", at = @At("HEAD"), cancellable = true)
    private void dualhotbar$getSelected(CallbackInfoReturnable<ItemStack> cir) {
        if (this.selected >= 0 && this.selected < this.items.size()) {
            cir.setReturnValue(this.items.get(this.selected));
        } else {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }

    /**
     * Vanilla scroll only cycles the 9 hotbar slots. Cycle through the whole
     * (configurable) bottom bar instead.
     */
    @Inject(method = "swapPaint", at = @At("HEAD"), cancellable = true)
    private void dualhotbar$swapPaint(double scrollDelta, CallbackInfo ci) {
        int slots = InventoryLayout.bottomSlots();
        int sign = (int) Math.signum(scrollDelta);
        this.selected = Math.floorMod(this.selected - sign, slots);
        ci.cancel();
    }
}
