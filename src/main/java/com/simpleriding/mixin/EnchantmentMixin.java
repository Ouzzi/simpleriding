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
        if (isHorseArmor(stack.getItem())) {

            Enchantment self = (Enchantment) (Object) this;

            String key = getEnchantmentTranslationKey(self);

            if (key == null) return;

            // WHITELIST: Was ist erlaubt?
            boolean isAllowed =
                key.contains("protection") ||
                key.contains("feather_falling") ||
                key.contains("unbreaking") ||
                key.contains("thorns") ||
                key.contains("mending") ||
                key.contains(Simpleriding.MOD_ID);

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