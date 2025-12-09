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
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Stream;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    // Speichert den gefundenen Feather Falling Eintrag temporär für den aktuellen Thread
    @Unique
    private static final ThreadLocal<RegistryEntry<Enchantment>> CAPTURED_FEATHER_FALLING = new ThreadLocal<>();

    // 1. Wir fangen den Stream der möglichen Verzauberungen am Anfang der Methode ab.
    //    Wir durchsuchen ihn nach Feather Falling und speichern es, falls vorhanden.
    //    Da Streams nur einmal gelesen werden können, wandeln wir ihn in eine Liste um und geben einen neuen Stream zurück.
    @ModifyVariable(method = "getPossibleEntries", at = @At("HEAD"), argsOnly = true)
    private static Stream<RegistryEntry<Enchantment>> captureFeatherFallingFromStream(Stream<RegistryEntry<Enchantment>> stream) {
        // Reset state
        CAPTURED_FEATHER_FALLING.remove();

        if (stream == null) return Stream.empty();

        // Stream in Liste umwandeln (Java 16+)
        List<RegistryEntry<Enchantment>> list = stream.toList();

        // Suchen nach Feather Falling
        for (RegistryEntry<Enchantment> entry : list) {
            if (entry.matchesKey(Enchantments.FEATHER_FALLING)) {
                CAPTURED_FEATHER_FALLING.set(entry);
                break;
            }
        }

        // Neuen Stream zurückgeben, damit die Originalmethode weiterarbeiten kann
        return list.stream();
    }

    // 2. Erlaubt, dass das Item überhaupt verzaubert werden darf (Amboss & Co)
    @Inject(method = "canHaveEnchantments", at = @At("HEAD"), cancellable = true)
    private static void allowHorseArmor(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (isHorseArmor(stack.getItem())) {
            cir.setReturnValue(true);
        }
    }

    // 3. Gaukeln dem Zaubertisch vor, dass das Item eine Enchantability von 15 hat.
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
        if (!isHorseArmor(stack.getItem())) {
            CAPTURED_FEATHER_FALLING.remove(); // Aufräumen, falls keine Pferderüstung
            return;
        }

        List<EnchantmentLevelEntry> list = cir.getReturnValue();

        System.out.println("Filter running for: " + stack.getItem());

        // FIX: Wir holen den zuvor abgefangenen Feather Falling Eintrag aus dem ThreadLocal
        RegistryEntry<Enchantment> featherFalling = CAPTURED_FEATHER_FALLING.get();

        // Wenn wir Feather Falling im Input-Stream gefunden haben und das Level > 0 ist...
        if (featherFalling != null && level > 0) {
            // Prüfen, ob es nicht eh schon drin ist
            boolean isPresent = list.stream().anyMatch(entry -> entry.enchantment().equals(featherFalling));

            if (!isPresent) {
                // Manuell hinzufügen
                list.add(new EnchantmentLevelEntry(featherFalling, 1));
            }
        }

        // Aufräumen
        CAPTURED_FEATHER_FALLING.remove();

        // Wir entfernen alles, was NICHT erlaubt ist
        list.removeIf(entry -> {
            RegistryKey<Enchantment> key = entry.enchantment().getKey().orElse(null);
            if (key == null) return false;

            // Erlaubte Liste:
            return !(key.equals(Enchantments.PROTECTION) ||
                    key.equals(Enchantments.BLAST_PROTECTION) ||
                    key.equals(Enchantments.FIRE_PROTECTION) ||
                    key.equals(Enchantments.PROJECTILE_PROTECTION) ||
                    key.equals(ModEnchantments.LEAPING) ||
                    key.equals(Enchantments.FEATHER_FALLING));
        });
    }

    // --- HILFSMETHODE (Das hat gefehlt) ---
    @Unique
    private static boolean isHorseArmor(Item item) {
        return item == Items.IRON_HORSE_ARMOR ||
                item == Items.GOLDEN_HORSE_ARMOR ||
                item == Items.DIAMOND_HORSE_ARMOR ||
                item == Items.LEATHER_HORSE_ARMOR; // Leder auch erlauben, falls gewünscht
    }
}