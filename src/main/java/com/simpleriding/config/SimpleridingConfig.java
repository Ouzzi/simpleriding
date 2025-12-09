package com.simpleriding.config;

import com.simpleriding.Simpleriding;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = Simpleriding.MOD_ID)
public class SimpleridingConfig implements ConfigData {


    @ConfigEntry.Gui.CollapsibleObject
    public WorldGen worldGen = new WorldGen();

    @ConfigEntry.Gui.CollapsibleObject
    public EnchantmentBalancing enchantments = new EnchantmentBalancing();


    public static class WorldGen {
        @ConfigEntry.Gui.Tooltip
        public boolean enableVillagerTrades = true;

        @ConfigEntry.Gui.Tooltip
        public boolean enableLootTableChanges = true;
    }

    public static class EnchantmentBalancing {
        @ConfigEntry.Gui.CollapsibleObject
        public SwiftRide swiftRide = new SwiftRide();

        @ConfigEntry.Gui.CollapsibleObject
        public HorseJump horseJump = new HorseJump();

        public static class SwiftRide {
            @ConfigEntry.Gui.Tooltip
            public float ghastSpeedMultiplier = 0.85f; // 85% pro Level

            @ConfigEntry.Gui.Tooltip
            public float horseSpeedMultiplier = 0.3f; // 30% pro Level

            @ConfigEntry.Gui.Tooltip
            public float otherSpeedMultiplier = 0.2f; // 20% pro Level
        }

        public static class HorseJump {
            @ConfigEntry.Gui.Tooltip
            public float jumpStrengthMultiplier = 0.20f; // +20% pro Level
        }
    }
}