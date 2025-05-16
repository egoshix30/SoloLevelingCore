package fr.egoshix.Sololevelingcore.portal;

import fr.egoshix.Sololevelingcore.portal.PortalSpawner;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "sololevelingcore")
public class PortalCommand {

    @SubscribeEvent
    public static void onRegisterCommand(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("spawnportal")
                        .requires(source -> source.hasPermission(2)) // OP only
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            var world = player.serverLevel();
                            var pos = player.blockPosition().offset(2, 0, 0); // à côté du joueur
                            PortalSpawner.generatePortal(world, pos);

                            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                                    "§5[SOLO LEVELING] Portail géant généré à côté de toi !"
                            ));
                            return 1;
                        })
        );
    }
}
