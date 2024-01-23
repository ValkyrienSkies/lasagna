package com.ewoudje.lasagna.mixin.section_storage;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ewoudje.lasagna.api.Identifiable;
import com.ewoudje.lasagna.chunkstorage.ExtraSectionStorage;
import com.ewoudje.lasagna.chunkstorage.ExtraStorageSectionContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.*;

@Mixin(LevelChunkSection.class)
public abstract class MixinLevelChunkSection implements ExtraStorageSectionContainer {

    @Unique
    private Map<ResourceLocation, ExtraSectionStorage> storage = new HashMap<>();

    @Unique
    private Map<Class<? extends ExtraSectionStorage>, Set<ExtraSectionStorage>> storageByType = new HashMap<>();

    @Nullable
    @Override
    public ExtraSectionStorage getSectionStorage(@NotNull ResourceLocation id) {
        return this.storage.get(id);
    }

    @Override
    public void setSectionStorage(@NotNull ResourceLocation id, ExtraSectionStorage storage) {
        this.storage.put(id, storage);
        this.storageByType.computeIfAbsent(storage.getClass(), (k) -> new HashSet<>()).add(storage);
    }

    @NotNull
    @Override
    public <T extends ExtraSectionStorage> Set<T> getSectionsOfType(@NotNull Class<T> type) {
        return storageByType.get(type) == null ? Collections.emptySet() : (Set<T>) storageByType.get(type);
    }

    @Nullable
    @Override
    public ExtraSectionStorage removeSectionStorage(@NotNull ResourceLocation id) {
        ExtraSectionStorage remove = this.storage.remove(id);

        if (remove != null) {
            this.storageByType.get(remove.getClass()).remove(remove);
        }

        return remove;
    }

    @Override
    public Set<Map.Entry<ResourceLocation, ExtraSectionStorage>> getStorage() {
        return this.storage.entrySet();
    }
}
