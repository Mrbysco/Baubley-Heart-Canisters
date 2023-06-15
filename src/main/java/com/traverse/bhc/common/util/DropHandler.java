package com.traverse.bhc.common.util;

import com.traverse.bhc.common.BaubleyHeartCanisters;
import com.traverse.bhc.common.config.ConfigHandler;
import com.traverse.bhc.common.init.RegistryHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Mod.EventBusSubscriber(modid = BaubleyHeartCanisters.MODID)
public class DropHandler {

    @SubscribeEvent
    public static void onEntityDrop(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide || entity instanceof Player) return;

        if (!ModList.get().isLoaded("tinkersconstruct") && entity instanceof WitherSkeleton) {
            if (entity.level().random.nextDouble() < ConfigHandler.general.boneDropRate.get()) {
                entity.spawnAtLocation(RegistryHandler.WITHER_BONE.get(), 1);
            }
        }

        for (ItemStack stack : getEntityDrops(entity)) {
            entity.spawnAtLocation(stack.getItem(), 0);
        }
    }

    public static List<ItemStack> getEntityDrops(LivingEntity entity) {
        List<ItemStack> list = new ArrayList<>();
        handleEntry("red",entity,list);
        handleEntry("yellow",entity,list);
        handleEntry("green",entity,list);
        handleEntry("blue",entity,list);
        return list;
    }

    public static void handleEntry(String category, LivingEntity entity, List<ItemStack> items) {
        for (Map.Entry<String, Double> entry : BaubleyHeartCanisters.config.getHeartTypeEntries(category).entrySet()) {
            ItemStack stack = ItemStack.EMPTY;
            switch (category) {
                case "red":
                    stack = new ItemStack(RegistryHandler.RED_HEART.get());
                    break;
                case "yellow":
                    stack = new ItemStack(RegistryHandler.YELLOW_HEART.get());
                    break;
                case "green":
                    stack = new ItemStack(RegistryHandler.GREEN_HEART.get());
                    break;
                case "blue":
                    stack = new ItemStack(RegistryHandler.BLUE_HEART.get());
                    break;
            }

            if (entry.getKey().equals(entity.getEncodeId())) {
                addWithPercent(items, stack, entry.getValue());
            } else {
                switch (entry.getKey()) {
                    case "passive":
                        if((!(entity instanceof Monster) && !(entity instanceof Player))) {
                            addWithPercent(items, stack, entry.getValue());
                        }
                        break;
                    case "hostile":
                        if (entity instanceof Monster && entity.canChangeDimensions() && !(entity instanceof Warden)) {
                            addWithPercent(items, stack, entry.getValue());
                        }
                        break;
                    case "boss":
                        if (!entity.canChangeDimensions() && !(entity instanceof EnderDragon)) {
                            addWithPercent(items, stack, entry.getValue());
                        }
                        break;
                    case "dragon":
                        if (entity instanceof EnderDragon) {
                            addWithPercent(items, stack, entry.getValue());
                        }
                        break;
                }
            }
        }
    }

    public static void addWithPercent(List<ItemStack> list, ItemStack stack, double percentage) {
        Random random = new Random();
        int percent = (int) (percentage * 100);
        if (random.nextInt(100) < percent) {
            list.add(stack);
        }
    }
}
