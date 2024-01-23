package com.ewoudje.lasagna.fabric.mixin;

import net.fabricmc.fabric.impl.resource.loader.ModResourcePackUtil;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ModResourcePackUtil.class)
public class MixinModResourcePackUtil {

    @Redirect(method = "loadDynamicRegistry", at =
            @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;openAllSelected()Ljava/util/List;"))
    private static List<PackResources> redirectPacks(PackRepository instance) {
        instance.setSelected(instance.getAvailableIds());
        return instance.openAllSelected();
    }

}
