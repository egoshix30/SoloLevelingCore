package fr.egoshix.Sololevelingcore.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;

public class PortalDungeon {

    public static void teleportToDungeon(ServerPlayer player) {
        ServerLevel world = player.serverLevel();

        // Position du donjon (ex : très profond, Y = 10)
        int x = player.getBlockX() + 100 + (int) (Math.random() * 100);
        int z = player.getBlockZ() + 100 + (int) (Math.random() * 100);
        int y = 10;

        BlockPos center = new BlockPos(x, y, z);

        // Génère un cube/donjon simple (9x9x5)
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                for (int dy = 0; dy < 5; dy++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    if (dy == 0 || dy == 4 || Math.abs(dx) == 4 || Math.abs(dz) == 4) {
                        world.setBlock(pos, Blocks.DEEPSLATE_BRICKS.defaultBlockState(), 3); // Murs/plafond/sol
                    } else {
                        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3); // Salle vide
                    }
                }
            }
        }

        // Spawn le joueur au centre
        player.teleportTo(world, x + 0.5, y + 1, z + 0.5, player.getYRot(), player.getXRot());

        // Optionnel : Message stylé
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§5[SOLO LEVELING] Bienvenue dans le donjon !"));
    }
}
