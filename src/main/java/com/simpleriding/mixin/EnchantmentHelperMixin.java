package com.simpleriding.mixin;

import com.simpleriding.enchantment.ModEnchantments;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EnchantableComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Stream;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    // 1. Erlaubt, dass das Item überhaupt verzaubert werden darf (Amboss & Co)
    @Inject(method = "canHaveEnchantments", at = @At("HEAD"), cancellable = true)
    private static void allowHorseArmor(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (isHorseArmor(stack.getItem())) {
            cir.setReturnValue(true);
        }
    }

    // 2. Gaukeln dem Zaubertisch vor, dass das Item eine Enchantability von 15 hat.
    @Redirect(
            method = "generateEnchantments",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;get(Lnet/minecraft/component/ComponentType;)Ljava/lang/Object;"
            )
    )
    private static Object fakeEnchantability(ItemStack instance, ComponentType<?> type) {
        Object original = instance.get(type);
        if (type == DataComponentTypes.ENCHANTABLE && original == null) {
            if (isHorseArmor(instance.getItem())) {
                return new EnchantableComponent(15);
            }
        }
        return original;
    }

    // Dasselbe für calculateRequiredExperienceLevel
    @Redirect(
            method = "calculateRequiredExperienceLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;get(Lnet/minecraft/component/ComponentType;)Ljava/lang/Object;"
            )
    )
    private static Object fakeEnchantabilityForLevels(ItemStack instance, ComponentType<?> type) {
        Object original = instance.get(type);
        if (type == DataComponentTypes.ENCHANTABLE && original == null) {
            if (isHorseArmor(instance.getItem())) {
                return new EnchantableComponent(15);
            }
        }
        return original;
    }

    @Inject(method = "getPossibleEntries", at = @At("RETURN"))
    private static void filterHorseEnchantments(int level, ItemStack stack, Stream<RegistryEntry<Enchantment>> possibleEnchantments, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        if (!isHorseArmor(stack.getItem())) return;

        List<EnchantmentLevelEntry> list = cir.getReturnValue();

        // WHITELIST: Wir entfernen alles, was NICHT explizit erlaubt ist.
        // Das Tag 'FOOT_ARMOR_ENCHANTABLE' fügt zwar alle Stiefel-Enchants hinzu,
        // aber hier schmeißen wir Wasserläufer, Eisläufer, Seelenläufer etc. wieder raus.
        list.removeIf(entry -> {
            RegistryKey<Enchantment> key = entry.enchantment().getKey().orElse(null);
            if (key == null) return false;

            // Definition der erlaubten Verzauberungen
            boolean isAllowed =
                    key.equals(Enchantments.PROTECTION) ||
                    key.equals(Enchantments.BLAST_PROTECTION) ||
                    key.equals(Enchantments.FIRE_PROTECTION) ||
                    key.equals(Enchantments.PROJECTILE_PROTECTION) ||
                    key.equals(ModEnchantments.LEAPING) ||
                    key.equals(Enchantments.FEATHER_FALLING);

            // Hinweis: Falls du auch 'Haltbarkeit' (Unbreaking) oder 'Dornen' (Thorns) erlauben willst,
            // musst du sie hier ergänzen: || key.equals(Enchantments.UNBREAKING)

            // Wenn es NICHT erlaubt ist -> entfernen (return true)
            return !isAllowed;
        });
    }

    @Unique
    private static boolean isHorseArmor(Item item) {
        return item == Items.LEATHER_HORSE_ARMOR ||
                item == Items.IRON_HORSE_ARMOR ||
                item == Items.GOLDEN_HORSE_ARMOR ||
                item == Items.DIAMOND_HORSE_ARMOR;
    }
}