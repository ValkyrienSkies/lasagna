package org.mashed.lasagna.networking

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.chunk.LevelChunk

sealed interface PacketTarget
sealed interface ToClientPacketTarget: PacketTarget
sealed interface ToServerPacketTarget: PacketTarget

object ServerPacketTarget: ToServerPacketTarget
object AllPlayersPacketTarget: ToClientPacketTarget

class PlayerPacketTarget(val serverPlayer: ServerPlayer): ToClientPacketTarget
class TrackingChunkPacketTarget(val chunk: LevelChunk): ToClientPacketTarget
class TrackingEntityPacketTarget(val entity: Entity): ToClientPacketTarget
class TrackingEntityAndSelfPacketTarget(val entity: Entity): ToClientPacketTarget