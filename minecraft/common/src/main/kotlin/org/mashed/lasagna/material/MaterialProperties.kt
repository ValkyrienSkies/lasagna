package org.mashed.lasagna.material

data class MaterialProperties(
    val density: Float,
    val conductivity: Double, // 1/R
    val thermalExpansion: Double,
    val ignitionPoint: Float, // Float.INFINITY if not flammable
    val meltingPoint: Float,
    val boilingPoint: Float,
    val tensileStrength: Float, // stress / tensileStrength if tensile stress
    val compressiveStrength: Float, // stress / compressiveStrength if compressive stress

    val gasTightness: Float, // 0.0 to 1.0
    val liquidTightness: Float, // 0.0 to 1.0

    //stress < hardness fine
    //stress > hardness but stress - hardness < elestacity forms back to original shape
    //stress - hardness > elestacity permament deformation
    //stress - hardness > fractureToughnes fracture
    val hardness: Float,
    val elasticity: Float,
    val fractureToughness: Float,
)