package com.dualhotbar.mixin;

import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Vanilla rejects carried-item slot changes outside 0-8. Accept any inventory
 * slot (including the extended hotbar slots) so "select-and-use" works.
 */
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    @Shadow
    @Final
    private ServerPlayer player;

    @Shadow
    @Final
    private static Logger LOGGER;

    @Inject(method = "handleSetCarriedItem", at = @At("HEAD"), cancellable = true)
    private void dualhotbar$allowExtendedSlots(ServerboundSetCarriedItemPacket packet, CallbackInfo ci) {
        int slot = packet.getSlot();
        if (slot >= 0 && slot < this.player.getInventory().getContainerSize()) {
            if (this.player.getInventory().selected != slot && this.player.getUsedItemHand() == net.minecraft.world.InteractionHand.MAIN_HAND) {
                this.player.stopUsingItem();
            }
            this.player.getInventory().selected = slot;
            this.player.resetLastActionTime();
        } else {
            LOGGER.warn("{} tried to set an invalid carried item", this.player.getName().getString());
        }
        ci.cancel();
    }
}
