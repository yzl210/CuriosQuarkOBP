package cn.leomc.curiosquarkobp;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.type.capability.ICurio;
import vazkii.quark.addons.oddities.client.model.BackpackModel;

public class CurioBackpack implements ICurio {

    protected ItemStack stack;
    @OnlyIn(Dist.CLIENT)
    protected BackpackModel model;

    public CurioBackpack(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public boolean canRender(String identifier, int index, LivingEntity livingEntity) {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(String identifier, int index, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (this.model == null)
            model = new BackpackModel();
        ArmorItem armoritem = (ArmorItem) stack.getItem();
        boolean hasEffect = stack.hasEffect();
        int i = ((net.minecraft.item.IDyeableArmorItem) armoritem).getColor(stack);
        EntityRenderer<?> entityRenderer = Minecraft.getInstance().getRenderManager().getRenderer(livingEntity);
        if (entityRenderer instanceof LivingRenderer) {
            try {
                ((LivingRenderer<LivingEntity, PlayerModel<LivingEntity>>) entityRenderer).getEntityModel().setModelAttributes(model);
            } catch (ClassCastException e) {
                e.printStackTrace();
                return;
            }
        }
        model.setVisible(false);
        model.bipedBody.showModel = true;
        model.bipedRightArm.showModel = true;
        model.bipedLeftArm.showModel = true;
        float r = (float) (i >> 16 & 255) / 255.0F;
        float g = (float) (i >> 8 & 255) / 255.0F;
        float b = (float) (i & 255) / 255.0F;
        render(matrixStack, renderTypeBuffer, light, hasEffect, model, r, g, b, getArmorResource(livingEntity, null));
        render(matrixStack, renderTypeBuffer, light, hasEffect, model, 1.0F, 1.0F, 1.0F, getArmorResource(livingEntity, "overlay"));
    }

    @OnlyIn(Dist.CLIENT)
    private void render(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, boolean glint, BackpackModel backpackModel, float r, float g, float b, ResourceLocation armorResource) {
        IVertexBuilder ivertexbuilder = ItemRenderer.getArmorVertexBuilder(renderTypeBuffer, RenderType.getArmorCutoutNoCull(armorResource), false, glint);
        backpackModel.render(matrixStack, ivertexbuilder, light, OverlayTexture.NO_OVERLAY, r, g, b, 1.0F);
    }

    @OnlyIn(Dist.CLIENT)
    private ResourceLocation getArmorResource(LivingEntity livingEntity, String type) {
        return new ResourceLocation(stack.getItem().getArmorTexture(stack, livingEntity, EquipmentSlotType.CHEST, type));
    }


}
