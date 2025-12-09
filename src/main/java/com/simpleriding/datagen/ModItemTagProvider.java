package com.simpleriding.datagen;


import com.simpleriding.util.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {

        valueLookupBuilder(ItemTags.ARMOR_ENCHANTABLE)
                .add(Items.LEATHER_HORSE_ARMOR)
                .add(Items.IRON_HORSE_ARMOR)
                .add(Items.GOLDEN_HORSE_ARMOR)
                .add(Items.DIAMOND_HORSE_ARMOR);

        valueLookupBuilder(ModTags.Items.SADDLE_ENCHANTABLE)
                .add(Items.SADDLE)
                .add(Items.BLACK_HARNESS)
                .add(Items.BROWN_HARNESS)
                .add(Items.WHITE_HARNESS)
                .add(Items.GRAY_HARNESS)
                .add(Items.LIGHT_GRAY_HARNESS)
                .add(Items.CYAN_HARNESS)
                .add(Items.PINK_HARNESS)
                .add(Items.RED_HARNESS)
                .add(Items.ORANGE_HARNESS)
                .add(Items.YELLOW_HARNESS)
                .add(Items.LIME_HARNESS)
                .add(Items.GREEN_HARNESS)
                .add(Items.MAGENTA_HARNESS)
                .add(Items.PURPLE_HARNESS)
                .add(Items.BLUE_HARNESS)
                .add(Items.LIGHT_BLUE_HARNESS);

        valueLookupBuilder(ModTags.Items.HORSE_ARMOR_ENCHANTABLE)
                .add(Items.LEATHER_HORSE_ARMOR)
                .add(Items.IRON_HORSE_ARMOR)
                .add(Items.GOLDEN_HORSE_ARMOR)
                .add(Items.DIAMOND_HORSE_ARMOR);
    }
}
