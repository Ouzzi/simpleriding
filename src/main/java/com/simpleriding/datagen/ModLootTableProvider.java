package com.simpleriding.datagen;


import com.simpleriding.Simpleriding;
import com.simpleriding.enchantment.ModEnchantments;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.SetComponentsLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

// List all Loot Table types:
// 1. STRONGHOLD LIBRARY CHEST: ()
// 2. END CITY: (), ()
// 4. ANCIENT CITY: (), ()
// 5. BASTION: (TAILWIND, LEAPING), ()
// 6. NETHER BRIDGE: (TAILWIND), ()
// 7. PILLAGER OUTPOST: (), ()
// 8. WOODLAND MANSION: (), ()
// 9. BURIED TREASURE: (), ()
// 10. SIMPLE DUNGEON: (), ()
// 11. SHIPWRECK TREASURE: (), ()
// 12. IGLOO: (), ()
// 13. ABANDONED MINESHAFT: (), ()
// 14. VAULT: (LEAPING, TAILWIND), ()

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        RegistryWrapper.Impl<Enchantment> impl = this.registries.getOrThrow(RegistryKeys.ENCHANTMENT);
    }

    public static void modifyLootTables() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registry) -> {

            if (!Simpleriding.getConfig().worldGen.enableLootTableChanges) {
                return;
            }

            var enchantments = registry.getOrThrow(RegistryKeys.ENCHANTMENT);



            // 5. BASTION (FUNNEL, BREAK THROUGH)
            // + GOLD SLEDGEHAMMER, CORES
            if (LootTables.BASTION_TREASURE_CHEST.equals(key) || LootTables.BASTION_OTHER_CHEST.equals(key)) {
                LootPool.Builder pool = LootPool.builder()
                        .rolls(UniformLootNumberProvider.create(0, 2))
                        .with(enchantedBook(ModEnchantments.TAILWIND, 1, enchantments, 10))
                        .with(enchantedBook(ModEnchantments.LEAPING, 1, enchantments, 5));
                tableBuilder.pool(pool);
            }

            // 6. NETHER BRIDGE (FUNNEL, BREAK THROUGH, STRIP_MINER, TAILWIND)
            if (LootTables.NETHER_BRIDGE_CHEST.equals(key)) {
                LootPool.Builder pool = LootPool.builder()
                        .rolls(UniformLootNumberProvider.create(0, 1))
                        .with(enchantedBook(ModEnchantments.TAILWIND, 1, enchantments, 10));
                tableBuilder.pool(pool);
            }

            // 14. VAULT (Trial Chambers)
            if (LootTables.TRIAL_CHAMBERS_REWARD_COMMON_CHEST.equals(key) || LootTables.TRIAL_CHAMBERS_REWARD_RARE_CHEST.equals(key)) {
                LootPool.Builder pool = LootPool.builder().rolls(UniformLootNumberProvider.create(0, 1))
                        .with(enchantedBook(ModEnchantments.LEAPING, 1, enchantments, 10))
                        .with(enchantedBook(ModEnchantments.TAILWIND, 1, enchantments, 10));
                tableBuilder.pool(pool);
            }
        });
    }

    /**
     * Helper Methode um ein Enchanted Book mit einem bestimmten Enchantment zu erstellen.
     */
    private static LeafEntry.Builder<?> enchantedBook(
            RegistryKey<Enchantment> enchantKey,
            int level,
            RegistryWrapper<Enchantment> registry,
            int weight) {

        ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
        builder.add(registry.getOrThrow(enchantKey), level);
        ItemEnchantmentsComponent component = builder.build();

        return ItemEntry.builder(Items.ENCHANTED_BOOK)
                .weight(weight)
                .apply(SetComponentsLootFunction.builder(DataComponentTypes.STORED_ENCHANTMENTS, component));
    }
}