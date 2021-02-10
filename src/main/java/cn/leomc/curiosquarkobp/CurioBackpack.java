package cn.leomc.curiosquarkobp;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import top.theillusivec4.curios.api.type.capability.ICurio;
import vazkii.quark.addons.oddities.client.model.BackpackModel;

public class CurioBackpack implements ICurio {

    protected ItemStack stack;
    protected BackpackModel model;

    public CurioBackpack(ItemStack stack) {
        this.stack = stack;
        this.model = new BackpackModel();
    }

    @Override
    public boolean canRender(String identifier, int index, LivingEntity livingEntity) {
        return true;
    }

    //TODO: Model render
    @Override
    public void render(String identifier, int index, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        /*int color = ((DyeableArmorItem) stack.getItem()).getColor(stack);
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        model.render();
         */
    }
}
