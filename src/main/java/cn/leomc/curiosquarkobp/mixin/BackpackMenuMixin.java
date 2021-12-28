package cn.leomc.curiosquarkobp.mixin;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.CuriosApi;
import vazkii.arl.util.InventoryIIH;
import vazkii.quark.addons.oddities.inventory.BackpackMenu;
import vazkii.quark.addons.oddities.inventory.SlotCachingItemHandler;
import vazkii.quark.addons.oddities.module.BackpackModule;

import java.util.Optional;

@Mixin(BackpackMenu.class)
public abstract class BackpackMenuMixin extends InventoryMenu {

    public BackpackMenuMixin(Inventory p_39706_, boolean p_39707_, Player p_39708_) {
        super(p_39706_, p_39707_, p_39708_);
    }

    @Inject(
            method = "<init>",
            at = @At("RETURN"),
            remap = false
    )
    private void init(int windowId, Player player, CallbackInfo ci) {
        Optional<ImmutableTriple<String, Integer, ItemStack>> optional = CuriosApi.getCuriosHelper().findEquippedCurio(BackpackModule.backpack, player);

        Slot anchor = slots.get(9);
        int left = anchor.x;
        int top = anchor.y - 58;

        if (player.getInventory().armor.get(2).getItem() != BackpackModule.backpack && optional.isPresent()) {
            InventoryIIH inv = new InventoryIIH(optional.get().getRight());

            for (int i = 0; i < 3; ++i)
                for (int j = 0; j < 9; ++j) {
                    int k = j + i * 9;
                    addSlot(new SlotCachingItemHandler(inv, k, left + j * 18, top + i * 18));
                }
        }
    }
}
