package cn.leomc.curiosquarkobp.mixin;

import cn.leomc.curiosquarkobp.CQOBCarriedAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import top.theillusivec4.curios.common.network.NetworkHandler;
import top.theillusivec4.curios.common.network.client.CPacketOpenVanilla;
import top.theillusivec4.curios.common.network.server.SPacketGrabbedItem;
import vazkii.quark.addons.oddities.inventory.BackpackMenu;

import java.util.function.Supplier;

@Mixin(CPacketOpenVanilla.class)
public class CPacketOpenVanillaMixin implements CQOBCarriedAccessor {

    @Shadow @Final private ItemStack carried;

    public ItemStack getCarried(){
        return carried;
    }

    /**
     * @author yzl210
     */
    @Overwrite(remap = false)
    public static void handle(CPacketOpenVanilla msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender != null) {
                ItemStack stack = sender.isCreative() ? ((CQOBCarriedAccessor)msg).getCarried() : sender.containerMenu.getCarried();
                sender.containerMenu.setCarried(ItemStack.EMPTY);
                if(sender.containerMenu.getClass() != BackpackMenu.class)
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
