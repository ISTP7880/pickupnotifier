package com.fuzs.pickupnotifier.handler;

import com.fuzs.pickupnotifier.util.DisplayEntry;
import com.fuzs.pickupnotifier.util.ExperienceDisplayEntry;
import com.fuzs.pickupnotifier.util.ItemDisplayEntry;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Optional;

public class AddEntriesHandler {

    static final List<DisplayEntry> PICK_UPS = Lists.newArrayList();

    // accessed by asm transformer bundled with this mod
    @SuppressWarnings("unused")
    public static void onEntityPickup(Entity entity, EntityLivingBase livingentity) {

        if (livingentity instanceof EntityPlayerSP) {
            if (entity instanceof EntityItem) {
                addItemEntry(((EntityItem) entity).getItem());
            } else if (entity instanceof EntityXPOrb) {
                addExperienceEntry((EntityXPOrb) entity);
            }
        }

    }

    private static void addItemEntry(ItemStack stack) {

        ResourceLocation resourcelocation = ForgeRegistries.ITEMS.getKey(stack.getItem());
        boolean blacklisted = resourcelocation != null && (ConfigBuildHandler.GENERAL_CONFIG.blacklist.get().contains(resourcelocation.toString())
                || ConfigBuildHandler.GENERAL_CONFIG.blacklist.get().contains(resourcelocation.getNamespace()));

        if (!stack.isEmpty() && stack.getCount() > 0 && !blacklisted) {
            addEntry(new ItemDisplayEntry(stack));
        }

    }

    private static void addExperienceEntry(EntityXPOrb orb) {

        if (ConfigBuildHandler.GENERAL_CONFIG.displayExperience.get() && orb.xpValue > 0) {
            addEntry(new ExperienceDisplayEntry(orb));
        }

    }

    private static void addEntry(DisplayEntry entry) {

        synchronized (PICK_UPS) {
            float scale = ConfigBuildHandler.DISPLAY_CONFIG.scale.get() / 6.0F;
            int scaledHeight = (int) (Minecraft.getInstance().mainWindow.getScaledHeight() / scale);
            int length = (int) (scaledHeight * ConfigBuildHandler.DISPLAY_CONFIG.height.get().floatValue() / DisplayEntry.HEIGHT) - 1;

            Optional<DisplayEntry> displayEntryOptional = ConfigBuildHandler.GENERAL_CONFIG.combineEntries.get() ? PICK_UPS
                    .stream().filter(it -> it.canCombine(entry)).findFirst() : Optional.empty();
            if (displayEntryOptional.isPresent()) {
                DisplayEntry duplicateEntry = displayEntryOptional.get();
                duplicateEntry.addCount(entry.getCount());
                duplicateEntry.resetLife();
                // adding back to the end of the list
                PICK_UPS.remove(duplicateEntry);
                PICK_UPS.add(duplicateEntry);
            } else {
                if (PICK_UPS.size() >= length) {
                    PICK_UPS.remove(0);
                }
                PICK_UPS.add(entry);
            }
        }

    }

}