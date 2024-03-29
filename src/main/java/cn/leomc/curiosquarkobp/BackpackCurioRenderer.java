package cn.leomc.curiosquarkobp;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.violetmoon.quark.addons.oddities.item.BackpackItem;
import org.violetmoon.quark.base.client.handler.ModelHandler;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

@SuppressWarnings({"unchecked", "rawtypes"})
public class BackpackCurioRenderer implements ICurioRenderer {

    private final HumanoidModel model = ModelHandler.armorModel(ModelHandler.backpack, EquipmentSlot.CHEST);

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack poseStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource multiBufferSource, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (stack.getItem() instanceof BackpackItem backpack) {
            EntityModel<T> entityModel = renderLayerParent.getModel();
            if (entityModel instanceof HumanoidModel<T> humanoidModel)
                humanoidModel.copyPropertiesTo(model);
            else
                entityModel.copyPropertiesTo(model);
            boolean hasFoil = stack.hasFoil();
            int i = backpack.getColor(stack);
            float r = (float) (i >> 16 & 255) / 255.0F;
            float g = (float) (i >> 8 & 255) / 255.0F;
            float b = (float) (i & 255) / 255.0F;
            render(poseStack, multiBufferSource, light, hasFoil, model, r, g, b, getArmorResource(stack, null));
            render(poseStack, multiBufferSource, light, hasFoil, model, 1.0F, 1.0F, 1.0F, getArmorResource(stack, "overlay"));
        }
    }

    private void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int light, boolean glint, Model backpackModel, float r, float g, float b, ResourceLocation armorResource) {
        VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(multiBufferSource, RenderType.armorCutoutNoCull(armorResource), false, glint);
        backpackModel.renderToBuffer(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY, r, g, b, 1.0F);
    }

    private ResourceLocation getArmorResource(ItemStack backpack, String type) {
        return new ResourceLocation(backpack.getItem().getArmorTexture(backpack, Minecraft.getInstance().player, EquipmentSlot.CHEST, type));
    }

}
