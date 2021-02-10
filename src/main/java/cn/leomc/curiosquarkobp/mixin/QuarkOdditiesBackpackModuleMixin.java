package cn.leomc.curiosquarkobp.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;
import vazkii.quark.addons.oddities.module.BackpackModule;
import vazkii.quark.base.module.QuarkModule;

@Mixin(BackpackModule.class)
public class QuarkOdditiesBackpackModuleMixin extends QuarkModule {

    @Inject(
            method = "isEntityWearingBackpack(Lnet/minecraft/entity/Entity;)Z",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void wearingBackpack(Entity e, CallbackInfoReturnable<Boolean> cir) {
        if (e instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) e;
            boolean hasBackpack = CuriosApi.getCuriosHelper().findEquippedCurio(BackpackModule.backpack, living).isPresent();
            if (hasBackpack)
                cir.setReturnValue(true);
        }
    }

    @Inject(
            method = "isEntityWearingBackpack(Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Z",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void wearingBackpackItemStack(Entity e, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        wearingBackpack(e, cir);
    }


}
