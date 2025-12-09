package com.simpleriding.items;

import com.simpleriding.Simpleriding;
import com.simpleriding.enchantment.ModEnchantments;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup RIDING_ITEMS_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(Simpleriding.MOD_ID, "riding_items"),
            FabricItemGroup.builder().icon(() -> new ItemStack(Items.SADDLE))
                    .displayName(Text.translatable("itemgroup.simpleriding.riding_items"))
                    .entries((displayContext, entries) -> {

                        // --- Enchanted Books ---
                        RegistryWrapper.WrapperLookup lookup = displayContext.lookup();
                        RegistryWrapper<Enchantment> enchantmentRegistry = lookup.getOrThrow(RegistryKeys.ENCHANTMENT);

                        // 5. Armor Utilities
                        addEnchant(entries, enchantmentRegistry, ModEnchantments.TAILWIND, 3);
                        addEnchant(entries, enchantmentRegistry, ModEnchantments.LEAPING, 3);

                    }).build());

    public static void registerItemGroups() {
        Simpleriding.LOGGER.info("Registering Item Groups for " + Simpleriding.MOD_ID);
    }

    /**
     * Helper method to add an Enchanted Book to the Creative Tab.
     * Checks if the enchantment exists in the registry wrapper before adding.
     *
     * @param entries The ItemGroup entries list
     * @param registry The Enchantment Registry Wrapper
     * @param key The Registry Key of the custom enchantment
     * @param level The level of the enchantment to display on the book
     */
    private static void addEnchant(ItemGroup.Entries entries, RegistryWrapper<Enchantment> registry, RegistryKey<Enchantment> key, int level) {
        registry.getOptional(key).ifPresent(enchantmentEntry -> {
            ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
            ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
            builder.add(enchantmentEntry, level);
            book.set(DataComponentTypes.STORED_ENCHANTMENTS, builder.build());
            entries.add(book);
        });
    }
}
