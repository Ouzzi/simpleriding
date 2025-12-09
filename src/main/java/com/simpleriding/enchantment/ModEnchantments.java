package com.simpleriding.enchantment;

import com.simpleriding.Simpleriding;
import com.simpleriding.util.ModTags;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEnchantments {
    // Keys
    public static final RegistryKey<Enchantment> TAILWIND = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(Simpleriding.MOD_ID, "tailwind"));
    public static final RegistryKey<Enchantment> LEAPING = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(Simpleriding.MOD_ID, "leaping"));

    public static void bootstrap(Registerable<Enchantment> registerable) {
        var items = registerable.getRegistryLookup(RegistryKeys.ITEM);

        var enchantmentsLookup = registerable.getRegistryLookup(RegistryKeys.ENCHANTMENT);

        // 16. TAILWIND (Max Level III, Common) [SADDLE]
        register(registerable, TAILWIND, Enchantment.builder(
                Enchantment.definition(
                        items.getOrThrow(ModTags.Items.SADDLE_ENCHANTABLE), // Ziel: Saddle
                        items.getOrThrow(ModTags.Items.SADDLE_ENCHANTABLE),
                        2, // Weight (Rare)
                        3, // Max Level
                        Enchantment.leveledCost(15, 10),
                        Enchantment.leveledCost(65, 10),
                        4,
                        AttributeModifierSlot.ARMOR
                )));

        // 17. LEAPING (Max Level III, Common) [SADDLE]
        register(registerable, LEAPING, Enchantment.builder(
                Enchantment.definition(
                        items.getOrThrow(ModTags.Items.HORSE_ARMOR_ENCHANTABLE), // Ziel: Saddle
                        items.getOrThrow(ModTags.Items.HORSE_ARMOR_ENCHANTABLE),
                        3, // Weight (Uncommon)
                        3, // Max Level
                        Enchantment.leveledCost(20, 10),
                        Enchantment.leveledCost(70, 10),
                        4,
                        AttributeModifierSlot.ARMOR
                )));

}

    private static void register(Registerable<Enchantment> registry, RegistryKey<Enchantment> key, Enchantment.Builder builder) {
        registry.register(key, builder.build(key.getValue()));
    }
}