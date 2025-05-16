package fr.egoshix.Sololevelingcore.leveling;

public class LevelData {
    private int level;
    private int xp;
    private int totalXpGained;

    public LevelData() {
        level = 1;
        xp = 0;
        totalXpGained = 0;
    }

    public int getLevel() { return level; }
    public int getXp() { return xp; }
    public int getTotalXpGained() { return totalXpGained; }

    public void setLevel(int level) { this.level = level; }
    public void setXp(int xp) { this.xp = xp; }
    public void setTotalXpGained(int totalXpGained) { this.totalXpGained = totalXpGained; }

    public void addXp(int amount) {
        xp += amount;
        totalXpGained += amount;
        while (xp >= xpForNextLevel() && level < 100) {
            xp -= xpForNextLevel();
            level++;
        }
        if (level >= 100) {
            level = 100;
            xp = 0;
        }
    }
    public void copyFrom(LevelData other) {
        this.level = other.level;
        this.xp = other.xp;
        // Ajoute d'autres champs à copier si tu en as
    }

    public void reset() {
        level = 1;
        xp = 0;
        totalXpGained = 0;
    }

    public int xpForNextLevel() {
        return 100 + (level - 1) * 10;
    }
}
