package cn.leomc.curiosquarkobp.mixin;


import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import top.theillusivec4.curios.api.CuriosApi;
import vazkii.arl.network.IMessage;
import vazkii.quark.addons.oddities.container.BackpackContainer;
import vazkii.quark.base.network.message.HandleBackpackMessage;

import java.util.Optional;

@Mixin(HandleBackpackMessage.class)
public class QuarkOdditiesHandleBackpackMessageMixin implements IMessage {

    private static final long serialVersionUID = 3474816381329541425L;
    public boolean open;

    public QuarkOdditiesHandleBackpackMessageMixin() {
    }

    public QuarkOdditiesHandleBackpackMessageMixin(boolean open) {
        this.open = open;
    }


    @Overwrite(
            remap = false
    )
    public boolean receive(NetworkEvent.Context context) {
        ServerPlayerEntity player = context.getSender();
        context.enqueueWork(() -> {
            if (this.open) {
                ItemStack stack;
                Optional<ImmutableTriple<String, Integer, ItemStack>> backpack = CuriosApi.getCuriosHelper().findEquippedCurio(ForgeRegistries.ITEMS.getValue(new ResourceLocation("quark", "backpack")), player);
                if (backpack.isPresent()) {
                    stack = backpack.get().getRight();
                } else {
                    stack = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
                }
                if (stack.getItem() instanceof INamedContainerProvider) {
                    ItemStack holding = player.inventory.getItemStack();
                    player.inventory.setItemStack(ItemStack.EMPTY);
                    NetworkHooks.openGui(player, (INamedContainerProvider) stack.getItem(), player.getPosition());
                    player.inventory.setItemStack(holding);
                }
            } else {
                BackpackContainer.saveCraftingInventory(player);
                player.openContainer = player.container;
            }

        });
        return true;
    }
}
