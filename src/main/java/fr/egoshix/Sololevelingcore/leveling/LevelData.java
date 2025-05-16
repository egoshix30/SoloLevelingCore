package fr.egoshix.Sololevelingcore.leveling;

import net.minecraft.nbt.CompoundTag;

public class LevelData {
    public int level = 1;
    public int xp = 0;
    public int totalXpGained = 0;

    public int getLevel() { return level; }
    public int getXp() { return xp; }
    public int getTotalXpGained() { return totalXpGained; }
    public void setLevel(int level) { this.level = Math.max(1, level); }
    public void setXp(int xp) { this.xp = Math.max(0, xp); }
    public void setTotalXpGained(int totalXpGained) { this.totalXpGained = Math.max(0, totalXpGained); }

    public void addXp(int amount) {
        this.xp += amount;
        this.totalXpGained += amount;
        while (xp >= xpForNextLevel() && level < 100) {
            xp -= xpForNextLevel();
            level++;
        }
        if (level >= 100) {
            xp = 0;
        }
    }

    public int xpForNextLevel() {
        return 50 + level * 25;
    }

    public void reset() {
        this.level = 1;
        this.xp = 0;
        this.totalXpGained = 0;
    }

    public void copyFrom(LevelData other) {
        if (other == null) return;
        this.level = other.level;
        this.xp = other.xp;
        this.totalXpGained = other.totalXpGained;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("level", this.level);
        tag.putInt("xp", this.xp);
        tag.putInt("totalXpGained", this.totalXpGained);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        this.level = tag.getInt("level");
        this.xp = tag.getInt("xp");
        this.totalXpGained = tag.getInt("totalXpGained");
    }
}
