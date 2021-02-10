package cn.leomc.curiosquarkobp;


import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.type.capability.ICurio;
import vazkii.quark.addons.oddities.module.BackpackModule;

@Mod(CuriosQuarkOdditiesBackpack.MODID)
public class CuriosQuarkOdditiesBackpack {

    public static final String MODID = "curiosquarkobp";

    private static final Logger LOGGER = LogManager.getLogger();

    public CuriosQuarkOdditiesBackpack() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::enqueueIMC);
    }


    private void enqueueIMC(InterModEnqueueEvent event) {
        InterModComms.sendTo("curios", "register_type", () ->
                SlotTypePreset.BACK.getMessageBuilder().build());
    }

    @Mod.EventBusSubscriber(modid = MODID)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void attachCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
            ItemStack stack = event.getObject();

            if (stack.getItem() == BackpackModule.backpack) {
                ICurio curioBackpack = new CurioBackpack(stack);

                event.addCapability(CuriosCapability.ID_ITEM, new ICapabilityProvider() {
                    LazyOptional<ICurio> curio = LazyOptional.of(() -> curioBackpack);

                    @Override
                    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
                        return CuriosCapability.ITEM.orEmpty(cap, curio);
                    }

                });
            }
        }
    }


}
