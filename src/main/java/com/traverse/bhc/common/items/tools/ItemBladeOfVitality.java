package com.traverse.bhc.common.items.tools;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.traverse.bhc.common.BaubleyHeartCanisters;
import com.traverse.bhc.common.container.BladeOfVitalityContainer;
import com.traverse.bhc.common.container.HeartAmuletContainer;
import com.traverse.bhc.common.datacomponent.HeartAmount;
import com.traverse.bhc.common.init.RegistryHandler;
import com.traverse.bhc.common.util.HeartType;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

public class ItemBladeOfVitality extends SwordItem implements MenuProvider {

    public static final UUID DAMAGE_MODIFIER_ID = UUID.fromString("432ba3b0-c3bd-4f1c-b14c-76a0b32a386c");


    //ToDo: make an actual Tier for Blade of Vitality Easier to Customize
    public ItemBladeOfVitality() {
        super(Tiers.NETHERITE, new Item.Properties().attributes(SwordItem.createAttributes(Tiers.NETHERITE, 3, -2.4F)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND)
            return InteractionResultHolder.fail(player.getItemInHand(hand));

        if (!level.isClientSide() && player.isShiftKeyDown()) {
            player.openMenu(this, friendlyByteBuf -> ItemStack.STREAM_CODEC.encode(friendlyByteBuf, player.getItemInHand(hand)));
        }

        return super.use(level, player, hand);
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return false;
    }

//    @Override TODO: Figure out the attribute modifier!
//    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
//        ImmutableMultimap.Builder<Attribute, AttributeModifier> RESULT = ImmutableMultimap.builder();
//        RESULT.putAll(super.getAttributeModifiers(slot, stack));
//        if(slot == EquipmentSlot.MAINHAND) {
//            int[] heartCount = getHeartCount(stack);
//            int heartTotal = IntStream.of(heartCount).sum();
//            if (heartTotal > 0) {
//                RESULT.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(DAMAGE_MODIFIER_ID, "Weapon modifier", heartTotal, AttributeModifier.Operation.ADDITION));
//            }
//        }
//        return RESULT.build();
//    }

    public Map<Integer, Integer> getHeartCount(ItemStack stack) {
        if (stack.has(RegistryHandler.HEART_AMOUNT_COMPONENT)) {
            return stack.get(RegistryHandler.HEART_AMOUNT_COMPONENT).slotToAmount();
        }
        return HeartAmount.createSizedMap(HeartType.values().length);
    }

    //ToDo Actually check the length of the Hearts on the Weapon and Add to the damage
    @Override
    public int getDamage(ItemStack stack) {
        return this.getMaxDamage(RegistryHandler.BLADE_OF_VITALITY.get().getDefaultInstance()) + HeartAmuletContainer.HEART_AMOUNT.length();
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

//    @Override TODO: Check if isDamageable is needed
//    public boolean canBeDepleted() {
//        return false;
//    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.bhc.blade_of_vitality");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new BladeOfVitalityContainer(id, inventory, player.getMainHandItem());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(Util.makeDescriptionId("tooltip", ResourceLocation.fromNamespaceAndPath(BaubleyHeartCanisters.MODID, "vitality_blade"))).setStyle(Style.EMPTY.applyFormat(ChatFormatting.GOLD)));
    }

//    @Override
//    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
//        return true;
//    }
}
