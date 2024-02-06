package cn.leomc.curiosquarkobp.mixin;

import cn.leomc.curiosquarkobp.CQOBCarriedAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.violetmoon.quark.addons.oddities.inventory.BackpackMenu;
import top.theillusivec4.curios.common.network.NetworkHandler;
import top.theillusivec4.curios.common.network.client.CPacketOpenVanilla;
import top.theillusivec4.curios.common.network.server.SPacketGrabbedItem;

import java.util.function.Supplier;

@Mixin(CPacketOpenVanilla.class)
public class CPacketOpenVanillaMixin implements CQOBCarriedAccessor {

    @Shadow(remap = false)
    @Final
    private ItemStack carried;

    public ItemStack getCarried() {
        return carried;
    }

    @Inject(
            at = @At("HEAD"),
            method = "handle",
            remap = false,
            cancellable = true
    )
    private static void handle(CPacketOpenVanilla msg, Supplier<NetworkEvent.Context> ctx, CallbackInfo ci) {
        ci.cancel();
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender != null) {
                ItemStack stack = sender.isCreative() ? ((CQOBCarriedAccessor) msg).getCarried() : sender.containerMenu.getCarried();
                sender.containerMenu.setCarried(ItemStack.EMPTY);
                if (!(sender.containerMenu instanceof BackpackMenu))
                    sender.doCloseContainer();

                if (!stack.isEmpty()) {
                    sender.containerMenu.setCarried(stack);
                    NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> sender), new SPacketGrabbedItem(stack));
                }
            }

        });
        ctx.get().setPacketHandled(true);
    }

}
