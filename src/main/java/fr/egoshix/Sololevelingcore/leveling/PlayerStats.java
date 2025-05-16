package fr.egoshix.Sololevelingcore.leveling;

import net.minecraft.nbt.CompoundTag;

public class PlayerStats {
    private int monstersKilled = 0;
    private int totalXpGained = 0;
    private int maxLevelReached = 1;

    public int getMonstersKilled() { return monstersKilled; }
    public int getTotalXpGained() { return totalXpGained; }
    public int getMaxLevelReached() { return maxLevelReached; }

    public void setMonstersKilled(int val) { this.monstersKilled = val; }
    public void setTotalXpGained(int val) { this.totalXpGained = val; }
    public void setMaxLevelReached(int val) { this.maxLevelReached = val; }

    public void addMonsterKill() { monstersKilled++; }
    public void addXp(int xp, int currentLevel) {
        totalXpGained += xp;
        if (currentLevel > maxLevelReached) maxLevelReached = currentLevel;
    }
    public void copyFrom(PlayerStats other) {
        this.monstersKilled = other.monstersKilled;
        this.totalXpGained = other.totalXpGained;
        this.maxLevelReached = other.maxLevelReached;
        // Ajoute d'autres champs si besoin
    }

    public void reset() {
        monstersKilled = 0;
        totalXpGained = 0;
        maxLevelReached = 1;
    }

    public CompoundTag saveNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("monstersKilled", monstersKilled);
        tag.putInt("totalXpGained", totalXpGained);
        tag.putInt("maxLevelReached", maxLevelReached);
        return tag;
    }

    public void loadNBT(CompoundTag tag) {
        if (tag.contains("monstersKilled")) monstersKilled = tag.getInt("monstersKilled");
        if (tag.contains("totalXpGained")) totalXpGained = tag.getInt("totalXpGained");
        if (tag.contains("maxLevelReached")) maxLevelReached = tag.getInt("maxLevelReached");
    }
}
