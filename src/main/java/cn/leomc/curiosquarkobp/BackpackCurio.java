package cn.leomc.curiosquarkobp;


import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import vazkii.quark.addons.oddities.module.BackpackModule;

import java.util.concurrent.atomic.AtomicBoolean;

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
                && !has(player);
    }


    private boolean has(LivingEntity entity) {
        LazyOptional<IItemHandlerModifiable> optional = CuriosApi.getCuriosHelper().getEquippedCurios(entity);
        AtomicBoolean has = new AtomicBoolean(false);
        optional.ifPresent(handler -> {
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (stack.getItem() == BackpackModule.backpack) {
                    has.set(true);
                    break;
                }
            }
        });
        return has.get();
    }


    @Override
    public ItemStack getStack() {
        return backpack;
    }


}
