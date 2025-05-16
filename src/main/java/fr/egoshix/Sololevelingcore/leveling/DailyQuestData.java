package fr.egoshix.Sololevelingcore.leveling;

import net.minecraft.nbt.CompoundTag;

import java.util.Random;

public class DailyQuestData {
    public enum QuestType { KILL_MOBS, GAIN_XP, REACH_LEVEL }

    private QuestType type = QuestType.KILL_MOBS;
    private int target = 10;
    private int progress = 0;
    private boolean completed = false;
    private long lastGenerated = 0; // Timestamp en millisecondes (System.currentTimeMillis())

    public QuestType getType() { return type; }
    public int getTarget() { return target; }
    public int getProgress() { return progress; }
    public boolean isCompleted() { return completed; }
    public long getLastGenerated() { return lastGenerated; }

    public void addProgress(int amount) {
        if (!completed) {
            progress += amount;
            if (progress >= target) {
                progress = target;
                completed = true;
            }
        }
    }
    public void copyFrom(DailyQuestData other) {
        this.type = other.type;
        this.progress = other.progress;
        this.target = other.target;
        this.completed = other.completed;
        this.lastGenerated = other.lastGenerated;
        // Ajoute d'autres champs si tu en ajoutes plus tard
    }

    public void generateNewQuest(int playerLevel) {
        Random rand = new Random();
        completed = false;
        progress = 0;
        lastGenerated = System.currentTimeMillis();
        int t = rand.nextInt(3);
        if (t == 0) {
            type = QuestType.KILL_MOBS;
            target = 10 + rand.nextInt(11); // 10 à 20 monstres à tuer
        } else if (t == 1) {
            type = QuestType.GAIN_XP;
            target = 100 + rand.nextInt(201); // 100 à 300 XP à gagner
        } else {
            type = QuestType.REACH_LEVEL;
            target = playerLevel + 1 + rand.nextInt(3); // Atteindre 1 à 3 niveaux de plus
        }
    }

    public CompoundTag saveNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("type", type.ordinal());
        tag.putInt("target", target);
        tag.putInt("progress", progress);
        tag.putBoolean("completed", completed);
        tag.putLong("lastGenerated", lastGenerated);
        return tag;
    }

    public void loadNBT(CompoundTag tag) {
        type = QuestType.values()[tag.getInt("type")];
        target = tag.getInt("target");
        progress = tag.getInt("progress");
        completed = tag.getBoolean("completed");
        if (tag.contains("lastGenerated")) {
            lastGenerated = tag.getLong("lastGenerated");
        }
    }
}
