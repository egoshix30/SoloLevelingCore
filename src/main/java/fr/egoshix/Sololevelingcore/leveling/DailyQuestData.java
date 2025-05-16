package fr.egoshix.Sololevelingcore.leveling;

import net.minecraft.nbt.CompoundTag;

public class DailyQuestData {
    public enum QuestType { KILL_MOBS, GAIN_XP, REACH_LEVEL, NONE }

    public QuestType type = QuestType.NONE;
    public int progress = 0;
    public int target = 0;
    public boolean completed = false;
    public long lastGenerated = 0L;

    public QuestType getType() { return type; }
    public int getProgress() { return progress; }
    public int getTarget() { return target; }
    public boolean isCompleted() { return completed; }
    public long getLastGenerated() { return lastGenerated; }

    public void addProgress(int amount) {
        if (!completed) {
            this.progress += amount;
            if (this.progress >= target) {
                this.progress = target;
                this.completed = true;
            }
        }
    }

    public void generateNewQuest(int playerLevel) {
        this.type = QuestType.values()[1 + (int) (Math.random() * 3) % 3];
        this.progress = 0;
        switch (type) {
            case KILL_MOBS -> this.target = 5 + (int)(Math.random() * 6);
            case GAIN_XP -> this.target = 100 + (int)(Math.random() * 151);
            case REACH_LEVEL -> this.target = Math.max(2, playerLevel + 1 + (int)(Math.random() * 3));
            default -> this.target = 0;
        }
        this.completed = false;
        this.lastGenerated = System.currentTimeMillis();
    }

    public void reset() {
        this.type = QuestType.NONE;
        this.progress = 0;
        this.target = 0;
        this.completed = false;
        this.lastGenerated = 0L;
    }

    public void copyFrom(DailyQuestData other) {
        if (other == null) return;
        this.type = other.type;
        this.progress = other.progress;
        this.target = other.target;
        this.completed = other.completed;
        this.lastGenerated = other.lastGenerated;
    }

    public CompoundTag saveNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("type", this.type == null ? -1 : this.type.ordinal());
        tag.putInt("progress", this.progress);
        tag.putInt("target", this.target);
        tag.putBoolean("completed", this.completed);
        tag.putLong("lastGenerated", this.lastGenerated);
        return tag;
    }

    public void loadNBT(CompoundTag tag) {
        int typeIndex = tag.getInt("type");
        this.type = (typeIndex < 0 || typeIndex >= QuestType.values().length) ? QuestType.NONE : QuestType.values()[typeIndex];
        this.progress = tag.getInt("progress");
        this.target = tag.getInt("target");
        this.completed = tag.getBoolean("completed");
        this.lastGenerated = tag.getLong("lastGenerated");
    }
}
