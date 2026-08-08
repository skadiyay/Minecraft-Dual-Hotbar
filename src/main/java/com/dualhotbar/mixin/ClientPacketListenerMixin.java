package com.dualhotbar.mixin;

import com.dualhotbar.util.MixinReflect;
import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Vanilla only applies server-sent carried-item updates to hotbar slots 0-8.
 * Allow the extended hotbar slots so "select-and-use" works on them.
 */
@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
    /**
     * {@code minecraft} is declared in {@code ClientCommonPacketListenerImpl}
     * (a superclass of the mixin target), which {@code @Shadow} cannot resolve
     * - see MixinReflect.
     */
    private static final Field MINECRAFT = MixinReflect.field(ClientCommonPacketListenerImpl.class, "minecraft");

    @Inject(method = "handleSetCarriedItem", at = @At("HEAD"), cancellable = true)
    private void dualhotbar$allowExtendedSlots(ClientboundSetCarriedItemPacket packet, CallbackInfo ci) {
        Player player;
        try {
            player = ((Minecraft) MINECRAFT.get(this)).player;
        } catch (IllegalAccessException e) {
            throw new RuntimeException("dualhotbar: failed to read ClientPacketListener.minecraft", e);
        }
        int slot = packet.getSlot();
        if (player != null && slot >= 0 && slot < player.getInventory().getContainerSize()) {
            player.getInventory().selected = slot;
        }
        ci.cancel();
    }
}
