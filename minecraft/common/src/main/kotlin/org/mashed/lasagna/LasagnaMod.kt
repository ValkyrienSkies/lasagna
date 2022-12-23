package org.mashed.lasagna

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import net.minecraft.client.multiplayer.ClientSuggestionProvider
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.coordinates.BlockPosArgument
import net.minecraft.commands.arguments.coordinates.Coordinates
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.TextComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Blocks
import org.mashed.lasagna.scaleblocks.ScaleBlocksView
import org.mashed.lasagna.scaleblocks.ScaledSection
import org.mashed.lasagna.scaleblocks.ScaledSectionContainer

object LasagnaMod {
    const val MOD_ID = "lasagna"
    const val VERSION = "DEV"

    @JvmStatic
    fun init() {

    }

    @JvmStatic
    fun initClient() {

    }

    @JvmStatic
    fun registerClientCommands(dispatcher: CommandDispatcher<ClientSuggestionProvider>) {
        dispatcher.register(
            literal<ClientSuggestionProvider>("clasagna").then(
                literal<ClientSuggestionProvider>("version").executes {
                    Minecraft.gui.chat.addMessage(TextComponent("Lasagna version $VERSION"))
                    1
                }).then(literal<ClientSuggestionProvider>("debug").then(argument<ClientSuggestionProvider, Int>("pos", IntegerArgumentType.integer()).executes { ctx ->
                    try {
                        (Minecraft.level?.getChunk(BlockPos.ZERO)?.getSection(0) as ScaledSectionContainer?)?.let {
                            if (it.scaledSection == null)
                                it.scaledSection = ScaledSection(ScaleBlocksView(3, 0.05))

                            val cord = ctx.getArgument("pos", Int::class.java)

                            it.scaledSection!!.setBlockState(cord, 12, 0, Blocks.GOLD_BLOCK.defaultBlockState())

                            Minecraft.gui.chat.addMessage(TextComponent("Lasagna debug"))
                        }
                    } catch (e: Exception) {
                        Minecraft.gui.chat.addMessage(TextComponent("Lasagna debug failed: ${e.message}"))
                    }
                    1
                }))
        )
    }

    @JvmStatic
    fun registerServerCommands(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            literal<CommandSourceStack>("lasagna").then(
                literal<CommandSourceStack>("debug").executes {
                    try {
                        (it.source.level.getChunk(0, 0).getSection(0) as ScaledSectionContainer?)?.let {
                            if (it.scaledSection == null)
                                it.scaledSection = ScaledSection(ScaleBlocksView(3, 0.05))
                                    .apply { setBlockState(0, 12, 0, Blocks.GOLD_BLOCK.defaultBlockState()) }
                        }
                        it.source.sendSuccess(TextComponent("Lasagna debug"), false)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        it.source.sendFailure(TextComponent("Lasagna debug failed: ${e.message}"))
                    }
                    1
                })
        )
    }

    val String.resource: ResourceLocation get() = ResourceLocation(MOD_ID, this)
}