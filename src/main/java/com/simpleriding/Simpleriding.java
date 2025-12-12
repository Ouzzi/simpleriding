package com.simpleriding;

import com.simpleriding.component.ModDataComponentTypes;
import com.simpleriding.config.SimpleridingConfig;
import com.simpleriding.datagen.ModTradeOffers;
import com.simpleriding.enchantment.ModEnchantmentEffects;
import com.simpleriding.items.ModItemGroups;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Simpleriding implements ModInitializer {
	public static final String MOD_ID = "simpleriding";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static SimpleridingConfig CONFIG;

	@Override
	public void onInitialize() {
        LOGGER.info("Simpleriding mod initialized!");


        AutoConfig.register(SimpleridingConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(SimpleridingConfig.class).getConfig();


        ModItemGroups.registerItemGroups();


        ModTradeOffers.registerModTradeOffers();
        ModDataComponentTypes.registerDataComponentTypes();
        ModEnchantmentEffects.registerEnchantmentEffects();
    }

    public  static SimpleridingConfig getConfig() {
        return CONFIG;
    }
}

// TODO:
// -