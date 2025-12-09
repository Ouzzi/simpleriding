package com.simpleriding.util;

import com.simpleriding.Simpleriding;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {

    public static class Items {
        public static final TagKey<Item> SADDLE_ENCHANTABLE = createTag("saddle_enchantable");
        public static final TagKey<Item> HORSE_ARMOR_ENCHANTABLE = createTag("horse_armor_enchantable");


        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(Simpleriding.MOD_ID, name));
        }
    }
}
