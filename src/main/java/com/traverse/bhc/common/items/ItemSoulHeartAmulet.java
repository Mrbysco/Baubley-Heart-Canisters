package com.traverse.bhc.common.items;

import com.traverse.bhc.common.BaubleyHeartCanisters;
import com.traverse.bhc.common.container.SoulHeartAmuletContainer;
import com.traverse.bhc.common.datacomponent.HeartAmount;
import com.traverse.bhc.common.init.RegistryHandler;
import com.traverse.bhc.common.util.HeartType;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.List;
import java.util.Map;

import static com.traverse.bhc.common.util.HealthModifier.updatePlayerHealth;

public class ItemSoulHeartAmulet extends BaseItem implements MenuProvider, ICurioItem {

    public ItemSoulHeartAmulet() {
        super(1);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND)
            return InteractionResultHolder.fail(player.getItemInHand(hand));

        if (!level.isClientSide() && !player.isShiftKeyDown()) {
            player.openMenu(this, friendlyByteBuf -> ItemStack.STREAM_CODEC.encode(friendlyByteBuf, player.getItemInHand(hand)));
        }

        return super.use(level, player, hand);
    }

    public Map<Integer, Integer> getHeartCount(ItemStack stack) {
        if (stack.has(RegistryHandler.HEART_AMOUNT_COMPONENT)) {
            return stack.get(RegistryHandler.HEART_AMOUNT_COMPONENT).slotToAmount();
        }
        return HeartAmount.createSizedMap(HeartType.values().length);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.bhc.soul_heart_amulet");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        InteractionHand hand = getHandForAmulet(player);
        return new SoulHeartAmuletContainer(id, inventory, hand != null ? player.getItemInHand(hand) : ItemStack.EMPTY);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(Util.makeDescriptionId("tooltip", ResourceLocation.fromNamespaceAndPath(BaubleyHeartCanisters.MODID, "heartamulet"))).setStyle(Style.EMPTY.applyFormat(ChatFormatting.GOLD)));
    }

    public static InteractionHand getHandForAmulet(Player player) {
        if (player.getMainHandItem().getItem() == RegistryHandler.SOUL_HEART_AMULET.get())
            return InteractionHand.MAIN_HAND;
        else if (player.getOffhandItem().getItem() == RegistryHandler.SOUL_HEART_AMULET.get())
            return InteractionHand.OFF_HAND;

        return null;
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        LivingEntity livingEntity = slotContext.entity();
        if(livingEntity instanceof Player player) {
            ICuriosItemHandler handler = CuriosApi.getCuriosInventory(livingEntity).orElse(null);
            if (handler == null) return;
            SlotResult equipped = handler.findFirstCurio(RegistryHandler.SOUL_HEART_AMULET.get()).orElse(null);
            if (equipped != null) {
                updatePlayerHealth(player, equipped.stack(), true);
            }
        }
    }


    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (slotContext.entity() instanceof Player player) {
            updatePlayerHealth((Player) player, ItemStack.EMPTY, false);
        }
    }

}
