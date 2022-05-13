package cn.leomc.curiosquarkobp.mixin;


import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import vazkii.arl.network.IMessage;
import vazkii.quark.addons.oddities.inventory.BackpackMenu;
import vazkii.quark.addons.oddities.module.BackpackModule;
import vazkii.quark.base.network.message.oddities.HandleBackpackMessage;

import java.util.Optional;

@Mixin(HandleBackpackMessage.class)
public abstract class HandleBackpackMessageMixin implements IMessage {


    @Shadow
    public boolean open;

    /**
     * @author yzl210
     */
    @Overwrite(remap = false)
    public boolean receive(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        context.enqueueWork(() -> {
            if (open) {
                ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
                if (stack.getItem() != BackpackModule.backpack) {
                    Optional<SlotResult> result = CuriosApi.getCuriosHelper().findFirstCurio(player, BackpackModule.backpack);
                    if (result.isPresent())
                        stack = result.get().stack();
                }

                if (stack.getItem() instanceof MenuProvider && player.containerMenu != null) {
                    ItemStack holding = player.inventoryMenu.getCarried();
                    player.inventoryMenu.setCarried(ItemStack.EMPTY);
                    player.doCloseContainer();

                    NetworkHooks.openGui(player, (MenuProvider) stack.getItem(), player.blockPosition());
                    player.containerMenu.setCarried(holding);
                }
            } else {
                if (player.containerMenu != null) {
                    ItemStack holding = player.containerMenu.getCarried();
                    player.containerMenu.setCarried(ItemStack.EMPTY);

                    BackpackMenu.saveCraftingInventory(player);
                    player.containerMenu = player.inventoryMenu;
                    player.inventoryMenu.setCarried(holding);
                }
            }

        });
        return true;
    }
}
