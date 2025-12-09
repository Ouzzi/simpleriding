package com.simpleriding.enchantment;

import com.mojang.serialization.MapCodec;
import com.simpleriding.Simpleriding;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEnchantmentEffects {


    private static MapCodec<? extends EnchantmentEntityEffect> registerEntityEffect(String name,
                                                                                    MapCodec<? extends EnchantmentEntityEffect> codec) {
        return Registry.register(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, Identifier.of(Simpleriding.MOD_ID, name), codec);
    }

    public static void registerEnchantmentEffects() {
        Simpleriding.LOGGER.info("Registering Mod Enchantment Effects for " + Simpleriding.MOD_ID);
    }
}