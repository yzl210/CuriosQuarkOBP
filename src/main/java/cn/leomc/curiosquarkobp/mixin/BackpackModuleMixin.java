package cn.leomc.curiosquarkobp.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;
import vazkii.quark.addons.oddities.module.BackpackModule;
import vazkii.quark.base.module.QuarkModule;

import java.util.Optional;

@Mixin(BackpackModule.class)
public abstract class BackpackModuleMixin extends QuarkModule {

    @Inject(
            method = "isEntityWearingBackpack(Lnet/minecraft/world/entity/Entity;)Z",
            at = @At("RETURN"),
            cancellable = true,
            remap = false
    )
    private static void isEntityWearingBackpack(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof LivingEntity livingEntity) {
            boolean hasBackpack = CuriosApi.getCuriosHelper().findEquippedCurio(BackpackModule.backpack, livingEntity).isPresent();
            if (hasBackpack)
                cir.setReturnValue(true);
        }
    }

    @Inject(
            method = "isEntityWearingBackpack(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Z",
            at = @At("RETURN"),
            cancellable = true,
            remap = false
    )
    private static void isEntityWearingBackpack(Entity entity, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof LivingEntity livingEntity) {
            Optional<ImmutableTriple<String, Integer, ItemStack>> optional = CuriosApi.getCuriosHelper().findEquippedCurio(BackpackModule.backpack, livingEntity);
            if (optional.isPresent() && optional.get().getRight() == stack)
                cir.setReturnValue(true);
        }
    }


}
