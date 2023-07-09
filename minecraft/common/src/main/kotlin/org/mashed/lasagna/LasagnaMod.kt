package org.mashed.lasagna

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import net.minecraft.client.multiplayer.ClientSuggestionProvider
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.TextComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.chunk.storage.SectionStorage
import org.mashed.lasagna.api.events.RegistryEvents
import org.mashed.lasagna.chunkstorage.ExtraSectionStorage
import org.mashed.lasagna.scaleblocks.ScaleBlocksView
import org.mashed.lasagna.scaleblocks.ScaledSectionStorage
import org.mashed.lasagna.chunkstorage.ExtraStorageSectionContainer
import org.mashed.lasagna.chunkstorage.setSectionStorage
import org.mashed.lasagna.networking.*

object LasagnaMod {
    const val MOD_ID = "lasagna"
    const val VERSION = "DEV"
    val test_scale = "test_scale".resource

    @JvmStatic
    fun init() {
        ExtraSectionStorage.register(test_scale, ScaledSectionStorage::readNbt)

        LasagnaNetworking.packetServer { p: Ping, serverPlayer: ServerPlayer ->
            LasagnaNetworking.send(PlayerPacketTarget(serverPlayer), Ping())
        }

        LasagnaNetworking.packetClient { p: Ping, mc ->
            mc.gui.chat.addMessage(TextComponent("Pong!"))
        }

        defineSerialization("ping".resource) { p: Ping, buf: FriendlyByteBuf ->
            buf.writeUtf("ping")
        } decode { buf ->
            val message = buf.readUtf()
            if (message != "ping") throw IllegalArgumentException("Invalid ping message")
            Ping()
        }
    }

    @JvmStatic
    fun initClient() {

    }

    @JvmStatic
    fun registerClientCommands(dispatcher: CommandDispatcher<SharedSuggestionProvider>) {
        dispatcher.register(
            literal<SharedSuggestionProvider>("clasagna").then(
                literal<SharedSuggestionProvider>("version").executes {
                    Minecraft.gui.chat.addMessage(TextComponent("Lasagna version $VERSION"))
                    1
                }).then(literal<SharedSuggestionProvider>("debug").then(argument<SharedSuggestionProvider, Int>("pos", IntegerArgumentType.integer()).executes { ctx ->
                    try {
                        (Minecraft.level?.getChunk(BlockPos.ZERO)?.getSection(0) as ExtraStorageSectionContainer?)?.let {
                            var storage = it.getSectionStorage(test_scale);
                            if (storage == null)
                                it.setSectionStorage(ScaledSectionStorage(ScaleBlocksView(3, 0.05), "test".resource)
                                    .apply { storage = this })

                            val cord = ctx.getArgument("pos", Int::class.java)

                            (storage as ScaledSectionStorage).setBlockState(cord, 12, 0, Blocks.GOLD_BLOCK.defaultBlockState())

                            Minecraft.gui.chat.addMessage(TextComponent("Lasagna debug"))
                        }
                    } catch (e: Exception) {
                        Minecraft.gui.chat.addMessage(TextComponent("Lasagna debug failed: ${e.message}"))
                    }
                    1
                })).then(literal<SharedSuggestionProvider>("ping").executes {
                    LasagnaNetworking.send(ServerPacketTarget, Ping())
                    1
                }))

        RegistryEvents.onClientCommandRegister.invoke(dispatcher)
    }

    @JvmStatic
    fun registerServerCommands(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            literal<CommandSourceStack>("lasagna").then(
                literal<CommandSourceStack>("debug").executes { ctx ->
                    try {
                        (ctx.source.level.getChunk(0, 0).getSection(0) as ExtraStorageSectionContainer?)?.let {
                            if (it.getSectionStorage(test_scale) == null)
                                it.setSectionStorage(
                                    ScaledSectionStorage(ScaleBlocksView(3, 0.05), test_scale)
                                        .apply { setBlockState(0, 12, 0, Blocks.GOLD_BLOCK.defaultBlockState()) })
                        }
                        ctx.source.sendSuccess(TextComponent("Lasagna debug"), false)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        ctx.source.sendFailure(TextComponent("Lasagna debug failed: ${e.message}"))
                    }
                    1
                })
        )

        RegistryEvents.onServerCommandRegister.invoke(dispatcher)
    }

    internal val String.resource: ResourceLocation get() = ResourceLocation(MOD_ID, this)

    private class Ping
}