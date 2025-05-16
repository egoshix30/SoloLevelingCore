package fr.egoshix.Sololevelingcore.leveling;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LevelProvider implements ICapabilitySerializable<CompoundTag>, ICapabilityProvider {
    private final LevelData data = new LevelData();
    private final PlayerStats stats = new PlayerStats();
    private final DailyQuestData dailyQuest = new DailyQuestData();

    public LevelData getData() { return data; }
    public PlayerStats getStats() { return stats; }
    public DailyQuestData getDailyQuest() { return dailyQuest; }

    private final LazyOptional<LevelProvider> holder = LazyOptional.of(() -> this);

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable net.minecraft.core.Direction side) {
        if (cap == LevelCapability.LEVEL) {
            return (LazyOptional<T>) holder;
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("levelData", data.saveNBT());
        tag.put("playerStats", stats.saveNBT());
        tag.put("dailyQuest", dailyQuest.saveNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("levelData")) data.loadNBT(nbt.getCompound("levelData"));
        if (nbt.contains("playerStats")) stats.loadNBT(nbt.getCompound("playerStats"));
        if (nbt.contains("dailyQuest")) dailyQuest.loadNBT(nbt.getCompound("dailyQuest"));
    }

    public static LevelProvider getSafe(Player player) {
        return LevelCapability.get(player);
    }
}
