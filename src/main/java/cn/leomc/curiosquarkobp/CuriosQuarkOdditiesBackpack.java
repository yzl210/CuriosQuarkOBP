package cn.leomc.curiosquarkobp;


import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.type.capability.ICurio;
import vazkii.quark.addons.oddities.module.BackpackModule;

import javax.annotation.Nonnull;

@Mod(CuriosQuarkOdditiesBackpack.MODID)
public class CuriosQuarkOdditiesBackpack {

    public static final String MODID = "curiosquarkobp";

    public CuriosQuarkOdditiesBackpack() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::enqueueIMC);
        eventBus.addListener(this::clientSetup);

    }

    private void clientSetup(FMLClientSetupEvent event) {
        CuriosRendererRegistry.register(BackpackModule.backpack, BackpackCurioRenderer::new);
    }

    private void enqueueIMC(InterModEnqueueEvent event) {
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () ->
                SlotTypePreset.BACK.getMessageBuilder().build());
    }

    @Mod.EventBusSubscriber(modid = MODID)
    public static class Events {
        @SubscribeEvent
        public static void attachCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
            ItemStack stack = event.getObject();

            if (stack.getItem() == BackpackModule.backpack) {
                ICurio curioBackpack = new BackpackCurio(stack);

                event.addCapability(CuriosCapability.ID_ITEM, new ICapabilityProvider() {
                    final LazyOptional<ICurio> curio = LazyOptional.of(() -> curioBackpack);

                    @Nonnull
                    @Override
                    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction side) {
                        return CuriosCapability.ITEM.orEmpty(capability, curio);
                    }

                });
            }
        }
    }


}
