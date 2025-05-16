package fr.egoshix.Sololevelingcore.leveling;

import net.minecraft.nbt.CompoundTag;

public class PlayerStats {
    public int monstersKilled = 0;
    public int totalXpGained = 0;
    public int maxLevelReached = 1;

    public int getMonstersKilled() { return monstersKilled; }
    public int getTotalXpGained() { return totalXpGained; }
    public int getMaxLevelReached() { return maxLevelReached; }
    public void setMonstersKilled(int val) { this.monstersKilled = val; }
    public void setTotalXpGained(int val) { this.totalXpGained = val; }
    public void setMaxLevelReached(int val) { this.maxLevelReached = val; }

    public void addMonsterKill() { this.monstersKilled++; }
    public void addXp(int amount, int currentLevel) {
        this.totalXpGained += amount;
        if (currentLevel > this.maxLevelReached) {
            this.maxLevelReached = currentLevel;
        }
    }

    public void reset() {
        this.monstersKilled = 0;
        this.totalXpGained = 0;
        this.maxLevelReached = 1;
    }

    public void copyFrom(PlayerStats other) {
        if (other == null) return;
        this.monstersKilled = other.monstersKilled;
        this.totalXpGained = other.totalXpGained;
        this.maxLevelReached = other.maxLevelReached;
    }

    public CompoundTag saveNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("monstersKilled", this.monstersKilled);
        tag.putInt("totalXpGained", this.totalXpGained);
        tag.putInt("maxLevelReached", this.maxLevelReached);
        return tag;
    }

    public void loadNBT(CompoundTag tag) {
        this.monstersKilled = tag.getInt("monstersKilled");
        this.totalXpGained = tag.getInt("totalXpGained");
        this.maxLevelReached = tag.getInt("maxLevelReached");
    }
}
