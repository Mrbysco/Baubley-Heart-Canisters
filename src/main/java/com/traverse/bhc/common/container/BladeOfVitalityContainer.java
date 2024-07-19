package com.traverse.bhc.common.container;

import com.traverse.bhc.common.config.ConfigHandler;
import com.traverse.bhc.common.datacomponent.HeartAmount;
import com.traverse.bhc.common.init.RegistryHandler;
import com.traverse.bhc.common.items.BaseHeartCanister;
import com.traverse.bhc.common.util.InventoryUtil;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.TreeMap;

public class BladeOfVitalityContainer extends AbstractContainerMenu {
    public ItemStackHandler itemStackHandler;

    public BladeOfVitalityContainer(int windowId, Inventory playerInventory, ItemStack stack) {
        super(RegistryHandler.BLADE_OF_VITALITY_CONTAINER.get(), windowId);
        this.itemStackHandler = InventoryUtil.createVirtualInventory(4, stack);


        //Heart Container Slots
        this.addSlot(new BladeOfVitalityContainer.SlotPendant(this.itemStackHandler, 0, 80, 5));//RED
        this.addSlot(new BladeOfVitalityContainer.SlotPendant(this.itemStackHandler, 1, 80, 25));//YELLOW
        this.addSlot(new BladeOfVitalityContainer.SlotPendant(this.itemStackHandler, 2, 80, 45));//GREEN
        this.addSlot(new BladeOfVitalityContainer.SlotPendant(this.itemStackHandler, 3, 80, 65));//BLUE

        //Add player inventory slots
        for (int row = 0; row < 9; ++row) {
            int x = 8 + row * 18;
            int y = 56 + 86;
            if (row == getSlotFor(playerInventory, stack)) {
                addSlot(new BladeOfVitalityContainer.LockedSlot(playerInventory, row, x, y));
                continue;
            }

            addSlot(new Slot(playerInventory, row, x, y));
        }

        for (int row = 1; row < 4; ++row) {
            for (int col = 0; col < 9; ++col) {
                int x = 8 + col * 18;
                int y = row * 18 + (56 + 10);
                addSlot(new Slot(playerInventory, col + row * 9, x, y));
            }
        }
    }

    @Override
    public void removed(Player playerIn) {
        ItemStack sword = playerIn.getMainHandItem();
        InventoryUtil.serializeInventory(this.itemStackHandler, sword);



        Map<Integer, Integer> hearts = new TreeMap<>();
        for (int i = 0; i < this.itemStackHandler.getSlots(); i++) {
            ItemStack stack = this.itemStackHandler.getStackInSlot(i);
            if (!stack.isEmpty()) hearts.put(i, (stack.getCount() * 2));
        }
        sword.set(RegistryHandler.HEART_AMOUNT_COMPONENT, new HeartAmount(hearts));

        super.removed(playerIn);
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            stack = slotStack.copy();
            if (index < this.itemStackHandler.getSlots()) {
                if (!this.moveItemStackTo(slotStack, this.itemStackHandler.getSlots(), this.slots.size(), true))
                    ;
                return ItemStack.EMPTY;
            } else if (!this.moveItemStackTo(slotStack, 0, this.itemStackHandler.getSlots(), false)) {
                return ItemStack.EMPTY;
            }
            if (slotStack.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return stack;
    }


    private static class SlotPendant extends SlotItemHandler {
        public SlotPendant(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return super.mayPlace(stack) && stack.getItem() instanceof BaseHeartCanister && ((BaseHeartCanister) stack.getItem()).type.ordinal() == this.getSlotIndex();
        }

        @Override
        public int getMaxStackSize() {
            return ConfigHandler.general.heartStackSize.get();
        }
    }

    private static class LockedSlot extends Slot {

        public LockedSlot(Inventory inventoryIn, int index, int xPosition, int yPosition) {
            super((Container) inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public boolean mayPickup(Player playerIn) {
            return false;
        }
    }

    public int getSlotFor(Inventory inventory, ItemStack stack) {
        for (int i = 0; i < inventory.items.size(); ++i) {
            if (!inventory.items.get(i).isEmpty() && stackEqualExact(stack, inventory.items.get(i))) {
                return i;
            }
        }

        return -1;
    }

    private boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && ItemStack.isSameItem(stack1, stack2);
    }
}
