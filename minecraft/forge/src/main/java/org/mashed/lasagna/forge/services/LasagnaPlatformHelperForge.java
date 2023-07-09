package org.mashed.lasagna.forge.services;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import net.minecraft.client.gui.screens.worldselection.WorldPreset;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.mashed.lasagna.forge.LasagnaModForge;
import org.mashed.lasagna.networking.*;
import org.mashed.lasagna.services.LasagnaPlatformHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class LasagnaPlatformHelperForge implements LasagnaPlatformHelper {
    private static final Set<Serialization<?>> registered = new HashSet<>();

    @NotNull
    @Override
    public CreativeModeTab createCreativeTab(@NotNull ResourceLocation id, @NotNull Function0<ItemStack> stack) {
        return new CreativeModeTab(id.toString()) {
            @Override
            public ItemStack makeIcon() {
                return stack.invoke();
            }

            @Override
            public Component getDisplayName() {
                return new TranslatableComponent("itemGroup." + String.format("%s.%s", id.getNamespace(), id.getPath()));
            }
        };
    }

    @OnlyIn(Dist.CLIENT)
    @NotNull
    @Override
    public ResourceKey<Registry<WorldPreset>> worldPresetsRegistry() {
        return (ResourceKey) ForgeRegistries.Keys.WORLD_TYPES;
    }

    @OnlyIn(Dist.CLIENT)
    @NotNull
    @Override
    public ResourceKey<Registry<DimensionSpecialEffects>> dimensionEffectsRegistry() {
        return LasagnaModForge.DIMENSION_EFFECTS_REGISTRY;
    }

    @Override
    public void registerDataListener(@NotNull ResourceLocation id, @NotNull PreparableReloadListener listener) {
        LasagnaModForge.addReloadListener(listener);
    }

    @Override
    public <T> void sendToClient(@NotNull Serialization<T> serialization, @NotNull ToClientPacketTarget target, T data) {
        checkSerialization(serialization);

        PacketDistributor.PacketTarget forgeTarget;
        if (target == AllPlayersPacketTarget.INSTANCE) forgeTarget =
                PacketDistributor.ALL.noArg();
        else if (target instanceof PlayerPacketTarget) forgeTarget =
                PacketDistributor.PLAYER.with(() -> ((PlayerPacketTarget) target).getServerPlayer());
        else if (target instanceof TrackingChunkPacketTarget) forgeTarget =
                PacketDistributor.TRACKING_CHUNK.with(() -> ((TrackingChunkPacketTarget) target).getChunk());
        else if (target instanceof TrackingEntityPacketTarget) forgeTarget =
                PacketDistributor.TRACKING_ENTITY.with(() -> ((TrackingEntityPacketTarget) target).getEntity());
        else if (target instanceof TrackingEntityAndSelfPacketTarget) forgeTarget =
                PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> ((TrackingEntityAndSelfPacketTarget) target).getEntity());
        else throw new IllegalArgumentException("Unknown target " + target);

        LasagnaModForge.PACKETS.send(forgeTarget, data);
    }

    @Override
    public <T> void sendToServer(@NotNull Serialization<T> serialization, T data) {
        checkSerialization(serialization);

        LasagnaModForge.PACKETS.send(PacketDistributor.SERVER.noArg(), data);
    }

    private int nextId = 0;
    private <T> void checkSerialization(Serialization<T> serialization) {
        if (!registered.contains(serialization)) {
            registered.add(serialization);
            var encode = serialization.getEncode();
            var decode = serialization.getDecode();
            var onRecieveClient = LasagnaNetworking.INSTANCE.onPacketRecievedClient(serialization.getPacketClass());
            var onRecieveServer = LasagnaNetworking.INSTANCE.onPacketRecievedServer(serialization.getPacketClass());

            LasagnaModForge.PACKETS.<T>registerMessage(nextId++, serialization.getPacketClass(), encode::invoke, decode::invoke, (data, ctx) -> {
                if (ctx.get().getDirection().getReceptionSide().isClient()) {
                    onRecieveClient.invoke(data, (call) -> {
                        ctx.get().enqueueWork(call::invoke);
                        return Unit.INSTANCE;
                    });
                } else {
                    onRecieveServer.invoke(data, ctx.get().getSender(), (call) -> {
                        ctx.get().enqueueWork(call::invoke);
                        return Unit.INSTANCE;
                    });
                }
            });
        }
    }
}
