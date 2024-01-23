package com.ewoudje.lasagna.mixin.section_storage;

import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.apache.commons.lang3.mutable.MutableObject;
import com.ewoudje.lasagna.chunkstorage.ExtraSectionDataPacket;
import com.ewoudje.lasagna.chunkstorage.ExtraStorageSectionContainer;
import com.ewoudje.lasagna.networking.LasagnaNetworking;
import com.ewoudje.lasagna.networking.PlayerPacketTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkMap.class)
public class MixinChunkMap {

    @Inject(method = "playerLoadedChunk", at = @At("TAIL"))
    void sendExtraSectionStorage(ServerPlayer player, MutableObject<ClientboundLevelChunkWithLightPacket> packetCache, LevelChunk chunk, CallbackInfo ci) {
        for (LevelChunkSection section : chunk.getSections()) {
            if (!((ExtraStorageSectionContainer) section).getStorage().isEmpty()) {
                LasagnaNetworking.send(
                        new PlayerPacketTarget(player),
                        ExtraSectionDataPacket.class,
                        new ExtraSectionDataPacket(chunk)
                );
                return;
            }
        }
    }

}
