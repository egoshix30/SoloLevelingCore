package fr.egoshix.Sololevelingcore.leveling;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

// AJOUT DE CET IMPORT :
import fr.egoshix.Sololevelingcore.leveling.LevelUtils;

@Mod.EventBusSubscriber(modid = "sololevelingcore")
public class LevelEvents {

    public static void showXpActionBar(ServerPlayer player, int amount) {
        player.displayClientMessage(
                Component.literal("+" + amount + " XP").withStyle(ChatFormatting.DARK_PURPLE),
                true
        );
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            LevelProvider oldProv = LevelCapability.get(event.getOriginal());
            LevelProvider newProv = LevelCapability.get(event.getEntity());
            if (oldProv != null && newProv != null) {
                if (oldProv.getData() != null && newProv.getData() != null)
                    newProv.getData().copyFrom(oldProv.getData());
                if (oldProv.getStats() != null && newProv.getStats() != null)
                    newProv.getStats().copyFrom(oldProv.getStats());
                if (oldProv.getDailyQuest() != null && newProv.getDailyQuest() != null)
                    newProv.getDailyQuest().copyFrom(oldProv.getDailyQuest());
            }
        }
    }


    private static final long ONE_DAY_MS = 24L * 60 * 60 * 1000; // 24h en millisecondes

    public static void broadcastSoloLeveling(String msg) {
        MinecraftServer server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            for (ServerPlayer online : server.getPlayerList().getPlayers()) {
                online.sendSystemMessage(Component.literal(msg).withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD));
                online.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.MASTER, 1.5F, 0.8F);
            }
        }
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Player> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(new ResourceLocation("sololevelingcore", "level"), new LevelProvider());
        }
    }

    @SubscribeEvent
    public static void onMobKill(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        if (event.getEntity() instanceof net.minecraft.world.entity.monster.Monster) {
            LevelProvider prov = LevelCapability.get(player);
            int oldLevel = prov.getData().getLevel();
            String oldRank = RankUtils.getRankFromLevel(oldLevel);

            int xpAmount = 20;
            prov.getData().addXp(xpAmount);
            LevelUtils.syncLevelToClient(player);

            prov.getStats().addMonsterKill();
            prov.getStats().addXp(xpAmount, prov.getData().getLevel());

            DailyQuestData quest = prov.getDailyQuest();
            if (quest.getType() == DailyQuestData.QuestType.KILL_MOBS && !quest.isCompleted()) {
                quest.addProgress(1);
                if (quest.isCompleted()) {
                    player.sendSystemMessage(Component.literal("§dQuête journalière accomplie ! (Tuer des monstres)"));
                    prov.getData().addXp(quest.getTarget() * 3);
                    LevelUtils.syncLevelToClient(player);
                    showXpActionBar(player, quest.getTarget() * 3);
                }
            }

            showXpActionBar(player, xpAmount);

            int newLevel = prov.getData().getLevel();
            String newRank = RankUtils.getRankFromLevel(newLevel);

            if (!oldRank.equals(newRank)) {
                RankBonusUtils.applyBonusesForRank(player, newRank);
                player.sendSystemMessage(Component.literal("§6Nouveau rang : " + newRank + " ! Bonus débloqué."));
                if (newRank.equals("C") || newRank.equals("B") || newRank.equals("A") || newRank.startsWith("S")) {
                    broadcastSoloLeveling("★ [" + player.getName().getString() + "] est passé au rang " + newRank + " !");
                }
            }
            if (newLevel == 100 && oldLevel < 100) {
                broadcastSoloLeveling("⛧ [" + player.getName().getString() + "] a atteint le NIVEAU 100 !");
            }

            if (quest.getType() == DailyQuestData.QuestType.REACH_LEVEL && !quest.isCompleted()) {
                if (prov.getData().getLevel() >= quest.getTarget()) {
                    quest.addProgress(quest.getTarget());
                    player.sendSystemMessage(Component.literal("§dQuête journalière accomplie ! (Atteindre un niveau)"));
                    prov.getData().addXp(quest.getTarget() * 5);
                    LevelUtils.syncLevelToClient(player);
                    showXpActionBar(player, quest.getTarget() * 5);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;

        var block = event.getState().getBlock();
        int xpAmount = 0;
        if (block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE) {
            xpAmount = 60;
        } else if (block == Blocks.EMERALD_ORE || block == Blocks.DEEPSLATE_EMERALD_ORE) {
            xpAmount = 50;
        } else if (block == Blocks.NETHERITE_BLOCK) {
            xpAmount = 100;
        } else if (block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE) {
            xpAmount = 25;
        } else if (block == Blocks.IRON_ORE || block == Blocks.DEEPSLATE_IRON_ORE) {
            xpAmount = 10;
        } else if (block == Blocks.LAPIS_ORE || block == Blocks.DEEPSLATE_LAPIS_ORE) {
            xpAmount = 15;
        } else if (block == Blocks.REDSTONE_ORE || block == Blocks.DEEPSLATE_REDSTONE_ORE) {
            xpAmount = 7;
        } else if (block == Blocks.COAL_ORE || block == Blocks.DEEPSLATE_COAL_ORE) {
            xpAmount = 5;
        }

        if (xpAmount > 0) {
            LevelProvider prov = LevelCapability.get(player);
            int oldLevel = prov.getData().getLevel();
            String oldRank = RankUtils.getRankFromLevel(oldLevel);

            prov.getData().addXp(xpAmount);
            LevelUtils.syncLevelToClient(player);
            prov.getStats().addXp(xpAmount, prov.getData().getLevel());

            showXpActionBar(player, xpAmount);

            int newLevel = prov.getData().getLevel();
            String newRank = RankUtils.getRankFromLevel(newLevel);

            if (!oldRank.equals(newRank)) {
                RankBonusUtils.applyBonusesForRank(player, newRank);
                player.sendSystemMessage(Component.literal("§6Nouveau rang : " + newRank + " ! Bonus débloqué."));
                if (newRank.equals("C") || newRank.equals("B") || newRank.equals("A") || newRank.startsWith("S")) {
                    broadcastSoloLeveling("★ [" + player.getName().getString() + "] est passé au rang " + newRank + " !");
                }
            }
            if (newLevel == 100 && oldLevel < 100) {
                broadcastSoloLeveling("⛧ [" + player.getName().getString() + "] a atteint le NIVEAU 100 !");
            }

            DailyQuestData quest = prov.getDailyQuest();
            if (quest.getType() == DailyQuestData.QuestType.GAIN_XP && !quest.isCompleted()) {
                quest.addProgress(xpAmount);
                if (quest.isCompleted()) {
                    player.sendSystemMessage(Component.literal("§dQuête journalière accomplie ! (Gagner de l’XP)"));
                    prov.getData().addXp(quest.getTarget() * 2);
                    LevelUtils.syncLevelToClient(player);
                    showXpActionBar(player, quest.getTarget() * 2);
                }
            }
            if (quest.getType() == DailyQuestData.QuestType.REACH_LEVEL && !quest.isCompleted()) {
                if (prov.getData().getLevel() >= quest.getTarget()) {
                    quest.addProgress(quest.getTarget());
                    player.sendSystemMessage(Component.literal("§dQuête journalière accomplie ! (Atteindre un niveau)"));
                    prov.getData().addXp(quest.getTarget() * 5);
                    LevelUtils.syncLevelToClient(player);
                    showXpActionBar(player, quest.getTarget() * 5);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onCommand(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("level")
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            LevelProvider prov = LevelCapability.get(player);
                            LevelData data = prov.getData();
                            String rank = RankUtils.getRankFromLevel(data.getLevel());
                            ctx.getSource().sendSuccess(() -> Component.literal(
                                    "Niveau : " + data.getLevel() +
                                            " | XP : " + data.getXp() + "/" + data.xpForNextLevel() +
                                            " | Rang : " + rank
                            ), false);
                            return Command.SINGLE_SUCCESS;
                        })
        );

        event.getDispatcher().register(
                Commands.literal("addxp")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                    LevelProvider prov = LevelCapability.get(player);
                                    int oldLevel = prov.getData().getLevel();
                                    String oldRank = RankUtils.getRankFromLevel(oldLevel);

                                    prov.getData().addXp(amount);
                                    LevelUtils.syncLevelToClient(player);
                                    prov.getStats().addXp(amount, prov.getData().getLevel());

                                    DailyQuestData quest = prov.getDailyQuest();
                                    if (quest.getType() == DailyQuestData.QuestType.GAIN_XP && !quest.isCompleted()) {
                                        quest.addProgress(amount);
                                        if (quest.isCompleted()) {
                                            player.sendSystemMessage(Component.literal("§dQuête journalière accomplie ! (Gagner de l’XP)"));
                                            prov.getData().addXp(quest.getTarget() * 2);
                                            LevelUtils.syncLevelToClient(player);
                                            showXpActionBar(player, quest.getTarget() * 2);
                                        }
                                    }
                                    if (quest.getType() == DailyQuestData.QuestType.REACH_LEVEL && !quest.isCompleted()) {
                                        if (prov.getData().getLevel() >= quest.getTarget()) {
                                            quest.addProgress(quest.getTarget());
                                            player.sendSystemMessage(Component.literal("§dQuête journalière accomplie ! (Atteindre un niveau)"));
                                            prov.getData().addXp(quest.getTarget() * 5);
                                            LevelUtils.syncLevelToClient(player);
                                            showXpActionBar(player, quest.getTarget() * 5);
                                        }
                                    }

                                    showXpActionBar(player, amount);

                                    int newLevel = prov.getData().getLevel();
                                    String newRank = RankUtils.getRankFromLevel(newLevel);

                                    if (!oldRank.equals(newRank)) {
                                        RankBonusUtils.applyBonusesForRank(player, newRank);
                                        player.sendSystemMessage(Component.literal("§6Nouveau rang : " + newRank + " ! Bonus débloqué."));
                                        if (newRank.equals("C") || newRank.equals("B") || newRank.equals("A") || newRank.startsWith("S")) {
                                            broadcastSoloLeveling("★ [" + player.getName().getString() + "] est passé au rang " + newRank + " !");
                                        }
                                    }
                                    if (newLevel == 100 && oldLevel < 100) {
                                        broadcastSoloLeveling("⛧ [" + player.getName().getString() + "] a atteint le NIVEAU 100 !");
                                    }

                                    ctx.getSource().sendSuccess(() -> Component.literal("§b+" + amount + " XP ajoutés !"), false);
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
        );

        event.getDispatcher().register(
                Commands.literal("resetlevel")
                        .requires(source -> source.hasPermission(2))
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            LevelProvider prov = LevelCapability.get(player);

                            prov.getData().reset();
                            prov.getStats().reset();
                            LevelUtils.syncLevelToClient(player);
                            RankBonusUtils.removeAllBonuses(player);

                            player.sendSystemMessage(Component.literal("§cTu es revenu au niveau 1 (reset Solo Leveling) !"));

                            String newRank = RankUtils.getRankFromLevel(1);
                            RankBonusUtils.applyBonusesForRank(player, newRank);

                            return Command.SINGLE_SUCCESS;
                        })
        );

        event.getDispatcher().register(
                Commands.literal("infoplayer")
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            LevelProvider prov = LevelCapability.get(player);
                            LevelData data = prov.getData();
                            PlayerStats stats = prov.getStats();
                            String rank = RankUtils.getRankFromLevel(data.getLevel());

                            ctx.getSource().sendSuccess(() -> Component.literal("§d==== Infos Joueur ===="), false);
                            ctx.getSource().sendSuccess(() -> Component.literal(
                                    "Niveau : " + data.getLevel() + " | Rang : " + rank), false);
                            ctx.getSource().sendSuccess(() -> Component.literal(
                                    "XP actuelle : " + data.getXp() + "/" + data.xpForNextLevel()), false);
                            ctx.getSource().sendSuccess(() -> Component.literal(
                                    "XP totale gagnée : " + stats.getTotalXpGained()), false);
                            ctx.getSource().sendSuccess(() -> Component.literal(
                                    "Monstres tués : " + stats.getMonstersKilled()), false);
                            ctx.getSource().sendSuccess(() -> Component.literal(
                                    "Plus haut niveau atteint : " + stats.getMaxLevelReached()), false);
                            return Command.SINGLE_SUCCESS;
                        })
        );

        event.getDispatcher().register(
                Commands.literal("dailyquest")
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            LevelProvider prov = LevelCapability.get(player);
                            DailyQuestData quest = prov.getDailyQuest();

                            String status = quest.isCompleted() ? "§aACCOMPLIE" : "§eEn cours";
                            String obj;
                            switch(quest.getType()) {
                                case KILL_MOBS -> obj = "Tuer " + quest.getTarget() + " monstres (" + quest.getProgress() + "/" + quest.getTarget() + ")";
                                case GAIN_XP -> obj = "Gagner " + quest.getTarget() + " XP (" + quest.getProgress() + "/" + quest.getTarget() + ")";
                                case REACH_LEVEL -> obj = "Atteindre le niveau " + quest.getTarget();
                                default -> obj = "Aucune quête";
                            }

                            ctx.getSource().sendSuccess(() -> Component.literal("§b==== Quête journalière ===="), false);
                            ctx.getSource().sendSuccess(() -> Component.literal(obj), false);
                            ctx.getSource().sendSuccess(() -> Component.literal("Statut : " + status), false);
                            if (quest.isCompleted()) {
                                ctx.getSource().sendSuccess(() -> Component.literal("§6Attends 24h pour recevoir une nouvelle quête !"), false);
                            }
                            return Command.SINGLE_SUCCESS;
                        })
        );
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        LevelProvider prov = LevelCapability.get(player);
        LevelData data = prov.getData();
        String rank = RankUtils.getRankFromLevel(data.getLevel());
        RankBonusUtils.applyBonusesForRank(player, rank);

        // SYNC CAPABILITY AU LOGIN !
        if (player instanceof ServerPlayer serverPlayer) {
            LevelUtils.syncLevelToClient(serverPlayer);
        }

        DailyQuestData quest = prov.getDailyQuest();
        long now = System.currentTimeMillis();
        if ((quest.isCompleted() && (now - quest.getLastGenerated() >= ONE_DAY_MS)) || quest.getTarget() == 0) {
            quest.generateNewQuest(data.getLevel());
            player.sendSystemMessage(Component.literal("§6Nouvelle quête journalière ! (/dailyquest pour voir ton objectif)"));
        }
    }
}
