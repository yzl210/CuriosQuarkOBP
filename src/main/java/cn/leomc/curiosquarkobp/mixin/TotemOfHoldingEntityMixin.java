package cn.leomc.curiosquarkobp.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.violetmoon.quark.addons.oddities.entity.TotemOfHoldingEntity;
import org.violetmoon.quark.addons.oddities.item.BackpackItem;
import org.violetmoon.quark.content.tweaks.compat.TotemOfHoldingCuriosCompat;

import java.util.List;

@Mixin(TotemOfHoldingEntity.class)
public abstract class TotemOfHoldingEntityMixin extends Entity {

    @Shadow
    private List<ItemStack> equipedCurios;

    @Shadow
    protected abstract Player getOwnerEntity();

    public TotemOfHoldingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @ModifyVariable(
            method = "skipAttackInteraction",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;", ordinal = 0, shift = At.Shift.BEFORE),
            name = "stack"
    )
    private ItemStack checkBackpack(ItemStack itemStack) {
        if (itemStack.getItem() instanceof BackpackItem && TotemOfHoldingCuriosCompat.equipCurios(getOwnerEntity(), equipedCurios, itemStack) == null)
            return ItemStack.EMPTY;
        return itemStack;
    }
}

