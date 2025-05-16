package fr.egoshix.Sololevelingcore;

import fr.egoshix.Sololevelingcore.network.SyncLevelCapabilityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod("sololevelingcore")
public class SoloLevelingCore {

    public static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel NETWORK_CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("sololevelingcore:main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public SoloLevelingCore() {

        NETWORK_CHANNEL.registerMessage(
                0,
                SyncLevelCapabilityPacket.class,
                SyncLevelCapabilityPacket::toBytes,
                SyncLevelCapabilityPacket::new,
                SyncLevelCapabilityPacket::handle
        );
        MinecraftForge.EVENT_BUS.register(this);
    }
}
