package cn.leomc.curiosquarkobp;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.violetmoon.quark.addons.oddities.module.BackpackModule;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;


public class BackpackCurio implements ICurio {

    protected ItemStack backpack;

    public BackpackCurio(ItemStack backpack) {
        this.backpack = backpack;
    }


    @Override
    public boolean canEquipFromUse(SlotContext slotContext) {
        return true;
    }


    @Override
    public boolean canEquip(SlotContext slotContext) {
        return slotContext.entity() instanceof Player player
                && player.getItemBySlot(EquipmentSlot.CHEST).getItem() != BackpackModule.backpack
                && CuriosApi.getCuriosInventory(player).map(inv -> inv.findCurios(BackpackModule.backpack).isEmpty()).orElse(false);
    }

    @Override
    public ItemStack getStack() {
        return backpack;
    }
}
