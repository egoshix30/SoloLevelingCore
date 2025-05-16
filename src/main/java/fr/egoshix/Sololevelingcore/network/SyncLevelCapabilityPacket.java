package fr.egoshix.Sololevelingcore.network;

import fr.egoshix.Sololevelingcore.leveling.LevelCapability;
import fr.egoshix.Sololevelingcore.leveling.LevelProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncLevelCapabilityPacket {
    public int level, xp, xpNext;
    public int monstersKilled, totalXpGained, maxLevelReached;

    public SyncLevelCapabilityPacket(int level, int xp, int xpNext,
                                     int monstersKilled, int totalXpGained, int maxLevelReached) {
        this.level = level;
        this.xp = xp;
        this.xpNext = xpNext;
        this.monstersKilled = monstersKilled;
        this.totalXpGained = totalXpGained;
        this.maxLevelReached = maxLevelReached;
    }

    public SyncLevelCapabilityPacket(FriendlyByteBuf buf) {
        level = buf.readInt();
        xp = buf.readInt();
        xpNext = buf.readInt();
        monstersKilled = buf.readInt();
        totalXpGained = buf.readInt();
        maxLevelReached = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(level);
        buf.writeInt(xp);
        buf.writeInt(xpNext);
        buf.writeInt(monstersKilled);
        buf.writeInt(totalXpGained);
        buf.writeInt(maxLevelReached);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                LevelProvider prov = LevelCapability.get(mc.player);
                if (prov != null) {
                    // Extrait côté client
                    prov.getData().setLevel(level);
                    prov.getData().setXp(xp);
                    prov.getData().setTotalXpGained(totalXpGained);
                    prov.getStats().setMonstersKilled(monstersKilled);
                    prov.getStats().setTotalXpGained(totalXpGained);
                    prov.getStats().setMaxLevelReached(maxLevelReached);

                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
