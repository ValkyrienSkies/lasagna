package org.mashed.lasagna.mixin.scaleblocks;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.mashed.lasagna.scaleblocks.ScaledSection;
import org.mashed.lasagna.scaleblocks.ScaledSectionContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ChunkSerializer.class)
public class MixinChunkSerializer {


    @Inject(
            method = "write",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;getStates()Lnet/minecraft/world/level/chunk/PalettedContainer;"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private static void writeScaledChunkData(ServerLevel level, ChunkAccess chunk, CallbackInfoReturnable<CompoundTag> cir,
                                     ChunkPos chunkPos, CompoundTag compoundTag, LevelChunkSection[] levelChunkSections,
                                     ListTag listTag, LevelLightEngine levelLightEngine,
                                     Registry registry, Codec codec, boolean bl,
                                     int i, int j, boolean bl2,
                                     DataLayer dataLayer, DataLayer dataLayer2,
                                     CompoundTag nbt,
                                     LevelChunkSection section) {
        var scaled = ((ScaledSectionContainer) section).getScaledSection();
        if (scaled != null)
            scaled.writeNbt(nbt);
    }

    @Inject(
            method = "read",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/village/poi/PoiManager;checkConsistencyWithBlocks(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/chunk/LevelChunkSection;)V", shift = At.Shift.BEFORE),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void readScaledChunkData(ServerLevel level, PoiManager poiManager,
                                            ChunkPos pos, CompoundTag tag, CallbackInfoReturnable<ProtoChunk> cir,
                                            UpgradeData upgradeData, boolean bl, ListTag listTag, int i,
                                            LevelChunkSection[] levelChunkSections, boolean bl2,
                                            ChunkSource chunkSource, LevelLightEngine levelLightEngine,
                                            Registry registry, Codec codec, int j, CompoundTag sectionNbt, int k, int index,
                                            PalettedContainer states, PalettedContainer biomes) {
        var section = levelChunkSections[index];
        ((ScaledSectionContainer) section).setScaledSection(ScaledSection.readNbt(sectionNbt));
    }

}
