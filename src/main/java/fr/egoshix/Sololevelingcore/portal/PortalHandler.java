package fr.egoshix.Sololevelingcore.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "sololevelingcore")
public class PortalHandler {
    private static final HashMap<UUID, Integer> cooldowns = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.level().isClientSide() || !(event.player instanceof ServerPlayer player)) return;
        if (event.phase != TickEvent.Phase.END) return;

        UUID uuid = player.getUUID();
        cooldowns.putIfAbsent(uuid, 0);
        int cd = cooldowns.get(uuid);
        if (cd > 0) {
            cooldowns.put(uuid, cd - 1);
            return;
        }

        BlockPos feet = player.blockPosition();
        Level world = player.level();

        // Détection améliorée : sous les pieds OU en face du joueur
        boolean onAmethyst = world.getBlockState(feet).getBlock() == Blocks.AMETHYST_BLOCK;
        BlockPos inFront = feet.relative(player.getDirection());
        boolean facingAmethyst = world.getBlockState(inFront).getBlock() == Blocks.AMETHYST_BLOCK;

        if (onAmethyst || facingAmethyst) {
            cooldowns.put(uuid, 40);
            PortalDungeon.teleportToDungeon(player);
        }
    }
}
