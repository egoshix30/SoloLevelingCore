package fr.egoshix.Sololevelingcore.leveling;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import java.util.UUID;

public class RankBonusUtils {

    // UUIDs uniques pour chaque type de bonus (pour éviter les doublons sur le joueur)
    public static final UUID HEALTH_UUID = UUID.fromString("b19e4b76-96a8-47b9-bce8-1b1e1e9ee4e1");
    public static final UUID DAMAGE_UUID = UUID.fromString("e73432c2-30bc-46b7-9715-9956a88b9216");
    public static final UUID SPEED_UUID  = UUID.fromString("5be1e01d-5f0a-4c7e-83c1-743c66b25477");

    /**
     * Donne les bonus pour chaque rang (adapter à ta sauce)
     */
    public static void applyBonusesForRank(Player player, String rank) {
        // D'abord retire les bonus existants (pour éviter les doublons si le joueur downgrade, respawn, etc.)
        removeAllBonuses(player);

        switch(rank) {
            case "D" -> {
                addModifier(player, Attributes.MAX_HEALTH, HEALTH_UUID, 4.0, "Rank D Health Bonus"); // +2 coeurs
            }
            case "C" -> {
                addModifier(player, Attributes.MAX_HEALTH, HEALTH_UUID, 8.0, "Rank C Health Bonus"); // +4 coeurs
                addModifier(player, Attributes.ATTACK_DAMAGE, DAMAGE_UUID, 2.0, "Rank C Damage Bonus");
            }
            case "B" -> {
                addModifier(player, Attributes.MAX_HEALTH, HEALTH_UUID, 12.0, "Rank B Health Bonus"); // +6 coeurs
                addModifier(player, Attributes.ATTACK_DAMAGE, DAMAGE_UUID, 3.0, "Rank B Damage Bonus");
                addModifier(player, Attributes.MOVEMENT_SPEED, SPEED_UUID, 0.02, "Rank B Speed Bonus");
            }
            case "A" -> {
                addModifier(player, Attributes.MAX_HEALTH, HEALTH_UUID, 16.0, "Rank A Health Bonus");
                addModifier(player, Attributes.ATTACK_DAMAGE, DAMAGE_UUID, 4.0, "Rank A Damage Bonus");
                addModifier(player, Attributes.MOVEMENT_SPEED, SPEED_UUID, 0.04, "Rank A Speed Bonus");
            }
            case "S" -> {
                addModifier(player, Attributes.MAX_HEALTH, HEALTH_UUID, 20.0, "Rank S Health Bonus");
                addModifier(player, Attributes.ATTACK_DAMAGE, DAMAGE_UUID, 6.0, "Rank S Damage Bonus");
                addModifier(player, Attributes.MOVEMENT_SPEED, SPEED_UUID, 0.07, "Rank S Speed Bonus");
            }
            case "SS" -> {
                addModifier(player, Attributes.MAX_HEALTH, HEALTH_UUID, 24.0, "Rank SS Health Bonus");
                addModifier(player, Attributes.ATTACK_DAMAGE, DAMAGE_UUID, 8.0, "Rank SS Damage Bonus");
                addModifier(player, Attributes.MOVEMENT_SPEED, SPEED_UUID, 0.09, "Rank SS Speed Bonus");
            }
            case "SSS" -> {
                addModifier(player, Attributes.MAX_HEALTH, HEALTH_UUID, 30.0, "Rank SSS Health Bonus");
                addModifier(player, Attributes.ATTACK_DAMAGE, DAMAGE_UUID, 12.0, "Rank SSS Damage Bonus");
                addModifier(player, Attributes.MOVEMENT_SPEED, SPEED_UUID, 0.12, "Rank SSS Speed Bonus");
            }
            // Pas de bonus pour le rang E
        }
        // Mets à jour la vie du joueur si on a augmenté le max health (important sinon les nouveaux cœurs n’apparaissent pas)
        player.setHealth(player.getMaxHealth());
    }

    public static void removeAllBonuses(Player player) {
        player.getAttribute(Attributes.MAX_HEALTH).removeModifier(HEALTH_UUID);
        player.getAttribute(Attributes.ATTACK_DAMAGE).removeModifier(DAMAGE_UUID);
        player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SPEED_UUID);
    }

    private static void addModifier(Player player, net.minecraft.world.entity.ai.attributes.Attribute attr, UUID uuid, double amount, String name) {
        // Ne stacke pas si déjà présent (sécurité)
        if (player.getAttribute(attr).getModifier(uuid) == null) {
            AttributeModifier modif = new AttributeModifier(uuid, name, amount, AttributeModifier.Operation.ADDITION);
            player.getAttribute(attr).addPermanentModifier(modif);
        }
    }
}
