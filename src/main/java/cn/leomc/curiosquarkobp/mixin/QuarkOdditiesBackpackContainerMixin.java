package cn.leomc.curiosquarkobp.mixin;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.CuriosApi;
import vazkii.arl.util.InventoryIIH;
import vazkii.quark.addons.oddities.container.BackpackContainer;
import vazkii.quark.addons.oddities.container.SlotCachingItemHandler;
import vazkii.quark.addons.oddities.module.BackpackModule;

import java.util.Optional;

@Mixin(BackpackContainer.class)
public class QuarkOdditiesBackpackContainerMixin extends PlayerContainer {

    public QuarkOdditiesBackpackContainerMixin(PlayerInventory playerInventory, boolean localWorld, PlayerEntity playerIn) {
        super(playerInventory, localWorld, playerIn);
    }

    @Inject(
            method = "<init>",
            at = @At("RETURN"),
            remap = false
    )
    private void init(int windowId, PlayerEntity player, CallbackInfo ci) {
        Optional<ImmutableTriple<String, Integer, ItemStack>> optional = CuriosApi.getCuriosHelper().findEquippedCurio(BackpackModule.backpack, player);
        if (player.inventory.armorInventory.get(2).getItem() != BackpackModule.backpack && optional.isPresent()) {
            Slot anchor = this.inventorySlots.get(9);
            int left = anchor.xPos;
            int top = anchor.yPos - 58;
            InventoryIIH inv = new InventoryIIH(optional.get().getRight());
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 9; ++j) {
                    int k = j + i * 9;
                    this.addSlot(new SlotCachingItemHandler(inv, k, left + j * 18, top + i * 18));
                }
            }
        }
    }
}
