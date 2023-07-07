package org.mashed.lasagna.networking

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.chunk.LevelChunk

sealed interface PacketTarget
sealed interface ToClientPacketTarget: PacketTarget
sealed interface ToServerPacketTarget: PacketTarget

object ServerTarget: ToServerPacketTarget
object AllPlayers: ToClientPacketTarget

class PlayerTarget(val serverPlayer: ServerPlayer): ToClientPacketTarget
class TrackingChunksTarget(val chunk: LevelChunk): ToClientPacketTarget
class TrackingEntityTarget(val entity: Entity): ToClientPacketTarget
class TrackingEntityAndSelfTarget(val entity: Entity): ToClientPacketTarget