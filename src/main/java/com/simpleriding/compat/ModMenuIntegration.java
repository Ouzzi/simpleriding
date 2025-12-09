package com.simpleriding.compat;

import com.simpleriding.config.SimpleridingConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        // Erzeugt das GUI automatisch basierend auf deiner Config-Klasse
        return parent -> AutoConfig.getConfigScreen(SimpleridingConfig.class, parent).get();
    }
}