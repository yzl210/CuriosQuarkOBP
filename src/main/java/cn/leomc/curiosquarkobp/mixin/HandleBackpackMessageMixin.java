package cn.leomc.curiosquarkobp.mixin;


import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.violetmoon.quark.addons.oddities.inventory.BackpackMenu;
import org.violetmoon.quark.addons.oddities.module.BackpackModule;
import org.violetmoon.quark.base.network.message.oddities.HandleBackpackMessage;
import org.violetmoon.zeta.network.IZetaNetworkEventContext;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;

@Mixin(HandleBackpackMessage.class)
public abstract class HandleBackpackMessageMixin {


    @Shadow
    public boolean open;

    /**
     * @author yzl210
     */
    @Overwrite(remap = false)
    public boolean receive(IZetaNetworkEventContext context) {
        ServerPlayer player = context.getSender();
        context.enqueueWork(() -> {
            if (open) {
                ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
                if (stack.getItem() != BackpackModule.backpack) {
                    Optional<SlotResult> result = CuriosApi.getCuriosInventory(player).map(inv -> inv.findFirstCurio(BackpackModule.backpack)).orElse(Optional.empty());
                    if (result.isPresent())
                        stack = result.get().stack();
                }

                if (stack.getItem() instanceof MenuProvider && player.containerMenu != null) {
                    ItemStack holding = player.containerMenu.getCarried().copy();
                    player.containerMenu.setCarried(ItemStack.EMPTY);
                    NetworkHooks.openScreen(player, (MenuProvider) stack.getItem(), player.blockPosition());
                    player.containerMenu.setCarried(holding);
                }
            } else if (player.containerMenu != null) {
                ItemStack holding = player.containerMenu.getCarried();
                player.containerMenu.setCarried(ItemStack.EMPTY);

                BackpackMenu.saveCraftingInventory(player);
                player.containerMenu = player.inventoryMenu;
                player.inventoryMenu.setCarried(holding);

            }

        });
        return true;
    }
}
