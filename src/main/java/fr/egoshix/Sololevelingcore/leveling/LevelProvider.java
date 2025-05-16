package fr.egoshix.Sololevelingcore.leveling;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LevelProvider implements ICapabilitySerializable<CompoundTag> {

    public static Capability<LevelProvider> LEVEL = CapabilityManager.get(new CapabilityToken<>() {});

    private final LevelData data = new LevelData();
    private final PlayerStats stats = new PlayerStats();
    private final DailyQuestData dailyQuest = new DailyQuestData();

    private final LazyOptional<LevelProvider> holder = LazyOptional.of(() -> this);

    public LevelData getData() {
        return data;
    }

    public PlayerStats getStats() {
        return stats;
    }

    public DailyQuestData getDailyQuest() {
        return dailyQuest;
    }

    public static LevelProvider getSafe(Player player) {
        if (player == null) return null;
        return player.getCapability(LEVEL).orElse(null);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("level", data.getLevel());
        tag.putInt("xp", data.getXp());
        tag.putInt("totalXpGained", data.getTotalXpGained());
        tag.put("playerStats", stats.saveNBT());
        tag.put("dailyQuest", dailyQuest.saveNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt == null) return;
        data.setLevel(nbt.getInt("level"));
        data.setXp(nbt.getInt("xp"));
        data.setTotalXpGained(nbt.getInt("totalXpGained"));
        if (nbt.contains("playerStats")) stats.loadNBT(nbt.getCompound("playerStats"));
        if (nbt.contains("dailyQuest")) dailyQuest.loadNBT(nbt.getCompound("dailyQuest"));
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable net.minecraft.core.Direction side) {
        return LEVEL.orEmpty(cap, holder);
    }
}
