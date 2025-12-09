package com.simpleriding.mixin;

import com.simpleriding.Simpleriding;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @Inject(method = "isAcceptableItem", at = @At("HEAD"), cancellable = true)
    private void restrictHorseArmorGlobal(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        // Wir greifen nur ein, wenn es um Pferderüstung geht
        if (isHorseArmor(stack.getItem())) {

            // 'this' ist die Instanz der Verzauberung
            Enchantment self = (Enchantment) (Object) this;

            // Da wir hier keinen Zugriff auf die Registry-ID haben (dynamische Registry),
            // nutzen wir den Translation-Key der Beschreibung zur Identifizierung.
            // Das ist ein sicherer Weg, um Vanilla-Verzauberungen zu unterscheiden.
            String key = getEnchantmentTranslationKey(self);

            // Wenn wir den Key nicht lesen können, lassen wir es sicherheitshalber zu (oder verbieten es).
            if (key == null) return;

            // WHITELIST: Was ist erlaubt?
            boolean isAllowed =
                    key.contains("protection") ||       // Schutz, Schusswischer, Feuerschutz...
                            key.contains("feather_falling") ||  // Federfall
                            key.contains("unbreaking") ||       // Haltbarkeit
                            key.contains("thorns") ||           // Dornen
                            key.contains("mending") ||          // Reparatur
                            key.contains(Simpleriding.MOD_ID);  // Unsere eigenen Mods (Tailwind, Leaping)

            // Wenn es NICHT erlaubt ist (z.B. depth_strider, soul_speed, frost_walker), verbieten wir es hart.
            if (!isAllowed) {
                cir.setReturnValue(false);
            }
        }
    }

    @Unique
    private String getEnchantmentTranslationKey(Enchantment enchantment) {
        Text description = enchantment.description();
        if (description.getContent() instanceof TranslatableTextContent translatable) {
            return translatable.getKey(); // z.B. "enchantment.minecraft.depth_strider"
        }
        return null;
    }

    @Unique
    private boolean isHorseArmor(Item item) {
        return item == Items.LEATHER_HORSE_ARMOR ||
                item == Items.IRON_HORSE_ARMOR ||
                item == Items.GOLDEN_HORSE_ARMOR ||
                item == Items.DIAMOND_HORSE_ARMOR;
    }
}