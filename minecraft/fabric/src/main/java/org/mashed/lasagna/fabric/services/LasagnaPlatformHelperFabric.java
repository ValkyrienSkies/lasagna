package org.mashed.lasagna.fabric.services;

import com.mojang.serialization.Lifecycle;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.gui.screens.worldselection.WorldPreset;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.mashed.lasagna.LasagnaMod;
import org.mashed.lasagna.networking.*;
import org.mashed.lasagna.services.LasagnaPlatformHelper;
import org.jetbrains.annotations.NotNull;
import org.mashed.lasagna.services.LasagnaPlatformHelper;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static org.mashed.lasagna.fabric.LasagnaModFabricClient.*;

public class LasagnaPlatformHelperFabric implements LasagnaPlatformHelper {

    @NotNull
    @Override
    public CreativeModeTab createCreativeTab(@NotNull ResourceLocation id, @NotNull Function0<ItemStack> stack) {
        return FabricItemGroupBuilder.build(id, stack::invoke);
    }

    @Environment(EnvType.CLIENT)
    @NotNull
    @Override
    public ResourceKey<Registry<WorldPreset>> worldPresetsRegistry() {
        return WORLD_PRESETS_REGISTRY;
    }

    @Environment(EnvType.CLIENT)
    @NotNull
    @Override
    public ResourceKey<Registry<DimensionSpecialEffects>> dimensionEffectsRegistry() {
        return DIMENSION_EFFECTS_REGISTRY;
    }

    @Override
    public void registerDataListener(@NotNull ResourceLocation id, @NotNull PreparableReloadListener listener) {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public @NotNull CompletableFuture<Void> reload(
                    PreparationBarrier preparationBarrier,
                    ResourceManager resourceManager,
                    ProfilerFiller preparationsProfiler,
                    ProfilerFiller reloadProfiler,
                    Executor backgroundExecutor,
                    Executor gameExecutor
            ) {
                return listener.reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
            }

            @Override
            public ResourceLocation getFabricId() {
                return id;
            }
        });
    }

    @Override
    public <T> void sendToClient(@NotNull Serialization<T> serialization, @NotNull ToClientPacketTarget target, T data) {
        ResourceLocation id = serialization.getId();
        FriendlyByteBuf buf = PacketByteBufs.create();
        serialization.getEncode().invoke(data, buf);

        Collection<ServerPlayer> sendTo;

        if (target instanceof AllPlayersPacketTarget) sendTo =
                PlayerLookup.all(((AllPlayersPacketTarget) target).getServer());
        else if (target instanceof PlayerPacketTarget) sendTo=
                Collections.singleton(((PlayerPacketTarget) target).getServerPlayer());
        else if (target instanceof TrackingChunkPacketTarget) sendTo =
                PlayerLookup.tracking(
                        (ServerLevel) ((TrackingChunkPacketTarget) target).getChunk().getLevel(),
                        ((TrackingChunkPacketTarget) target).getChunk().getPos()
                );
        else if (target instanceof TrackingEntityPacketTarget) sendTo =
                PlayerLookup.tracking(((TrackingEntityPacketTarget) target).getEntity());
        else if (target instanceof TrackingEntityAndSelfPacketTarget) {
            sendTo = PlayerLookup.tracking(((TrackingEntityAndSelfPacketTarget) target).getEntity());

            if (((TrackingEntityAndSelfPacketTarget) target).getEntity() instanceof ServerPlayer)
                sendTo.add((ServerPlayer) ((TrackingEntityAndSelfPacketTarget) target).getEntity());
        }
        else throw new IllegalArgumentException("Unknown target " + target);

        for (ServerPlayer player : sendTo) {
            ServerPlayNetworking.send(player, id, buf);
        }
    }

    @Override
    public <T> void sendToServer(@NotNull Serialization<T> serialization, T data) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        serialization.getEncode().invoke(data, buf);
        ClientPlayNetworking.send(Objects.requireNonNull(serialization.getId()), buf);
    }

    @Override
    public <T> void setupServerPacketHandler(@NotNull ResourceLocation id, Serialization<T> serialization) {
        var decode = serialization.getDecode();
        var onRecieveServer = LasagnaNetworking.INSTANCE.onPacketRecievedServer(serialization.getPacketClass());
        ServerPlayNetworking.registerGlobalReceiver(id, (server, player, handler, buf, responseSender) -> {
            onRecieveServer.invoke(decode.invoke(buf), player, (run) -> {
                server.execute(run::invoke);
                return Unit.INSTANCE;
            });
        });
    }

    @Override
    public <T> void setupClientPacketHandler(@NotNull ResourceLocation id, @NotNull Serialization<T> serialization) {
        var decode = serialization.getDecode();
        var onRecieveClient = LasagnaNetworking.INSTANCE.onPacketRecievedClient(serialization.getPacketClass());
        ClientPlayNetworking.registerGlobalReceiver(id, (client, handler, buf, responseSender) -> {
            onRecieveClient.invoke(decode.invoke(buf), (run) -> {
                client.execute(run::invoke);
                return Unit.INSTANCE;
            });
        });
    }
}
