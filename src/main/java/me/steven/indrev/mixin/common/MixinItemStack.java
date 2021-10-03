package me.steven.indrev.mixin.common;

import com.google.common.collect.Multimap;
import me.steven.indrev.api.AttributeModifierProvider;
import me.steven.indrev.items.energy.IREnergyItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {

    @Shadow public abstract Item getItem();

    @Inject(method = "getAttributeModifiers", at = @At("TAIL"), cancellable = true)
    private void indrev_modifiableAttributeModifiers(EquipmentSlot equipmentSlot, CallbackInfoReturnable<Multimap<EntityAttribute, EntityAttributeModifier>> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (stack.getItem() instanceof AttributeModifierProvider) {
            cir.setReturnValue(((AttributeModifierProvider) stack.getItem()).getAttributeModifiers(stack, equipmentSlot));
        }
    }

}
