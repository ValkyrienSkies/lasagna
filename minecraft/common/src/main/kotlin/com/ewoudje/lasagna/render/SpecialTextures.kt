package me.crackhead.potato_battery.render

import com.mojang.blaze3d.systems.RenderSystem
import com.ewoudje.lasagna.LasagnaMod.resource


// Based of Create Mod by simibubi
enum class SpecialTextures(filename: String) {
    BLANK("blank.png"),
    CHECKERED("checkerboard.png"),
    THIN_CHECKERED("thin_checkerboard.png"),
    CUTOUT_CHECKERED("cutout_checkerboard.png"),
    HIGHLIGHT_CHECKERED("highlighted_checkerboard.png"),
    SELECTION("selection.png");

    val location = "textures/special/$filename".resource

    fun bind() {
        RenderSystem.setShaderTexture(0, location)
    }
}