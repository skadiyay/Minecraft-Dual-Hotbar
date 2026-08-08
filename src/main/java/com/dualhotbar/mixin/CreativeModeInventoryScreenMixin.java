package com.dualhotbar.mixin;

import com.dualhotbar.util.MixinReflect;
import java.lang.reflect.Field;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The creative inventory screen wraps every slot of the survival inventory menu,
 * which would put the extra hotbar slots on top of the hotbar row. Remove them
 * after {@code selectTab} rebuilds the slot list (the extended slots are filled
 * from the survival inventory screen instead; creative mode can still use them
 * via the hotbar keys/scroll).
 */
@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin {
    /** First menu slot index owned by Dual Hotbar (crafting 0-4, armor 5-8, main 9-35, hotbar 36-44, offhand 45). */
    private static final int FIRST_EXTRA_MENU_SLOT = 46;

    /**
     * {@code menu} is declared in {@code AbstractContainerScreen} (a superclass of
     * the mixin target), which {@code @Shadow} cannot resolve - see MixinReflect.
     * The {@code slots} list itself is a public field of {@code AbstractContainerMenu}.
     */
    private static final Field MENU = MixinReflect.field(AbstractContainerScreen.class, "menu");

    @Inject(method = "selectTab", at = @At("TAIL"))
    private void dualhotbar$removeExtraSlots(CreativeModeTab tab, CallbackInfo ci) {
        try {
            AbstractContainerMenu menu = (AbstractContainerMenu) MENU.get(this);
            menu.slots.removeIf(slot -> slot.index >= FIRST_EXTRA_MENU_SLOT);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("dualhotbar: failed to read creative screen menu", e);
        }
    }
}
