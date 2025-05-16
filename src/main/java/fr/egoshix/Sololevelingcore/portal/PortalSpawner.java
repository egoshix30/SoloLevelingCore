package fr.egoshix.Sololevelingcore.portal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


import java.util.Random;

@Mod.EventBusSubscriber(modid = "sololevelingcore")
public class PortalSpawner {

    private static final int TICKS_BETWEEN_PORTALS = 20 * 60 * 5; // 5 minutes (baisse à 40 pour test rapide)
    private static int ticks = 0;
    private static final Random rand = new Random();

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.level.isClientSide() || !(event.level instanceof ServerLevel world)) return;
        if (event.phase != TickEvent.Phase.END) return;

        ticks++;
        if (ticks < TICKS_BETWEEN_PORTALS) return;
        ticks = 0;

        // Choisir une position random près du spawn (pour test)
        BlockPos spawn = world.getSharedSpawnPos();
        int dx = rand.nextInt(80) - 40;
        int dz = rand.nextInt(80) - 40;
        BlockPos pos = world.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, spawn.offset(dx, 0, dz));
        // Vérifie que l'emplacement est vide
        if (!world.isEmptyBlock(pos) || !world.isEmptyBlock(pos.above())) return;

        // Génère le portail géant
        generatePortal(world, pos);

        // Message avec coordonnées
        world.getServer().getPlayerList().broadcastSystemMessage(
                net.minecraft.network.chat.Component.literal(
                        "§5[SOLO LEVELING] Un portail géant est apparu en " +
                                pos.getX() + " " + pos.getY() + " " + pos.getZ() + " !"
                ), false
        );
    }

    // Méthode pour générer le portail géant 4x6 (cadre vertical)
    public static void generatePortal(ServerLevel world, BlockPos pos) {
        // Pilier gauche/droit (6 de haut)
        for (int i = 0; i < 6; i++) {
            world.setBlock(pos.offset(0, i, 0), Blocks.AMETHYST_BLOCK.defaultBlockState(), 2);
            world.setBlock(pos.offset(3, i, 0), Blocks.AMETHYST_BLOCK.defaultBlockState(), 2);
        }
        // Barres haut/bas (4 de large)
        for (int i = 0; i < 4; i++) {
            world.setBlock(pos.offset(i, 0, 0), Blocks.AMETHYST_BLOCK.defaultBlockState(), 2);
            world.setBlock(pos.offset(i, 5, 0), Blocks.AMETHYST_BLOCK.defaultBlockState(), 2);
        }
        // Optionnel : particules, etc. à l'intérieur
    }
}