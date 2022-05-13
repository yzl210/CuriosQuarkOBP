package cn.leomc.curiosquarkobp.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import top.theillusivec4.curios.common.inventory.container.CuriosContainerProvider;
import top.theillusivec4.curios.common.network.NetworkHandler;
import top.theillusivec4.curios.common.network.client.CPacketOpenCurios;
import top.theillusivec4.curios.common.network.server.SPacketGrabbedItem;

import java.util.function.Supplier;

@Mixin(CPacketOpenCurios.class)
public class CPacketOpenCuriosMixin {

    /**
     * @author yzl210
     */
    @Overwrite(remap = false)
    public static void handle(CPacketOpenCurios msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();

            if (sender != null) {
                ItemStack stack = sender.inventoryMenu.getCarried();
                sender.inventoryMenu.setCarried(ItemStack.EMPTY);
                NetworkHooks.openGui(sender, new CuriosContainerProvider());

                if (!stack.isEmpty()) {
                    sender.containerMenu.setCarried(stack);
                    NetworkHandler.INSTANCE
                            .send(PacketDistributor.PLAYER.with(() -> sender), new SPacketGrabbedItem(stack));
                }
            }

        });
        ctx.get().setPacketHandled(true);
    }

}
