package iguanaman.iguanatweakstconstruct.harvestlevels;

import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import iguanaman.iguanatweakstconstruct.harvestlevels.proxy.HarvestCommonProxy;
import iguanaman.iguanatweakstconstruct.reference.Reference;
import iguanaman.iguanatweakstconstruct.util.Log;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import net.minecraftforge.common.MinecraftForge;
import tconstruct.library.crafting.ModifyBuilder;
import tconstruct.library.modifier.ItemModifier;
import tconstruct.modifiers.tools.ModDurability;

import java.lang.reflect.Field;

/**
 * The Harvest-Tweaks Pulse. If this were a separate mod instead of pulse-module, it'd be a @Mod
 * This pulse modifies the harvest level of all tools and blocks.
 * Check util.HarvestLevels for more info.
 *
 * Check the oreDictlevels to get an idea of what can be harvested with each tier.
 */

@Pulse(id = Reference.PULSE_HARVESTTWEAKS, description = "Modify tool and item mining levels to create a tiered-ish progression")
public class IguanaHarvestLevelTweaks {
    @SidedProxy(clientSide = "iguanaman.iguanatweakstconstruct.harvestlevels.proxy.HarvestClientProxy", serverSide = "iguanaman.iguanatweakstconstruct.harvestlevels.proxy.HarvestCommonProxy")
    public static HarvestCommonProxy proxy;

    @Handler
    public void init(FMLInitializationEvent event)
    {
        // the only thing this does is replacing GUIs with our own GUIs to display the correct harvest levels
        proxy.initialize();

        // Remove Mininglevel-boost from diamond and emerald modifier.
        // We use reflection for that. Although that's quite.. meh it means any changes to tconstruct are registered and we don't have to do localization separately

        try {
            Log.info("Removing Mininglevel from Diamond/Emerald Modifier");
            Field maxLevel = ModDurability.class.getDeclaredField("miningLevel");
            for (ItemModifier mod : ModifyBuilder.instance.itemModifiers) {
                if (!(mod instanceof ModDurability))
                    continue;

                maxLevel.setAccessible(true);
                maxLevel.set((ModDurability)mod, 0);
            }
        } catch (NoSuchFieldException e) {
            Log.error(e.getMessage());
        } catch (IllegalAccessException e) {
            Log.error(e.getMessage());
        }
    }

    @Handler
    public void postInit(FMLPostInitializationEvent event)
    {
        TinkerMaterialTweaks.modifyToolMaterials();
        HarvestLevelTweaks.modifyHarvestLevels();
        MinecraftForge.EVENT_BUS.register(new VanillaToolTipHandler());
    }
}
