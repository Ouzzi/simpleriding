package com.simpleriding.datagen;


import com.simpleriding.Simpleriding;
import com.simpleriding.enchantment.ModEnchantments;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import net.minecraft.village.VillagerProfession;

import java.util.List;

public class ModTradeOffers {
    public record WeightedEnchantment(RegistryKey<Enchantment> key, int level, int weight) {}

    //register trade offers here
    public static void registerModTradeOffers() {
        Simpleriding.LOGGER.info("Registering Custom Trade Offers for " + Simpleriding.MOD_ID);
        registerVillagerTrades();
    }


    public static void registerVillagerTrades() {
        if (Simpleriding.getConfig().worldGen.enableVillagerTrades) {
            TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 2, factories -> {
                List<WeightedEnchantment> advancedPool = List.of(
                        new WeightedEnchantment(ModEnchantments.TAILWIND, 1, 30),
                        new WeightedEnchantment(ModEnchantments.LEAPING, 1, 20)
                );
                factories.add((world, entity, random) -> new TradeOffer(new TradedItem(Items.EMERALD, 10 + random.nextInt(20)), createRandomEnchantedBook(entity, random, advancedPool, 0), 2, 25, 0.5f));
            });
            TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 3, factories -> {
                List<WeightedEnchantment> masterPool = List.of(
                        new WeightedEnchantment(ModEnchantments.TAILWIND, 2, 20),
                        new WeightedEnchantment(ModEnchantments.LEAPING, 2, 30)
                );
                factories.add((world, entity, random) -> new TradeOffer(new TradedItem(Items.EMERALD, 15 + random.nextInt(20)), createRandomEnchantedBook(entity, random, masterPool, 15), 1, 50, 1.0f));
            });
            TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 4, factories -> {
                List<WeightedEnchantment> masterPool = List.of(
                        new WeightedEnchantment(ModEnchantments.TAILWIND, 3, 20),
                        new WeightedEnchantment(ModEnchantments.LEAPING, 3, 20)

                );
                factories.add((world, entity, random) -> new TradeOffer(new TradedItem(Items.EMERALD, 15 + random.nextInt(35)), createRandomEnchantedBook(entity, random, masterPool, 35), 1, 80, 1.0f));
            });
        }
    }

    /**
     * Hilfsmethode: Zieht eine zufällige Verzauberung basierend auf dem Gewicht.
     */
    private static WeightedEnchantment pickWeighted(List<WeightedEnchantment> pool, Random random) {
        int totalWeight = 0;
        for (WeightedEnchantment e : pool) totalWeight += e.weight();
        if (totalWeight == 0) {
            return null;
        }
        int pick = random.nextInt(totalWeight);
        int currentWeight = 0;
        for (WeightedEnchantment e : pool) {
            currentWeight += e.weight();
            if (pick < currentWeight) return e;
        }
        return pool.get(0);
    }

    private static void addEnchantmentToBuilder(Entity entity, ItemEnchantmentsComponent.Builder builder, WeightedEnchantment selection) {
        RegistryEntry<Enchantment> enchantmentEntry = entity.getRegistryManager()
                .getOrThrow(RegistryKeys.ENCHANTMENT)
                .getOrThrow(selection.key());

        builder.add(enchantmentEntry, selection.level());
    }

    private static ItemStack createRandomEnchantedBook(Entity entity, Random random, List<WeightedEnchantment> pool, int chanceForSecond) {
        ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
        ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
        WeightedEnchantment firstPick = pickWeighted(pool, random);

        // Safety check falls pickWeighted null returned (z.B. leerer Pool)
        if (firstPick != null) {
            addEnchantmentToBuilder(entity, builder, firstPick);

            // 2. Zweite Verzauberung
            if (pool.size() > 1 && random.nextInt(100) < chanceForSecond) {
                WeightedEnchantment secondPick = pickWeighted(pool, random);
                int attempts = 0;
                while (secondPick != null && secondPick.key().equals(firstPick.key()) && attempts < 10) {
                    secondPick = pickWeighted(pool, random);
                    attempts++;
                }
                if (secondPick != null && !secondPick.key().equals(firstPick.key())) {
                    addEnchantmentToBuilder(entity, builder, secondPick);
                }
            }
        }

        // WICHTIG: Bei Büchern nutzen wir STORED_ENCHANTMENTS
        stack.set(DataComponentTypes.STORED_ENCHANTMENTS, builder.build());
        return stack;
    }
}