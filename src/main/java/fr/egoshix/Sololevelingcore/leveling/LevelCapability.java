package fr.egoshix.Sololevelingcore.leveling;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "sololevelingcore")
public class LevelCapability {

    public static final Capability<LevelProvider> LEVEL = CapabilityManager.get(new CapabilityToken<>() {});

    public static final ResourceLocation LEVEL_ID = new ResourceLocation("sololevelingcore:level");

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(LEVEL_ID, new LevelProvider());
        }
    }

    public static LevelProvider get(Player player) {
        if (player == null) return null;
        return player.getCapability(LEVEL).orElse(null);
    }
}
