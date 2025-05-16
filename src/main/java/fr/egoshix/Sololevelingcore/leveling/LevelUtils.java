package fr.egoshix.Sololevelingcore.leveling;

import fr.egoshix.Sololevelingcore.SoloLevelingCore;
import fr.egoshix.Sololevelingcore.network.SyncLevelCapabilityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public class LevelUtils {

    public static void syncLevelToClient(ServerPlayer player) {
        LevelProvider prov = LevelCapability.get(player);
        if (prov != null) {
            PlayerStats stats = prov.getStats();
            SoloLevelingCore.NETWORK_CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncLevelCapabilityPacket(
                            prov.getData().getLevel(),
                            prov.getData().getXp(),
                            prov.getData().xpForNextLevel(),
                            stats.getMonstersKilled(),
                            stats.getTotalXpGained(),
                            stats.getMaxLevelReached()
                    )
            );
        }
    }
}
