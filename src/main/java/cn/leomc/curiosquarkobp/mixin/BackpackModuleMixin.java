package cn.leomc.curiosquarkobp.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
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
            boolean hasBackpack = CuriosApi.getCuriosHelper().findFirstCurio(livingEntity, BackpackModule.backpack).isPresent();
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
            Optional<SlotResult> result = CuriosApi.getCuriosHelper().findFirstCurio(livingEntity, BackpackModule.backpack);
            if (result.isPresent() && result.get().stack() == stack)
                cir.setReturnValue(true);
        }
    }


}
