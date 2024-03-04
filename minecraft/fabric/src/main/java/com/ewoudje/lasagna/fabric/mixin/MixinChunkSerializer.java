package com.ewoudje.lasagna.fabric.mixin;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.ticks.LevelChunkTicks;
import com.ewoudje.lasagna.chunkstorage.ChunkSerializerHelper;
import com.ewoudje.lasagna.chunkstorage.ExtraSectionStorage;
import com.ewoudje.lasagna.chunkstorage.ExtraStorageSectionContainer;
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
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void writeExtraChunkData(ServerLevel level, ChunkAccess chunk, CallbackInfoReturnable<CompoundTag> cir,
                                             ChunkPos chunkPos, CompoundTag compoundTag, LevelChunkSection[] levelChunkSections,
                                             ListTag listTag, LevelLightEngine levelLightEngine,
                                             Registry registry, Codec codec, boolean bl,
                                             int i, int j, boolean bl2,
                                             DataLayer dataLayer, DataLayer dataLayer2,
                                             CompoundTag nbt,
                                             LevelChunkSection section) {
        if (chunk instanceof LevelChunk levelChunk) {
            ChunkSerializerHelper.INSTANCE.write(levelChunk, j, nbt);
        }
    }

    @Inject(
            method = "read",
            at = @At(value = "RETURN", ordinal = 0)
    )
    private static void readExtraChunkData(ServerLevel level,
                                           PoiManager poiManager,
                                           ChunkPos pos, CompoundTag tag,
                                           CallbackInfoReturnable<ProtoChunk> cir) {
        assert cir.getReturnValue() != null && cir.getReturnValue() instanceof ImposterProtoChunk;

        ImposterProtoChunk chunk = (ImposterProtoChunk) cir.getReturnValue();
        ListTag listTag = tag.getList("sections", 10);

        for (int index = 0; index < chunk.getSections().length; index++) {
            CompoundTag sectionNbt = listTag.getCompound(index);
            ChunkSerializerHelper.INSTANCE.read(chunk.getWrapped(), sectionNbt,
                    level.getSectionIndexFromSectionY(sectionNbt.getByte("Y")));
        }
    }
}
