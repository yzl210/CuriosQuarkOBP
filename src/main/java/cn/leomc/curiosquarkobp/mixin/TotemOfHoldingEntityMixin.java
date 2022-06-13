package cn.leomc.curiosquarkobp.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import vazkii.quark.addons.oddities.entity.TotemOfHoldingEntity;
import vazkii.quark.addons.oddities.module.TotemOfHoldingModule;

import java.util.LinkedList;
import java.util.List;

@Mixin(TotemOfHoldingEntity.class)
public abstract class TotemOfHoldingEntityMixin extends Entity {

    @Shadow
    private List<ItemStack> storedItems = new LinkedList<>();


    public TotemOfHoldingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    /**
     * @author yzl210
     */
    @Overwrite(remap = false)
    public boolean skipAttackInteraction(@NotNull Entity e) {
        if (!level.isClientSide && e instanceof Player player) {
            if (!TotemOfHoldingModule.allowAnyoneToCollect && !player.getAbilities().instabuild) {
                Player owner = getOwnerEntity();
                if (e != owner)
                    return false;
            }

            int drops = Math.min(storedItems.size(), 3 + level.random.nextInt(4));

            for (int i = 0; i < drops; i++) {
                final ItemStack[] stack = {storedItems.remove(0)};
                if (isCurios(stack[0])) {
                    CuriosApi.getCuriosHelper().getCuriosHandler(player).ifPresent(handler -> {
                        for (String identifier : CuriosApi.getCuriosHelper().getCurioTags(stack[0].getItem())) {
                            if (stack[0] == null)
                                break;
                            handler.getStacksHandler(identifier).ifPresent(stacks -> {
                                IDynamicStackHandler dynamicStack = stacks.getStacks();
                                for (int slot = 0; slot < dynamicStack.getSlots(); slot++) {
                                    if (!dynamicStack.getStackInSlot(slot).isEmpty())
                                        continue;
                                    boolean flag = true;
                                    SlotContext context = null;
                                    LazyOptional<ICurio> curio = CuriosApi.getCuriosHelper().getCurio(stack[0]);
                                    if (curio.isPresent()) {
                                        NonNullList<Boolean> renders = stacks.getRenders();
                                        context = new SlotContext(identifier, player, slot, false,
                                                renders.size() > slot && renders.get(slot));
                                        flag = curio.resolve().get().canEquip(context);
                                    }
                                    if (flag) {
                                        dynamicStack.setStackInSlot(slot, stack[0]);
                                        stack[0] = null;
                                        if (curio.isPresent() && context != null)
                                            curio.resolve().get().onEquip(context, ItemStack.EMPTY);
                                        return;
                                    }
                                }
                            });
                        }
                    });
                }
                if (stack[0] != null) {
                    if (stack[0].getItem() instanceof ArmorItem armor) {
                        EquipmentSlot slot = armor.getSlot();
                        ItemStack curr = player.getItemBySlot(slot);

                        if (curr.isEmpty()) {
                            player.setItemSlot(slot, stack[0]);
                            stack[0] = null;
                        } else if (!EnchantmentHelper.hasBindingCurse(curr) && !EnchantmentHelper.hasBindingCurse(stack[0])) {
                            player.setItemSlot(slot, stack[0]);
                            stack[0] = curr;
                        }
                    } else if (stack[0].getItem() instanceof ShieldItem) {
                        ItemStack curr = player.getItemBySlot(EquipmentSlot.OFFHAND);

                        if (curr.isEmpty()) {
                            player.setItemSlot(EquipmentSlot.OFFHAND, stack[0]);
                            stack[0] = null;
                        }
                    }
                }

                if (stack[0] != null)
                    if (!player.addItem(stack[0]))
                        spawnAtLocation(stack[0], 0);
            }

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.DAMAGE_INDICATOR, getX(), getY() + 0.5, getZ(), drops, 0.1, 0.5, 0.1, 0);
                serverLevel.sendParticles(ParticleTypes.ENCHANTED_HIT, getX(), getY() + 0.5, getZ(), drops, 0.4, 0.5, 0.4, 0);
            }
        }

        return false;
    }

    private boolean isCurios(ItemStack stack) {
        return !CuriosApi.getCuriosHelper().getCurioTags(stack.getItem()).isEmpty();
    }

    @Shadow
    protected abstract Player getOwnerEntity();

}

