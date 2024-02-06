package cn.leomc.curiosquarkobp;


import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.violetmoon.quark.addons.oddities.module.BackpackModule;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.type.capability.ICurio;

@Mod(CuriosQuarkOdditiesBackpack.MODID)
public class CuriosQuarkOdditiesBackpack {

    public static final String MODID = "curiosquarkobp";

    public static final Logger LOGGER = LogManager.getLogger();

    public CuriosQuarkOdditiesBackpack() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::clientSetup);

    }

    private void clientSetup(FMLClientSetupEvent event) {
        CuriosRendererRegistry.register(BackpackModule.backpack, BackpackCurioRenderer::new);
    }

    @Mod.EventBusSubscriber(modid = MODID)
    public static class Events {
        @SubscribeEvent
        public static void attachCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
            ItemStack stack = event.getObject();

            if (stack.getItem() == BackpackModule.backpack) {
                ICurio curioBackpack = new BackpackCurio(stack);
                event.addCapability(CuriosCapability.ID_ITEM, CuriosApi.createCurioProvider(curioBackpack));
            }
        }

        @SubscribeEvent
        public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player) || player.isDeadOrDying() || player.isRemoved() || event.getSlot() != EquipmentSlot.CHEST)
                return;

            CuriosApi.getCuriosInventory(player).ifPresent(inventory -> {
                if (event.getTo().getItem() == BackpackModule.backpack && !inventory.findCurios(BackpackModule.backpack).isEmpty()) {
                    player.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
                    if (!player.addItem(event.getTo()))
                        player.drop(event.getTo(), false, true);
                }
            });

        }

    }


}
