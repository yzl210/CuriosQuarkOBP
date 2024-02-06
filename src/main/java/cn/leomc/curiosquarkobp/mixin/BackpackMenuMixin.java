package cn.leomc.curiosquarkobp.mixin;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.violetmoon.quark.addons.oddities.inventory.BackpackMenu;
import org.violetmoon.quark.addons.oddities.inventory.slot.BackpackSlot;
import org.violetmoon.quark.addons.oddities.module.BackpackModule;
import org.violetmoon.quark.base.util.InventoryIIH;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

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
        Optional<SlotResult> result = CuriosApi.getCuriosInventory(player).map(inv -> inv.findFirstCurio(BackpackModule.backpack)).orElse(Optional.empty());

        Slot anchor = slots.get(9);
        int left = anchor.x;
        int top = anchor.y - 58;

        if (player.getInventory().armor.get(2).getItem() != BackpackModule.backpack && result.isPresent()) {
            InventoryIIH inv = new InventoryIIH(result.get().stack());

            for (int i = 0; i < 3; ++i)
                for (int j = 0; j < 9; ++j) {
                    int k = j + i * 9;
                    addSlot(new BackpackSlot(inv, k, left + j * 18, top + i * 18));
                }
        }
    }
}
